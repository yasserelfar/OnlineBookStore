package com.mina.yasser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.User;
import com.mina.yasser.DataBase.UserDao;

public class AccountManagementActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etAddress, etPhone;
    private TextView tvBirthdate;
    private Button btnUpdate, btnLogout,btnorders;
    private UserDao userDao;
    private User currentUser;
    ImageButton btnCart;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Get stored userId
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            // If userId is not available, redirect to LoginActivity
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        tvBirthdate = findViewById(R.id.tvBirthdate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);
        btnCart  = findViewById(R.id.btnCart);
        btnorders= findViewById(R.id.btnorders);
        // Initialize DAO
        AppDatabase database = AppDatabase.getInstance(this);
        userDao = database.userDao();
        btnorders.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        });
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        }
        );

        // Load user details
        new Thread(() -> {
            currentUser = userDao.getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    etUsername.setText(currentUser.getUsername());
                    etEmail.setText(currentUser.getEmail());
                    etAddress.setText(currentUser.getAddress());
                    etPhone.setText(currentUser.getPhone());
                    tvBirthdate.setText(currentUser.getBirthdate());
                }
            });
        }).start();
        etUsername.setEnabled(false);
        // Handle update button click
        btnUpdate.setOnClickListener(v -> updateAccountDetails());

        // Handle logout button click
        btnLogout.setOnClickListener(v -> logout());
    }

    private void updateAccountDetails() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newAddress = etAddress.getText().toString().trim();
        String newPhone = etPhone.getText().toString().trim();
        String newBirthdate = tvBirthdate.getText().toString().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() || newPhone.isEmpty() || newBirthdate.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            // Update user details
            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);
            currentUser.setAddress(newAddress);
            currentUser.setPhone(newPhone);
            currentUser.setBirthdate(newBirthdate);

            userDao.update(currentUser);

            // Update SharedPreferences
            sharedPreferences.edit()
                    .putString("Username", newUsername)
                    .putString("Email", newEmail)
                    .putString("Address", newAddress)
                    .putString("Phone", newPhone)
                    .apply();

            runOnUiThread(() -> Toast.makeText(this, "Account details updated successfully!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void logout() {
        // Clear SharedPreferences
        sharedPreferences.edit().clear().apply();

        // Redirect to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity
        finish();
    }
}
