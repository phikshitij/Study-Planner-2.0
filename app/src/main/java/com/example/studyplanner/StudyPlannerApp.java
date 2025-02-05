package com.example.studyplanner;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StudyPlannerApp extends Application {
    private static final String TAG = "StudyPlannerApp";
    private static StudyPlannerApp instance;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try {
            dbHelper = new DatabaseHelper(this);
            Log.d(TAG, "Database initialized successfully");
            
            // Copy PDF files from assets to external storage
            copyPdfFiles();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing app: " + e.getMessage());
        }
    }

    private void copyPdfFiles() {
        String[] pdfFiles = {
            "software_engineering.pdf",
            "computer_networks.pdf",
            "theoretical_cs.pdf",
            "internet_programming.pdf",
            "data_warehouse.pdf"
        };

        File externalDir = getExternalFilesDir(null);
        if (externalDir == null) {
            Log.e(TAG, "External storage not available");
            return;
        }

        AssetManager assetManager = getAssets();
        for (String fileName : pdfFiles) {
            File outFile = new File(externalDir, fileName);
            
            // Skip if file already exists
            if (outFile.exists()) {
                continue;
            }

            try (InputStream in = assetManager.open(fileName);
                 OutputStream out = new FileOutputStream(outFile)) {
                
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                Log.d(TAG, "Copied " + fileName + " to external storage");
            } catch (IOException e) {
                Log.e(TAG, "Error copying " + fileName + ": " + e.getMessage());
            }
        }
    }

    public static StudyPlannerApp getInstance() {
        return instance;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
