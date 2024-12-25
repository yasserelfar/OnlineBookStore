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
    private SharedPreferences sharedPreferences;
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
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Initialize the Room database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bookstore_database").build();
        userDao = database.userDao();

        // Set DatePicker for birthdate
        etBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegActivity.this,
                    (view, year1, month1, dayOfMonth1) -> etBirthdate.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth1),
                    year, month, dayOfMonth);

            datePickerDialog.show();
        });

        btnSignUp = findViewById(R.id.button);
        btnSignUp.setOnClickListener(v -> signUp());
    }

    // Method for the Sign-Up button
    public void signUp() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String birthdate = etBirthdate.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || birthdate.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert user into the database (in a background thread)
        new Thread(() -> {
            try {
                // Check if the username already exists
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    // If user exists, show error message and clear fields
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                        // Clear the input fields so the user can retry
                        etUsername.setText("");
                        etPassword.setText("");
                        etEmail.setText("");
                        etAddress.setText("");
                        etPhone.setText("");
                        etBirthdate.setText("");
                    });
                    return; // Exit the method, as we don't want to insert the user if the username exists
                }

                // Create a new user object
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);
                newUser.setAddress(address);
                newUser.setPhone(phone);
                newUser.setBirthdate(birthdate);
                newUser.setAdmin(true); // Default: Not an admin

                // Insert into the database
                userDao.insertUser(newUser);
                sharedPreferences.edit()
                        .putString("Username", username)
                        .putString("Password", password)
                        .putInt("userId", newUser.getUserId())
                        .apply();
                // Navigate to MainActivity on success
                runOnUiThread(() -> {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close current activity
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "An error occurred during registration.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Method for the Login button
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
