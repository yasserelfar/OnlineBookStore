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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etUsernameOrEmail;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);

        // Initialize Room database using Singleton
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        userDao = database.userDao();
    }

    public void resetPassword(View view) {
        String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();

        if (usernameOrEmail.isEmpty()) {
            Toast.makeText(this, "Please enter your username or email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform password recovery in a background thread
        new Thread(() -> {
            try {
                // Search for the user by username or email
                User user = userDao.getUserByUsernameOrEmail(usernameOrEmail);

                if (user != null) {
                    // Generate a temporary password
                    String tempPassword = "0000";
                    user.setPassword(tempPassword);

                    // Update the user in the database (background thread)
                    userDao.update(user);

                    // Notify the user on the main thread
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Password reset successful. Your new password is: " + tempPassword, Toast.LENGTH_LONG).show();

                        // Optionally navigate back to LoginActivity
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    // Notify user of failure on the main thread
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No user found with the provided username or email.", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                // Handle any exceptions on the main thread
                runOnUiThread(() ->
                        Toast.makeText(this, "An error occurred during password recovery. Please try again.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }


    private String generateTemporaryPassword() {
        // Simple random password generator
        return "Temp1234"; // Replace with a more secure generation method
    }
}
