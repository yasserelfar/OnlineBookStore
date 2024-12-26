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
import com.mina.yasser.DataBase.OrderItemDao;
import com.mina.yasser.DataBase.ProductDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderActivity extends AppCompatActivity {
    private OrderDao orderDao;
    private SharedPreferences sharedPreferences;
    private OrderItemDao orderItemDao;
    private  ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize DAO and SharedPreferences
        orderDao = AppDatabase.getInstance(this).orderDao();
        orderItemDao = AppDatabase.getInstance(this).orderItemDao();
        productDao = AppDatabase.getInstance(this).productDao();
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);  // Use UserPrefs instead of LoginPrefs

        // Get userId from SharedPreferences
        int userId = sharedPreferences.getInt("userId", -1);  // Assuming userId is a String in SharedPreferences

        if (userId != -1) {
            // Setup RecyclerView
            RecyclerView ordersRecyclerView = findViewById(R.id.orderRecyclerView);
            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Fetch orders for the current user
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                List<Order> orders = orderDao.getUserOrders(userId);  // Modify the DAO query to get orders by userId
                Log.d("orderssize","orfers size :"+orders.size());
                runOnUiThread(() -> {
                    if (
                            orders != null && !orders.isEmpty()) {
                        // Initialize the adapter with the fetched orders
                        Log.d("orderssize","orfers size :"+orders.size());

                        OrderAdapter adapter = new OrderAdapter(orders, orderDao,orderItemDao,productDao, this);  // Pass the necessary DAOs and context
                        ordersRecyclerView.setAdapter(adapter);
                    } else {
                        Log.d("OrderActivity", "No orders found for user: " + userId);
                        // Handle empty orders list
                        // You can show a "No Orders" message here
                    }
                });
            });
        } else {
            Log.d("OrderActivity", "User not logged in or userId is missing.");
            // Handle the case where userId is missing (perhaps redirect to login)
        }
    }
}
