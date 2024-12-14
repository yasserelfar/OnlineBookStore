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
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.R;

import java.util.List;
import java.util.concurrent.Executors;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orderList;
    private final OrderDao orderDao;
    private final Context context; // Use context in constructor

    public OrderAdapter(List<Order> orderList, OrderDao orderDao, Context context) {
        this.orderList = orderList;
        this.orderDao = orderDao;
        this.context = context;
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
        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.userName.setText("User: " + order.getUserName());
//        holder.bookName.setText("Book: " + order.getBookName()); // Binding book name
        holder.price.setText("Price: $" + String.format("%.2f", order.getPrice()));
        holder.status.setText("Status: " + order.getStatus());
        if ("Canceled".equals(order.getStatus()))
        {
            holder.cancelButton.setEnabled(false);
        }
        Log.d("OrderViewHolder", "orderId: " + holder.orderId);
        Log.d("OrderViewHolder", "userName: " + holder.userName);
        Log.d("OrderViewHolder", "price: " + holder.price);
        Log.d("OrderViewHolder", "status: " +holder.status);
        // Cancel order button
        holder.cancelButton.setOnClickListener(v -> {
            order.setStatus("Canceled");
            updateOrder(order);
            Toast.makeText(context, "Order canceled!", Toast.LENGTH_SHORT).show();
            holder.cancelButton.setEnabled(false);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void updateOrder(Order order) {
        // Use executor to run database operations in the background
        Executors.newSingleThreadExecutor().execute(() -> orderDao.updateOrder(order));
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, userName, bookName, price, status;
        Button  cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderIdText);
            userName = itemView.findViewById(R.id.userNameText);
//            bookName = itemView.findViewById(R.id.bookNameText); // Added missing view for bookName
            price = itemView.findViewById(R.id.priceText);
            status = itemView.findViewById(R.id.statusText);
//            confirmButton = itemView.findViewById(R.id.confirmButton)
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
