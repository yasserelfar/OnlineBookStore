package com.mina.yasser.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FeedbackDao {

    // Insert new feedback
    @Insert
    void insertFeedback(Feedback feedback);

    // Get all feedback for a specific product
    @Query("SELECT * FROM feedback WHERE productId = :productId")
    List<Feedback> getFeedbackForProduct(int productId);

    // Get all feedback for a specific order
    @Query("SELECT * FROM feedback WHERE orderId = :orderId")
    List<Feedback> getFeedbackForOrder(int orderId);

    // Get all feedback from a specific user
    @Query("SELECT * FROM feedback WHERE userId = :userId")
    List<Feedback> getFeedbackForUser(int userId);
}
