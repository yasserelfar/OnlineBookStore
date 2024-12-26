package com.mina.yasser.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.DataBase.OrderItem;
import com.mina.yasser.DataBase.OrderItemDao;
import com.mina.yasser.DataBase.Product;
import com.mina.yasser.DataBase.ProductDao;
import com.mina.yasser.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private SharedPreferences sharedPreferences;
    private final List<Order> orderList;
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final ProductDao productDao;
    private final Context context;
    private final int userId;

    public OrderAdapter(List<Order> orderList, OrderDao orderDao, OrderItemDao orderItemDao, ProductDao productDao, Context context) {
        this.orderList = orderList;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.productDao = productDao;
        this.context = context;

        // Get the userId from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        this.userId = sharedPreferences.getInt("userId", -1);


        // Filter the orders based on userId
//        filterOrdersByUserId(orderList);
    }
    private void filterOrdersByUserId(List<Order> allOrders) {
        if (userId != -1) {
            for (Order order : allOrders) {
                if (order.getUserId()==userId) {
                    orderList.add(order); // Add order to the list if it belongs to the current user
                }
            }
        }
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Bind order details
        holder.orderDetails.setText(
                "Order ID: " + order.getOrderId() +
                        "\nUser: " + order.getUserName() +
                        "\nPrice: $" + String.format("%.2f", order.getPrice()) +
                        "\nStatus: " + order.getStatus()
        );

        if ("Canceled".equals(order.getStatus()) || "Shipping".equals(order.getStatus())) {
            holder.payButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        } else if ("Confirmed".equals(order.getStatus())) {
            holder.payButton.setEnabled(true);
            holder.cancelButton.setEnabled(false);
        } else if ("Pending".equals(order.getStatus())) {
            holder.payButton.setEnabled(false);
        }

        // Fetch order items asynchronously using orderId
        Executors.newSingleThreadExecutor().execute(() -> {
            List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(order.getOrderId());
            StringBuilder bookDetails = new StringBuilder();
            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem orderItem : orderItems) {
                    Product product = productDao.getProductByBarcodes(orderItem.getProductBarcode());
                    if (product != null) {
                        bookDetails.append("- ").append(product.getName())
                                .append(" (Qty: ").append(orderItem.getQuantity())
                                .append(")\n");
                    }
                }
            }
            String finalBookDetails = bookDetails.length() > 0 ? bookDetails.toString().trim() : "No books in this order.";

            // Update the UI with the order items
            ((AppCompatActivity) context).runOnUiThread(() -> holder.booksDetails.setText(finalBookDetails));
        });

        // Cancel order button
        holder.cancelButton.setOnClickListener(v -> {
            order.setStatus("Canceled");
            updateOrder(order, "Order Canceled!");
            holder.cancelButton.setEnabled(false);
        });

        // Pay order button
        holder.payButton.setOnClickListener(v -> {
            order.setStatus("Shipping");
            updateOrder(order, "Order Paid!");
            holder.payButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDetails, booksDetails;
        Button payButton, cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            booksDetails = itemView.findViewById(R.id.booksDetails);
            payButton = itemView.findViewById(R.id.payButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
