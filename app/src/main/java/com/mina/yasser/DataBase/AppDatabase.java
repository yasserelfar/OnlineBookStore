package com.mina.yasser.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;

@Database(entities = {User.class, Product.class, OrderDetail.class,Cart.class, Order.class, Feedback.class,OrderItem.class, Category.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public static Executor databaseWriteExecutor;
    public static SQLiteDatabase database;
    private static AppDatabase instance;
    public abstract OrderDetailDao orderDetailDao();
    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();
    public abstract FeedbackDao feedbackDao();
    public abstract CategoryDao categoryDao(); // Add the CategoryDao
    public abstract OrderItemDao orderItemDao();
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "bookstore_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }


}
