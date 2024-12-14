package com.mina.yasser;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.AdminOrderAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;

import java.util.List;
import java.util.concurrent.Executors;

public class AdminOrderActivity extends AppCompatActivity {
    private OrderDao orderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        orderDao = AppDatabase.getInstance(this).orderDao();
        RecyclerView adminOrderRecyclerView = findViewById(R.id.adminOrderRecyclerView);
        adminOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display all orders
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Order> orders = orderDao.getAllOrders();
            runOnUiThread(() -> {
                AdminOrderAdapter adapter = new AdminOrderAdapter(orders, orderDao, this);
                adminOrderRecyclerView.setAdapter(adapter);
            });
        });
    }
}