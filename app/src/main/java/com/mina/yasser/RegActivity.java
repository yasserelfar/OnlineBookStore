package com.mina.yasser;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.User;
import com.mina.yasser.DataBase.UserDao;

import java.util.Calendar;

public class RegActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etAddress, etPhone, etBirthdate;
    private Button btnSignUp;
    private AppDatabase database;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        // Initialize views
        etUsername = findViewById(R.id.Username);
        etPassword = findViewById(R.id.password);
        etEmail = findViewById(R.id.email);
        etAddress = findViewById(R.id.address);
        etPhone = findViewById(R.id.phone);
        etBirthdate = findViewById(R.id.birthdate);
        btnSignUp = findViewById(R.id.button);

        // Initialize the Room database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bookstore_database")
                .fallbackToDestructiveMigration() // Handle database schema changes
                .build();
        userDao = database.userDao();

        // Set DatePicker for birthdate
        etBirthdate.setOnClickListener(v -> showDatePicker());

        // Set Sign-Up button click listener
        btnSignUp.setOnClickListener(v -> signUp());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RegActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        etBirthdate.setText(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay),
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method for the Sign-Up button
    public void signUp() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthdate = etBirthdate.getText().toString().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Check if the username already exists
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                        clearFields();
                    });
                    return;
                }

                // Create a new user object
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);
                newUser.setAddress(address);
                newUser.setPhone(phone);
                newUser.setBirthdate(birthdate);
                newUser.setAdmin(false); // Default: Not an admin

                // Insert into the database
                userDao.insertUser(newUser);

                // Save user ID in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                sharedPreferences.edit().putInt("userId", newUser.getUserId()).apply();

                // Navigate to HomeActivity on success
                runOnUiThread(() -> {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish(); // Close current activity
                });
            } catch (Exception e) {
                Log.e("RegActivity", "Error during registration", e);
                runOnUiThread(() -> Toast.makeText(this, "An error occurred during registration.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Clear input fields
    private void clearFields() {
        etUsername.setText("");
        etPassword.setText("");
        etEmail.setText("");
        etAddress.setText("");
        etPhone.setText("");
        etBirthdate.setText("");
    }

    // Method for the Login button
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
