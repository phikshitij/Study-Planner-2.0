package com.example.studyplanner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studyplanner.database.DatabaseHelper;
import com.example.studyplanner.models.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private ProgressBar progressBar;
    private GeminiService geminiService;
    private DatabaseHelper databaseHelper;
    private Handler mainHandler;
    private boolean isGeneratingResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize Gemini service and handler
        geminiService = new GeminiService(this);
        mainHandler = new Handler(Looper.getMainLooper());

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("AI Study Assistant");
        }

        // Initialize views
        recyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);

        // Load existing messages
        loadChatHistory();

        // Setup send button
        sendButton.setOnClickListener(v -> {
            if (!isGeneratingResponse) {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                }
            } else {
                Toast.makeText(this, "Please wait for the previous response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatHistory() {
        List<ChatMessage> messages = databaseHelper.getAllMessages();
        for (ChatMessage message : messages) {
            chatAdapter.addMessage(message);
        }
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private void sendMessage(String message) {
        // Add and save user message
        ChatMessage userMessage = new ChatMessage(message, true);
        chatAdapter.addMessage(userMessage);
        databaseHelper.insertMessage(userMessage);

        messageInput.setText("");
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Show progress
        isGeneratingResponse = true;
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        Log.d("ChatActivity", "Sending message to Gemini: " + message);

        // Generate response using Gemini
        geminiService.generateResponse(message, new GeminiService.ChatCallback() {
            @Override
            public void onResponse(final String response) {
                Log.d("ChatActivity", "Received response from Gemini: " + response);
                mainHandler.post(() -> {
                    ChatMessage botMessage = new ChatMessage(response, false);
                    chatAdapter.addMessage(botMessage);
                    databaseHelper.insertMessage(botMessage);
                    hideProgress();
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                });
            }

            @Override
            public void onError(final String error) {
                Log.e("ChatActivity", "Error from Gemini: " + error);
                mainHandler.post(() -> {
                    ChatMessage errorMessage = new ChatMessage("Error: " + error, false);
                    chatAdapter.addMessage(errorMessage);
                    databaseHelper.insertMessage(errorMessage);
                    hideProgress();
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void hideProgress() {
        isGeneratingResponse = false;
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database connection
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        // Clean up any pending operations
        mainHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
