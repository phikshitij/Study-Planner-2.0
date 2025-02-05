package com.example.studyplanner;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.example.studyplanner.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLHandshakeException;

public class ChatGPTService {
    private static final String TAG = "ChatGPTService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private static final int MAX_REQUESTS_PER_MINUTE = 3;
    
    private final OkHttpClient client;
    private final String apiKey;
    private final Gson gson;
    private final Context context;
    
    // Rate limiting
    private static final AtomicInteger requestCount = new AtomicInteger(0);
    private static long lastResetTime = System.currentTimeMillis();

    public ChatGPTService(Context context) {
        this.context = context;
        this.apiKey = context.getString(R.string.openai_api_key);
        
        if (TextUtils.isEmpty(apiKey)) {
            Log.e(TAG, "Invalid API key configuration");
            throw new IllegalStateException("Invalid API key configuration");
        }

        Log.d(TAG, "Initializing ChatGPTService with API URL: " + API_URL);

        ConnectionPool connectionPool = new ConnectionPool(5, 30, TimeUnit.SECONDS);
        
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Add logging interceptor
        builder.addInterceptor(chain -> {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Log.d(TAG, String.format("Sending request to: %s", request.url()));
            Log.d(TAG, String.format("Request headers: %s", request.headers()));
            
            try {
                Response response = chain.proceed(request);
                long endTime = System.currentTimeMillis();
                Log.d(TAG, String.format("Received response for %s in %.1fs", response.request().url(), (endTime - startTime) / 1000.0));
                Log.d(TAG, String.format("Response code: %d", response.code()));
                return response;
            } catch (Exception e) {
                Log.e(TAG, "Error during request: " + e.getMessage(), e);
                throw e;
            }
        });

        this.client = builder.build();
        this.gson = new Gson();
    }

    private boolean checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastResetTime > TimeUnit.MINUTES.toMillis(1)) {
            requestCount.set(0);
            lastResetTime = currentTime;
        }
        
        return requestCount.get() < MAX_REQUESTS_PER_MINUTE;
    }

    public interface ChatGPTCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public void generateResponse(String userMessage, ChatGPTCallback callback) {
        if (TextUtils.isEmpty(userMessage)) {
            callback.onError("Message cannot be empty");
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection available. Please check your network settings.");
            return;
        }

        if (!checkRateLimit()) {
            callback.onError("Please wait a moment before sending another message. (Rate limit: " + MAX_REQUESTS_PER_MINUTE + " messages per minute)");
            return;
        }

        requestCount.incrementAndGet();

        try {
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", "You are a helpful study assistant. Provide clear, concise answers focused on academic topics.");

            JsonObject userMessageObj = new JsonObject();
            userMessageObj.addProperty("role", "user");
            userMessageObj.addProperty("content", userMessage);

            JsonObject[] messages = new JsonObject[]{systemMessage, userMessageObj};

            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("model", "gpt-3.5-turbo");
            jsonBody.add("messages", gson.toJsonTree(messages));
            jsonBody.addProperty("temperature", 0.7);
            jsonBody.addProperty("max_tokens", 500);

            String requestBody = jsonBody.toString();
            Log.d(TAG, "Request body: " + requestBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey.trim())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            Log.d(TAG, "Making API request to: " + request.url());
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API call failed", e);
                    String errorMessage;
                    if (e instanceof SocketTimeoutException) {
                        errorMessage = "Request timed out. Please try again.";
                    } else if (e instanceof UnknownHostException) {
                        errorMessage = "Cannot reach the server. Please check your internet connection.";
                    } else if (e instanceof SSLHandshakeException) {
                        errorMessage = "Secure connection failed. Please try again.";
                    } else {
                        errorMessage = "Network error: " + e.getMessage();
                    }
                    callback.onError(errorMessage);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()) {
                            String errorBody = responseBody != null ? responseBody.string() : "Unknown error";
                            Log.e(TAG, "API error: " + response.code() + " " + errorBody);
                            
                            String errorMessage;
                            switch (response.code()) {
                                case 401:
                                    errorMessage = "Authentication failed. Please check your API key.";
                                    break;
                                case 429:
                                    if (errorBody.contains("insufficient_quota")) {
                                        errorMessage = "API quota exceeded. Please check your OpenAI account.";
                                    } else {
                                        errorMessage = "Too many requests. Please wait a moment before trying again.";
                                    }
                                    break;
                                case 500:
                                case 502:
                                case 503:
                                case 504:
                                    errorMessage = "OpenAI servers are currently busy. Please try again in a few minutes.";
                                    break;
                                default:
                                    errorMessage = "Error: " + response.code() + ". Please try again.";
                            }
                            callback.onError(errorMessage);
                            return;
                        }

                        if (responseBody == null) {
                            callback.onError("Received empty response from server");
                            return;
                        }

                        String jsonResponse = responseBody.string();
                        Log.d(TAG, "Response: " + jsonResponse);
                        
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONArray choices = jsonObject.getJSONArray("choices");
                        
                        if (choices.length() == 0) {
                            callback.onError("No response generated. Please try again.");
                            return;
                        }

                        JSONObject firstChoice = choices.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        String content = message.getString("content");
                        
                        if (content.trim().isEmpty()) {
                            callback.onError("Received empty response. Please try again.");
                            return;
                        }

                        callback.onResponse(content.trim());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        callback.onError("Error processing response: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error generating request", e);
            callback.onError("Error preparing request: " + e.getMessage());
        }
    }
}
