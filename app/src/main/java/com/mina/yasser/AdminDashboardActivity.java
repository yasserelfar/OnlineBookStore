package com.mina.yasser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

//         Initialize buttons
        Button manageBooksButton = findViewById(R.id.manageBooksButton);
        Button manageCategoriesButton = findViewById(R.id.manageCategoriesButton);
        Button processOrdersButton = findViewById(R.id.processOrdersButton);

        manageBooksButton.setOnClickListener(v ->
                startActivity(new Intent(this, ManageBooksActivity.class))
        );

         manageCategoriesButton.setOnClickListener(v ->
             startActivity(new Intent(AdminDashboardActivity.this, ManageCategoriesActivity.class))
         );

         processOrdersButton.setOnClickListener(v ->
             startActivity(new Intent(AdminDashboardActivity.this, AdminOrderActivity.class))
         );


    }
}
