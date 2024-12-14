package com.mina.yasser.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OrderDetailDao {

    // Insert a new order detail
    @Insert
    void insert(OrderDetail orderDetail);

    // Get all order details for a specific orderId
    @Query("SELECT * FROM `order_details` WHERE orderId = :orderId")
    List<OrderDetail> getOrderDetailsByOrderId(int orderId);

    // Get all order details in the database (useful for admin or debugging purposes)
    @Query("SELECT * FROM `order_details`")
    List<OrderDetail> getAllOrderDetails();

    // Optionally, delete all order details for a specific order (e.g., for order cancellation)
    @Query("DELETE FROM `order_details` WHERE orderId = :orderId")
    void deleteOrderDetailsByOrderId(int orderId);
}
