package com.mina.yasser;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mina.yasser.Adapter.OrderAdapter;
import com.mina.yasser.DataBase.Order;
import com.mina.yasser.ViewModel.OrderViewModel;

import java.util.Calendar;
import java.util.List;

public class OrdersAdmin extends AppCompatActivity {

    private EditText editTextDate;
    private Button generateReportButton;
    private RecyclerView recyclerViewOrders;
    private TextView noResultsMessage;
    private OrderViewModel orderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_admin);

        editTextDate = findViewById(R.id.editTextDate);
        generateReportButton = findViewById(R.id.generateReportButton);
        recyclerViewOrders = findViewById(R.id.recyclerViewTransactions);
        noResultsMessage = findViewById(R.id.noResultsMessage);

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        editTextDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth1) -> editTextDate.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth1),
                    year, month, dayOfMonth);

            datePickerDialog.show();
        });
        generateReportButton.setOnClickListener(v -> {
            String selectedDate = editTextDate.getText().toString().trim();
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            orderViewModel.getOrdersByDate(selectedDate).observe(this, orders -> {
                if (orders == null || orders.isEmpty()) {
                    noResultsMessage.setVisibility(View.VISIBLE);
                    recyclerViewOrders.setVisibility(View.GONE);
                } else {
                    noResultsMessage.setVisibility(View.GONE);
                    recyclerViewOrders.setVisibility(View.VISIBLE);
                    recyclerViewOrders.setAdapter(new OrderAdapter(orders));
                }
            });
        });
    }
}
