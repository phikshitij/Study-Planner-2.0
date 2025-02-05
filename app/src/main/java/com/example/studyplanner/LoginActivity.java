package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            dbHelper = new DatabaseHelper(this);
            
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
            loginButton = findViewById(R.id.loginButton);
            registerLink = findViewById(R.id.registerLink);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String email = emailInput.getText().toString().trim();
                        String password = passwordInput.getText().toString().trim();

                        // Validate input fields
                        if (!validateInputs(email, password)) {
                            return;
                        }

                        // Check user credentials
                        if (dbHelper.checkUser(email, password)) {
                            Log.d(TAG, "Login successful for user: " + email);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "Login failed for user: " + email);
                            Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error during login: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            registerLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to register: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error initializing LoginActivity: " + e.getMessage());
            Toast.makeText(this, "An error occurred. Please restart the app.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
