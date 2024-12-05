package com.mina.yasser;

import android.content.Intent;
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

public class RegActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private AppDatabase database;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

//         Initialize views
        etUsername = findViewById(R.id.Username); // Match with XML ID
        etPassword = findViewById(R.id.password); // Match with XML ID

        // Initialize the Room database
        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "bookstore_database"
        ).build();
        userDao = database.userDao();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    // Method for the Sign-Up button
    public void signUp() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
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
                    });
                    return; // Exit the method, as we don't want to insert the user if the username exists
                }

                // Create a new user object
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setAdmin(false); // Default: Not an admin

                // Insert into the database
                userDao.insertUser(newUser);
                Log.d("asd","0");
                // Navigate to MainActivity on success
                runOnUiThread(() -> {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("asd","1");
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    Log.d("asd","2");
                    finish(); // Close current activity
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "An error occurred during registration.", Toast.LENGTH_SHORT).show());
            }
        }).start();


    }

    // Method for the Login button
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
