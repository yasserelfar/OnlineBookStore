package com.mina.yasser;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.OrderAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;

import java.util.List;
import java.util.concurrent.Executors;

public class OrderActivity extends AppCompatActivity {
    private OrderDao orderDao;
    private int userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderDao = AppDatabase.getInstance(this).orderDao();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        RecyclerView ordersRecyclerView = findViewById(R.id.orderRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Order> orders = orderDao.getUserOrders(userId);
            runOnUiThread(() -> {
                OrderAdapter adapter = new OrderAdapter(orders, orderDao,this);
                ordersRecyclerView.setAdapter(adapter);
            });
        });
    }
}
