package com.mina.yasser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.CheckoutAdapter;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.Adapter.CartAdapter;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.DataBase.UserDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity {
    private List<Cart> cartList = new ArrayList<>();
    private double totalPrice;
    private int userId;
    private String username;
    private CartDao cartDao;
    private UserDao userDao;
    private OrderDao orderDao;
    private ProductDao productDao;
    private SharedPreferences sharedPreferences;
    TextView checkoutTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Initialize DAOs
        userDao = AppDatabase.getInstance(this).userDao();
        cartDao = AppDatabase.getInstance(this).cartDao();
        orderDao = AppDatabase.getInstance(this).orderDao();
        productDao = AppDatabase.getInstance(this).productDao();

        // Fetch username asynchronously
        fetchUsernameAsync();

        checkoutTotalPrice = findViewById(R.id.checkoutTotalPrice);
        RecyclerView cartRecyclerView = findViewById(R.id.cartRecyclerView);
        Button proceedToPaymentButton = findViewById(R.id.proceedToPaymentButton);
        Button placeOrderButton = findViewById(R.id.placeOrderButton);
        TextView orderStatus = findViewById(R.id.orderStatus);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartDao.getCartProducts(userId).observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                if (carts != null && !carts.isEmpty()) {
                    cartList = carts;
                    calculateTotalPriceAsync(); // Ensure total price is calculated

                    checkoutTotalPrice.setText("Total: $" + String.format("%.2f", totalPrice));

                    // Set up RecyclerView adapter
                    CheckoutAdapter cartAdapter = new CheckoutAdapter(cartList, productDao, cartDao, CheckoutActivity.this);
                    cartRecyclerView.setAdapter(cartAdapter);

                    // Enable the place order button
                    placeOrderButton.setEnabled(true);

                    // Handle "Place Order"
                    placeOrderButton.setOnClickListener(v -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            addOrderToDatabase(cartList, totalPrice);
                            clearCartForUser(userId);
                            runOnUiThread(() -> {
                                orderStatus.setText("Order Status: Placed");
                                Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            });
                        });
                    });
                } else {
                    cartList.clear();
                    totalPrice = 0; // Reset total price
                    checkoutTotalPrice.setText("Total: $0.00");
                    placeOrderButton.setEnabled(false);
                    Toast.makeText(CheckoutActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        proceedToPaymentButton.setOnClickListener(v -> {
            Toast.makeText(this, "Proceeding to payment...", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchUsernameAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch username in the background
            username = userDao.getUserById(userId).getUsername();
            Log.d("username", "userName: " + username);
        });
    }

    private void calculateTotalPriceAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            double totalPrices[] = {0.0};
            for (Cart cart : cartList) {
                Product product = productDao.getProductByBarcodes(cart.getProductBarcode());
                if (product != null) {
                    totalPrices[0] += product.getPrice() * cart.getQuantity();
                } else {
                    Log.e("CheckoutActivity", "Product not found for barcode: " + cart.getProductBarcode());
                }
            }
            totalPrice = totalPrices[0];
            // Update the UI on the main thread after calculation
            runOnUiThread(() -> {
                Log.d("CheckoutActivity", "Total price calculated: $" + totalPrices[0]);
                checkoutTotalPrice.setText("Total: $" + String.format("%.2f", totalPrices[0]));
            });
        });
    }

    private void addOrderToDatabase(List<Cart> cartList, double totalPrice) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        Order order = new Order();
        order.setUserId(userId);
        order.setUserName(username);
        order.setPrice(totalPrice);
        order.setDate(currentDate);
        order.setStatus("Pending");

        orderDao.insertOrder(order);
    }

    private void clearCartForUser(int userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            cartDao.clearCartForUser(userId);
            runOnUiThread(() -> cartList.clear());
        });
    }
}
