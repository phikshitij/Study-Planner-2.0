package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

public class VideoResourcesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_resources);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Video Resources");

        // Initialize subject cards
        CardView softwareEngineeringCard = findViewById(R.id.softwareEngineeringCard);
        CardView computerNetworksCard = findViewById(R.id.computerNetworksCard);
        CardView theoreticalComputerScienceCard = findViewById(R.id.theoreticalComputerScienceCard);
        CardView internetProgrammingCard = findViewById(R.id.internetProgrammingCard);
        CardView dataWarehouseCard = findViewById(R.id.dataWarehouseCard);

        // Set click listeners for each subject
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = "";
                if (v.getId() == R.id.softwareEngineeringCard) {
                    subject = "Software Engineering";
                } else if (v.getId() == R.id.computerNetworksCard) {
                    subject = "Computer Networks";
                } else if (v.getId() == R.id.theoreticalComputerScienceCard) {
                    subject = "Theoretical Computer Science";
                } else if (v.getId() == R.id.internetProgrammingCard) {
                    subject = "Internet Programming";
                } else if (v.getId() == R.id.dataWarehouseCard) {
                    subject = "Data Warehouse and Mining";
                }

                Intent intent = new Intent(VideoResourcesActivity.this, SubjectVideosActivity.class);
                intent.putExtra("subject", subject);
                startActivity(intent);
            }
        };

        // Set the click listener for each card
        softwareEngineeringCard.setOnClickListener(cardClickListener);
        computerNetworksCard.setOnClickListener(cardClickListener);
        theoreticalComputerScienceCard.setOnClickListener(cardClickListener);
        internetProgrammingCard.setOnClickListener(cardClickListener);
        dataWarehouseCard.setOnClickListener(cardClickListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
