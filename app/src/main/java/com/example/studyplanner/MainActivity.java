package com.example.studyplanner;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity 
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private TextView timerText;
    private EditText hoursInput, minutesInput, secondsInput;
    private Button startTimerButton;
    private Button stopTimerButton;
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dbHelper = new DatabaseHelper(this);
            userEmail = getIntent().getStringExtra("email");
            if (userEmail == null) {
                Log.e(TAG, "No email provided in intent");
                Toast.makeText(this, "Error: User email not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            } else {
                Log.e(TAG, "Toolbar not found in layout");
            }

            // Setup navigation drawer
            setupNavigation(toolbar);

            // Setup timer views
            setupTimerViews();

            // Setup card clicks
            setupCardClicks();

            // Initialize MediaPlayer for notification sound
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI);

            // Setup chat button
            setupChatButton();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "An error occurred while starting the app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupNavigation(Toolbar toolbar) {
        try {
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            
            if (drawerLayout == null || navigationView == null) {
                Log.e(TAG, "Navigation drawer views not found");
                return;
            }

            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
            
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

        } catch (Exception e) {
            Log.e(TAG, "Error setting up navigation drawer: " + e.getMessage());
        }
    }

    private void setupTimerViews() {
        try {
            timerText = findViewById(R.id.timerText);
            hoursInput = findViewById(R.id.hoursInput);
            minutesInput = findViewById(R.id.minutesInput);
            secondsInput = findViewById(R.id.secondsInput);
            startTimerButton = findViewById(R.id.startTimerButton);
            stopTimerButton = findViewById(R.id.stopTimerButton);

            if (startTimerButton != null) {
                startTimerButton.setOnClickListener(v -> startTimer());
            }

            if (stopTimerButton != null) {
                stopTimerButton.setOnClickListener(v -> stopTimer());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error setting up timer views: " + e.getMessage());
        }
    }

    private void setupCardClicks() {
        try {
            CardView timetableCard = findViewById(R.id.timetableCard);
            CardView resourcesCard = findViewById(R.id.resourcesCard);
            CardView quizCard = findViewById(R.id.quizCard);
            CardView videoResourcesCard = findViewById(R.id.videoResourcesCard);

            if (timetableCard != null) {
                timetableCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
                    intent.putExtra("email", userEmail);
                    startActivity(intent);
                });
            }

            if (resourcesCard != null) {
                resourcesCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ResourcesActivity.class);
                    startActivity(intent);
                });
            }

            if (quizCard != null) {
                quizCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, QuizSubjectsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            } else {
                Log.e(TAG, "Quiz card not found in layout");
            }

            if (videoResourcesCard != null) {
                videoResourcesCard.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, VideoResourcesActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupCardClicks: " + e.getMessage());
        }
    }

    private void setupChatButton() {
        FloatingActionButton chatButton = findViewById(R.id.chatbotButton);
        if (chatButton != null) {
            chatButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
            });
        }
    }

    private long getTimeInMillis() {
        int hours = TextUtils.isEmpty(hoursInput.getText()) ? 0 : 
                   Integer.parseInt(hoursInput.getText().toString());
        int minutes = TextUtils.isEmpty(minutesInput.getText()) ? 0 : 
                     Integer.parseInt(minutesInput.getText().toString());
        int seconds = TextUtils.isEmpty(secondsInput.getText()) ? 0 : 
                     Integer.parseInt(secondsInput.getText().toString());

        if (hours == 0 && minutes == 0 && seconds == 0) {
            return -1;
        }

        return (hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L);
    }

    private void startTimer() {
        if (!timerRunning) {
            long timeInMillis = getTimeInMillis();
            
            if (timeInMillis == -1) {
                Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_SHORT).show();
                return;
            }

            countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long hours = (millisUntilFinished / 3600000);
                    long minutes = (millisUntilFinished % 3600000) / 60000;
                    long seconds = (millisUntilFinished % 60000) / 1000;
                    timerText.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }

                @Override
                public void onFinish() {
                    timerText.setText("00:00:00");
                    timerRunning = false;
                    // Play notification sound
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    Toast.makeText(MainActivity.this, "Study session completed!", Toast.LENGTH_LONG).show();
                }
            }.start();
            timerRunning = true;
        }
    }

    private void stopTimer() {
        if (timerRunning && countDownTimer != null) {
            countDownTimer.cancel();
            timerRunning = false;
            timerText.setText("00:00:00");
            // Clear input fields
            hoursInput.setText("");
            minutesInput.setText("");
            secondsInput.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                // Handle logout
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling navigation item selection: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
