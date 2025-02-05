package com.example.studyplanner;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.example.studyplanner.utils.NetworkUtils;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiKey;
    private final Context context;

    public GeminiService(Context context) {
        this.context = context;
        this.apiKey = context.getString(R.string.gemini_api_key);

        if (TextUtils.isEmpty(apiKey)) {
            throw new IllegalStateException("Gemini API key not found");
        }

        Log.d(TAG, "Initializing GeminiService with API key length: " + apiKey.length());

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        this.client = builder.build();
    }

    public interface ChatCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public void generateResponse(String userMessage, ChatCallback callback) {
        if (TextUtils.isEmpty(userMessage)) {
            callback.onError("Message cannot be empty");
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("No internet connection available");
            return;
        }

        try {
            HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                    .addQueryParameter("key", apiKey)
                    .build();

            // Create request body
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject textPart = new JSONObject();
            textPart.put("text", userMessage);
            parts.put(textPart);
            content.put("parts", parts);
            contents.put(content);
            jsonBody.put("contents", contents);
            jsonBody.put("generationConfig", new JSONObject()
                    .put("temperature", 0.7)
                    .put("maxOutputTokens", 800));

            String requestBody = jsonBody.toString();
            Log.d(TAG, "Request body: " + requestBody);

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(JSON, requestBody))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String errorMessage = "Network error: " + e.getMessage();
                    Log.e(TAG, errorMessage, e);
                    callback.onError(errorMessage);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful() || responseBody == null) {
                            String error = responseBody != null ? responseBody.string() : "Unknown error";
                            Log.e(TAG, "API error: " + response.code() + " " + error);
                            callback.onError("Error: " + response.code() + ". " + error);
                            return;
                        }

                        String jsonResponse = responseBody.string();
                        Log.d(TAG, "Raw response: " + jsonResponse);

                        JSONObject responseJson = new JSONObject(jsonResponse);
                        JSONObject candidate = responseJson.getJSONArray("candidates").getJSONObject(0);
                        JSONObject content = candidate.getJSONObject("content");
                        String text = content.getJSONArray("parts").getJSONObject(0).getString("text");

                        callback.onResponse(text.trim());
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
