package com.mina.yasser.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OrderDao {

    // Insert a new order
    @Insert
    void insertOrder(Order order);

    // Update an existing order (e.g., to change the status or details)
    @Update
    void updateOrder(Order order);

    // Get all orders for a specific user
    @Query("SELECT * FROM `orders` WHERE userId = :userId")
    List<Order> getUserOrders(int userId);

    // Get a specific order by ID
    @Query("SELECT * FROM `orders` WHERE orderId = :id")
    Order getOrderById(int id);

    // Get all orders
    @Query("SELECT * FROM `orders`")
    List<Order> getAllOrders();

    // Get all orders for a specific user with their book names
    @Query("SELECT * FROM `orders` WHERE userName = :userName")
    LiveData<List<Order>> getOrdersByUserName(String userName);



}
