package com.example.studyplanner;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class QuizSubjectsActivity extends AppCompatActivity {
    private static final String TAG = "QuizSubjectsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_subjects);

        // Setup toolbar
        setupToolbar();

        // Setup quiz cards with animations
        setupQuizCards();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Quiz Section");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupQuizCards() {
        // Find all card views
        CardView[] cards = {
            findViewById(R.id.cardSoftwareEngineering),
            findViewById(R.id.cardComputerNetworks),
            findViewById(R.id.cardTheoretical),
            findViewById(R.id.cardInternetProgramming),
            findViewById(R.id.cardDataWarehouse)
        };

        String[] subjects = {
            "Software Engineering",
            "Computer Networks",
            "Theoretical Computer Science",
            "Internet Programming",
            "Data Warehouse and Mining"
        };

        // Load animations
        Animation fallDownAnim = AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down);
        Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.item_animation_click);

        // Add animation to each card with delay
        for (int i = 0; i < cards.length; i++) {
            final CardView card = cards[i];
            final String subject = subjects[i];
            
            // Set initial visibility
            card.setVisibility(View.VISIBLE);
            card.setAlpha(0f);
            
            // Apply fall down animation with delay and fade in
            card.postDelayed(() -> {
                card.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
                card.startAnimation(fallDownAnim);
            }, i * 100); // 100ms delay between each card

            // Set click listener with animation and ripple effect
            card.setOnClickListener(v -> {
                v.startAnimation(clickAnim);
                handleQuizSelection(subject);
            });
        }
    }

    private void handleQuizSelection(String subject) {
        // Show a toast message
        Toast.makeText(this, 
            subject + " Quiz Coming Soon!", 
            Toast.LENGTH_SHORT
        ).show();
        
        // TODO: Implement actual quiz functionality
        // Intent intent = new Intent(this, QuizActivity.class);
        // intent.putExtra("subject", subject);
        // startActivity(intent);
        // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
