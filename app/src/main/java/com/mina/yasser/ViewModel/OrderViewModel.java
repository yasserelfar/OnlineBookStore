package com.mina.yasser.ViewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mina.yasser.DataBase.AppDatabase;
import com.mina.yasser.DataBase.Order;

import java.util.List;

public class OrderViewModel extends AndroidViewModel {
    private AppDatabase database;

    public OrderViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
    }

    public LiveData<List<Order>> getOrdersByDate(String date) {
        return database.orderDao().getOrdersByDate(date); // You may need to add this query to the DAO
    }
}
