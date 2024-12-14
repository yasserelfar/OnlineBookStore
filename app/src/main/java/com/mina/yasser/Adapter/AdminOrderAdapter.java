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

import com.mina.yasser.DataBase.Order;
import com.mina.yasser.DataBase.OrderDao;
import com.mina.yasser.R;

import java.util.List;
import java.util.concurrent.Executors;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OrderDao orderDao;
    private Context context;

    public AdminOrderAdapter(List<Order> orderList, OrderDao orderDao, Context context) {
        this.orderList = orderList;
        this.orderDao = orderDao;
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

        holder.orderDetails.setText(
                "Order ID: " + order.getOrderId() +
                        "\nUser: " + order.getUserName() +
                        "\nBook: " + order.getBookName() +
                        "\nPrice: $" + String.format("%.2f", order.getPrice()) +
                        "\nStatus: " + order.getStatus()
        );

        holder.confirmButton.setOnClickListener(v -> {
            if (!order.getStatus().equals("Pending")) {
                Toast.makeText(context, "Order already processed!", Toast.LENGTH_SHORT).show();
                return;
            }
            order.setStatus("Confirmed");
            updateOrder(order, "Order confirmed!");
        });

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
        TextView orderDetails;
        Button confirmButton, cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}
