package com.mina.yasser.DataBase;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "product", foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId", onDelete = ForeignKey.CASCADE))
public class Product {

    @PrimaryKey
    @NonNull
    private String barcode;

    private String name;
    private String author;
    private int categoryId;  // Store the Category ID (Foreign Key)
    private double price;
    private int quantityInStock;
    private int popularity;

    // Getters and Setters
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    // Optional: Override toString for debugging/logging purposes
    @Override
    public String toString() {
        return "Product{" +
                "barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", quantityInStock=" + quantityInStock +
                ", popularity=" + popularity +
                '}';
    }
}
