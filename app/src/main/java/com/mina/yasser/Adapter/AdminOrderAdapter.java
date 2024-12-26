package com.mina.yasser.Adapter;

import android.content.Context;
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

import com.mina.yasser.AdminOrderActivity;
import com.mina.yasser.DataBase.Cart;
import com.mina.yasser.DataBase.CartDao;
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

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderDao orderDao;
    private CartDao cartDao;
    private ProductDao productDao;
    private Context context;
    private OrderItemDao orderItemDao;

    public AdminOrderAdapter(List<Order> orderList, OrderDao orderDao, CartDao cartDao, ProductDao productDao, OrderItemDao orderItemDao, Context context) {
        this.orderList = (orderList != null) ? orderList : new ArrayList<>();
        this.orderItemDao = orderItemDao;
        this.orderDao = orderDao;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.context = context;
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

        if ("Canceled".equals(order.getStatus()) || "Shipping".equals(order.getStatus())) {
            holder.confirmButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        }

        holder.cancelButton.setOnClickListener(v -> {
            order.setStatus("Canceled");
            updateOrder(order, "Order Canceled");
            holder.cancelButton.setEnabled(false);
        });

        holder.confirmButton.setOnClickListener(v -> {
            order.setStatus("Confirmed");
            updateOrder(order, "Confirmed");
            holder.confirmButton.setEnabled(false);
            holder.cancelButton.setEnabled(false);
        });

        // Fetch order items for this order asynchronously using orderId
        Executors.newSingleThreadExecutor().execute(() -> {
            if (orderItemDao != null) {
                // Fetch all order items associated with the order using the orderId
                List<OrderItem> orderItems = orderItemDao.getOrderItemsByOrderId(order.getOrderId());

                // Add more detailed logs to debug
                if (orderItems == null) {
                    Log.d("OrderItemDebug", "orderItems is null for orderId: " + order.getOrderId());
                } else if (orderItems.isEmpty()) {
                    Log.d("OrderItemDebug", "orderItems is empty for orderId: " + order.getOrderId());
                } else {
                    Log.d("OrderItemDebug", "orderItems size: " + orderItems.size() + " for orderId: " + order.getOrderId());
                }

                StringBuilder bookDetails = new StringBuilder();
                if (orderItems != null && !orderItems.isEmpty()) {
                    for (OrderItem orderItem : orderItems) {
                        // Fetch product details for each order item using productBarcode
                        Product product = productDao.getProductByBarcodes(orderItem.getProductBarcode());
                        if (product != null) {
                            bookDetails.append("- ")
                                    .append(product.getName())
                                    .append(" (Qty: ")
                                    .append(orderItem.getQuantity())
                                    .append(")\n");
                        } else {
                            Log.d("OrderItemDebug", "No product found for barcode: " + orderItem.getProductBarcode());
                        }
                    }
                }

                // If no order items found, display a default message
                String finalBookDetails = bookDetails.length() > 0 ? bookDetails.toString().trim() : "No books in this order.";

                // Update the UI with the final book details
                ((AppCompatActivity) context).runOnUiThread(() -> {
                    if (holder.booksDetails != null) {
                        holder.booksDetails.setText(finalBookDetails);
                    }
                });
            } else {
                Log.e("OrderItemDao", "orderItemDao is null for orderId: " + order.getOrderId());
            }
        });

    }

    private void updateOrder(Order order, String message) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (orderDao != null) {
                orderDao.updateOrder(order);
                ((AppCompatActivity) context).runOnUiThread(() -> {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                });
            } else {
                Log.e("OrderDao", "orderDao is null");
            }
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
