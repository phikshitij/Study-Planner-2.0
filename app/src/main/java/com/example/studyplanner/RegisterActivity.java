package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText ageInput;
    private EditText courseInput;
    private Spinner performanceSpinner;
    private EditText studyHoursInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        courseInput = findViewById(R.id.courseInput);
        performanceSpinner = findViewById(R.id.performanceSpinner);
        studyHoursInput = findViewById(R.id.studyHoursInput);
        emailInput = findViewById(R.id.emailRegisterInput);
        passwordInput = findViewById(R.id.passwordRegisterInput);
        registerButton = findViewById(R.id.registerButton);

        // Setup performance spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.performance_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        performanceSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String age = ageInput.getText().toString().trim();
                String course = courseInput.getText().toString().trim();
                String performance = performanceSpinner.getSelectedItem().toString();
                String studyHours = studyHoursInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (name.isEmpty() || age.isEmpty() || course.isEmpty() || studyHours.isEmpty() 
                    || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel user = new UserModel();
                user.setName(name);
                user.setAge(Integer.parseInt(age));
                user.setCourse(course);
                user.setPerformanceLevel(performance);
                user.setStudyHoursPerDay(Integer.parseInt(studyHours));
                user.setEmail(email);
                user.setPassword(password);

                if (dbHelper.addUser(user)) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
