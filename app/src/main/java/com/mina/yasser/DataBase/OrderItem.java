package com.mina.yasser.DataBase;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = {
                @ForeignKey(entity = Order.class,
                        parentColumns = "orderId",
                        childColumns = "orderId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class,
                        parentColumns = "barcode",
                        childColumns = "productBarcode",
                        onDelete = ForeignKey.CASCADE)
        })
public class OrderItem {
    @PrimaryKey(autoGenerate = true)
    private int id; // مفتاح أساسي لكل عنصر في الطلب

    private int orderId; // مفتاح خارجي مرتبط بالطلب
    private String productBarcode; // مفتاح خارجي مرتبط بالمنتج
    private int quantity; // الكمية المطلوبة من المنتج
    private double price; // السعر الخاص بالمنتج في هذا الطلب

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
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
