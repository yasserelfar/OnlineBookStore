package com.mina.yasser.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OrderItemDao {

    // إدراج عنصر في الطلب
    @Insert
    void insert(OrderItem orderItem);

    // استرجاع جميع العناصر المرتبطة بالطلب
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    List<OrderItem> getOrderItemsByOrderId(int orderId);

    // استرجاع جميع العناصر المرتبطة بمنتج معين
    @Query("SELECT * FROM order_items WHERE productBarcode = :productBarcode")
    List<OrderItem> getOrderItemsByProductBarcode(String productBarcode);
}
