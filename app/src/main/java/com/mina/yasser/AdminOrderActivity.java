package com.mina.yasser;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.AdminOrderAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.ProductDao;

import java.util.List;
import java.util.concurrent.Executors;

public class AdminOrderActivity extends AppCompatActivity {
    private OrderDao orderDao;
    private CartDao cartDao;
    private ProductDao productDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        orderDao = AppDatabase.getInstance(this).orderDao();
        cartDao = AppDatabase.getInstance(this).cartDao();
        productDao = AppDatabase.getInstance(this).productDao();

        RecyclerView adminOrderRecyclerView = findViewById(R.id.adminOrderRecyclerView);
        adminOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display all orders
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Order> orders = orderDao.getAllOrders();
            if (orders != null) {
            runOnUiThread(() -> {
                AdminOrderAdapter adapter  = new AdminOrderAdapter(orders, orderDao, cartDao, productDao, this);
                adminOrderRecyclerView.setAdapter(adapter);
            });
        }
            else {
                Toast.makeText(this, "No orders available", Toast.LENGTH_SHORT).show();
            }
        });
        }
}