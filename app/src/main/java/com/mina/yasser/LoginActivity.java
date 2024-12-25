package com.mina.yasser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.User;
import com.mina.yasser.DataBase.UserDao;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private CheckBox rememberMeCheckBox;
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    boolean isRemembered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.LoginUsername);
        etPassword = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        TextView forgotPassword = findViewById(R.id.forgotPassword); // TextView for "Forgot Password"

        // Initialize Room database using Singleton
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.userDao();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Check if "Remember Me" is checked and pre-fill credentials
       isRemembered = sharedPreferences.getBoolean("RememberMe", false);
      boolean isadmin=sharedPreferences.getBoolean("admin", false);
        if (isRemembered) {
            if(isadmin){
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }
            else{
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }}

        // Handle "Forgot Password" click
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    public void login(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform login in a background thread
        new Thread(() -> {
            try {
                User user = userDao.getUserByUsername(username);

                runOnUiThread(() -> {
                    if (user != null && user.getPassword().equals(password)) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Save to SharedPreferences if "Remember Me" is checked
                        if (rememberMeCheckBox.isChecked()) {
                            sharedPreferences.edit()
                                    .putBoolean("RememberMe", true)
                                    .putString("Username", username)
                                    .putString("Password", password)
                                    .putInt("userId", user.getUserId())
                                    .apply();
                        } else {
                            sharedPreferences.edit()
                                    .putString("Username", username)
                                    .putString("Password", password)
                                    .putInt("userId", user.getUserId())
                                    .apply();
                        }

                        Intent intent;
                        if (user.isAdmin()) {
                            sharedPreferences.edit().putBoolean("admin",true).apply();
                            intent = new Intent(this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(this, HomeActivity.class);
                        }
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    } else {
                        Toast.makeText(this, "Invalid credentials. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "An error occurred during login. Please try again.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    public void signUP(View view) {
        Intent intent = new Intent(this, RegActivity.class);
        startActivity(intent);
    }
}
