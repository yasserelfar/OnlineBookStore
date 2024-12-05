package com.mina.yasser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.User;
import com.mina.yasser.DataBase.UserDao;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.LoginUsername);
        etPassword = findViewById(R.id.password);

        // Initialize Room database using Singleton
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.userDao();
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

                // Navigate to MainActivity if credentials are valid
                runOnUiThread(() -> {
                    if (user != null && user.getPassword().equals(password)) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
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
