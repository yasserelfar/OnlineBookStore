package com.mina.yasser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.CartItem;
import com.mina.yasser.DataBase.CartManager;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.Adapter.CheckoutAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView totalPriceTextView;
    private Button placeOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        totalPriceTextView = findViewById(R.id.totalPrice);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Get the cart items passed from the previous activity
        List<CartItem> cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");

        if (cartItems == null) {
            Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show();
            return; // Prevent app from crashing if no items are passed
        }

        // Set up RecyclerView
        CheckoutAdapter adapter = new CheckoutAdapter(cartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Calculate total price
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText("Total Price: $" + totalPrice);
//
        placeOrderButton.setOnClickListener(v -> {
            // Get current cart items
//            List<CartItem> cartItems = (List<CartItem>) getIntent().getSerializableExtra("cartItems");//variable cartitems already defined in scope
            if (cartItems == null || cartItems.isEmpty()) {
                Toast.makeText(this, "No items in the cart to place an order.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve user details
            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1); // Replace with the method you use to store userId
            String userName = sharedPreferences.getString("userName", "Unknown User");

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            for (CartItem item : cartItems) {
                Order order = new Order();
                order.setUserId(userId);
                order.setUserName(userName);
                order.setBookName(item.getProductName());
                order.setPrice(item.getPrice());
                order.setCategory(item.getCategory());
                order.setDate(currentDate);
                order.setStatus("Pending"); // Default status

                // Save the order using Room DAO
                new Thread(() -> {
                    AppDatabase.getInstance(this).orderDao().insertOrder(order);
                }).start();
            }

            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

            // Clear the cart and navigate back
            CartManager.getInstance().clearCart(); // Assuming a CartManager exists for cart management
            finish();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("cartItems", (Serializable) CartManager.getInstance().getCartItems());
            startActivity(intent);
        });

    }
}
