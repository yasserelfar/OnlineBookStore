package com.mina.yasser.DataBase;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "orders")

public class Order {
    @PrimaryKey(autoGenerate = true)
    private int orderId;
    private int userId;
    private String date;
    private String status;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
