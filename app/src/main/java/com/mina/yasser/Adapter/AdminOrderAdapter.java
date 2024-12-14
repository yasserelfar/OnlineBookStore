package com.mina.yasser.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.AdminOrderActivity;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderDao orderDao;
    private CartDao cartDao;
    private ProductDao productDao;
    private Context context;

    public AdminOrderAdapter(List<Order> orderList, OrderDao orderDao, CartDao cartDao, ProductDao productDao, Context context) {
        this.orderList = (orderList != null) ? orderList : new ArrayList<>();

        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.context = context;
    }

    public AdminOrderAdapter(List<Order> orders, OrderDao orderDao, AdminOrderActivity adminOrderActivity) {
        this.orderList = (orderList != null) ? orderList : new ArrayList<>();

        this.orderDao = orderDao;
        this.context = adminOrderActivity;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Display basic order details
        holder.orderDetails.setText(
                "Order ID: " + order.getOrderId() +
                        "\nUser: " + order.getUserName() +
                        "\nPrice: $" + String.format("%.2f", order.getPrice()) +
                        "\nStatus: " + order.getStatus()
        );

        // Fetch and display books in the order asynchronously
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Cart> cartItems = cartDao.getCartItemsByUserId(order.getUserId());
            if (cartItems == null || cartItems.isEmpty()) {
                ((AppCompatActivity) context).runOnUiThread(() -> holder.booksDetails.setText("No books in this order."));
                return;
            }

            List<String> barcodes = new ArrayList<>();
            for (Cart cart : cartItems) {
                barcodes.add(cart.getProductBarcode());
            }

            List<Product> products = productDao.getProductsByBarcodes(barcodes);

            StringBuilder bookDetails = new StringBuilder();
            for (Cart cart : cartItems) {
                for (Product product : products) {
                    if (cart.getProductBarcode().equals(product.getBarcode())) {
                        bookDetails.append("- ")
                                .append(product.getName())
                                .append(" (Qty: ")
                                .append(cart.getQuantity())
                                .append(")\n");
                    }
                }
            }

            String finalBookDetails = bookDetails.toString().trim();
            ((AppCompatActivity) context).runOnUiThread(() -> holder.booksDetails.setText(finalBookDetails.isEmpty() ? "No books in this order." : finalBookDetails));
        });

        // Handle confirm button
        holder.confirmButton.setOnClickListener(v -> {
            if (!order.getStatus().equals("Pending")) {
                Toast.makeText(context, "Order already processed!", Toast.LENGTH_SHORT).show();
                return;
            }
            order.setStatus("Confirmed");
            updateOrder(order, "Order confirmed!");
        });

        // Handle cancel button
        holder.cancelButton.setOnClickListener(v -> {
            if (!order.getStatus().equals("Pending")) {
                Toast.makeText(context, "Order already processed!", Toast.LENGTH_SHORT).show();
                return;
            }
            order.setStatus("Canceled");
            updateOrder(order, "Order canceled!");
        });
    }


    private void updateOrder(Order order, String message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            orderDao.updateOrder(order);
            ((AppCompatActivity) context).runOnUiThread(() -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetails, booksDetails;
        Button confirmButton, cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            booksDetails = itemView.findViewById(R.id.booksDetails);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
