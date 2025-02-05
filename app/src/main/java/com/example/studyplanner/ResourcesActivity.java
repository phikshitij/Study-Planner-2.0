package com.example.studyplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import java.io.File;

public class ResourcesActivity extends AppCompatActivity {
    private static final String TAG = "ResourcesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize cards
        setupCards();
    }

    private void setupCards() {
        CardView seCard = findViewById(R.id.seCard);
        CardView cnCard = findViewById(R.id.cnCard);
        CardView tcsCard = findViewById(R.id.tcsCard);
        CardView ipCard = findViewById(R.id.ipCard);
        CardView dwmCard = findViewById(R.id.dwmCard);

        seCard.setOnClickListener(v -> openPdf("software_engineering.pdf"));
        cnCard.setOnClickListener(v -> openPdf("computer_networks.pdf"));
        tcsCard.setOnClickListener(v -> openPdf("theoretical_cs.pdf"));
        ipCard.setOnClickListener(v -> openPdf("internet_programming.pdf"));
        dwmCard.setOnClickListener(v -> openPdf("data_warehouse.pdf"));
    }

    private void openPdf(String fileName) {
        try {
            // Get the PDF file from assets
            File pdfFile = new File(getExternalFilesDir(null), fileName);
            Uri pdfUri = FileProvider.getUriForFile(this, 
                "com.example.studyplanner.fileprovider", pdfFile);

            // Create intent to view PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Check if there's an app to handle PDF viewing
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please install a PDF viewer", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening PDF: " + e.getMessage());
            Toast.makeText(this, "Error opening PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
