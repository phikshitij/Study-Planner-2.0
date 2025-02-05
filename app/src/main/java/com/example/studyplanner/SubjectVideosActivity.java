package com.example.studyplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class SubjectVideosActivity extends AppCompatActivity {
    private static final String TAG = "SubjectVideosActivity";
    private LinearLayout videoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_subject_videos);

            // Setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            videoContainer = findViewById(R.id.videoContainer);
            if (videoContainer == null) {
                Log.e(TAG, "Video container not found");
                Toast.makeText(this, "Error initializing video player", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Get the subject from intent
            String subject = getIntent().getStringExtra("subject");
            if (subject == null) {
                Log.e(TAG, "No subject provided");
                Toast.makeText(this, "Error: No subject selected", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Set title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(subject + " Videos");
            }

            // Add videos based on the selected subject
            switch(subject) {
                case "Software Engineering":
                    addVideo("CYKout9ViX4");

                    break;
                case "Computer Networks":
                    addVideo("b6U487URsGAs");

                    break;
                case "Theoretical Computer Science":
                    addVideo("tpUXwTssiTU");

                    break;
                case "Internet Programming":
                    addVideo("y2m4d0G3GZc");

                    break;
                case "Data Warehouse and Mining":
                    addVideo("Kg5i1U_Y8ew");

                    break;
                default:
                    Log.e(TAG, "Unknown subject: " + subject);
                    Toast.makeText(this, "Error: Invalid subject", Toast.LENGTH_SHORT).show();
                    finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Error initializing activity", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void addVideo(String videoId) {
        try {
            // Create YouTube Player View
            YouTubePlayerView youTubePlayerView = new YouTubePlayerView(this);
            getLifecycle().addObserver(youTubePlayerView);

            // Calculate video height (16:9 aspect ratio)
            int screenWidth = getResources().getDisplayMetrics().widthPixels - 
                             (2 * getResources().getDimensionPixelSize(R.dimen.video_margin));
            int videoHeight = (screenWidth * 9) / 16;

            // Set layout parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    videoHeight
            );
            params.setMargins(
                    getResources().getDimensionPixelSize(R.dimen.video_margin),
                    getResources().getDimensionPixelSize(R.dimen.video_margin),
                    getResources().getDimensionPixelSize(R.dimen.video_margin),
                    getResources().getDimensionPixelSize(R.dimen.video_margin)
            );
            youTubePlayerView.setLayoutParams(params);

            // Initialize player
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    try {
                        youTubePlayer.cueVideo(videoId, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading video: " + e.getMessage());
                        Toast.makeText(SubjectVideosActivity.this, 
                                     "Error loading video", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Add to container
            videoContainer.addView(youTubePlayerView);

        } catch (Exception e) {
            Log.e(TAG, "Error adding video: " + e.getMessage());
            Toast.makeText(this, "Error adding video player", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        try {
            // Clean up video players
            if (videoContainer != null) {
                for (int i = 0; i < videoContainer.getChildCount(); i++) {
                    View view = videoContainer.getChildAt(i);
                    if (view instanceof YouTubePlayerView) {
                        YouTubePlayerView player = (YouTubePlayerView) view;
                        player.release();
                    }
                }
            }
            super.onDestroy();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
            super.onDestroy();
        }
    }
}
