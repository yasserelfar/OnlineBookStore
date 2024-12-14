package com.mina.yasser.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details")
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    private int orderDetailId;
    private int orderId;  // Foreign key reference to Order
    private String productbarcode;
    private String productName;
    private int quantity;
    private double price;

    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }


    public String getProductbarcode() {
        return productbarcode;
    }

    public void setProductbarcode(String productbarcode) {
        this.productbarcode = productbarcode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
