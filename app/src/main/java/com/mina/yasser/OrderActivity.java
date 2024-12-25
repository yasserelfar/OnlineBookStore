package com.mina.yasser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.OrderAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderActivity extends AppCompatActivity {
    private OrderDao orderDao;
    private int userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize DAO and SharedPreferences
        orderDao = AppDatabase.getInstance(this).orderDao();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Setup RecyclerView
        RecyclerView ordersRecyclerView = findViewById(R.id.orderRecyclerView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch orders for the current user
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            List<Order> orders = orderDao.getUserOrders(userId);

            runOnUiThread(() -> {
                if (orders != null && !orders.isEmpty()) {
                    // Initialize the adapter with the fetched orders
                    OrderAdapter adapter = new OrderAdapter(orders, orderDao, this);
                    ordersRecyclerView.setAdapter(adapter);
                } else {
                    Log.d("OrderViewHolder", "empty " );
                    Log.d("OrderViewHolder", "userid "+userId );
                    // Handle empty orders list
                    // You can show a "No Orders" message here
                }
            });
        });
    }
}
