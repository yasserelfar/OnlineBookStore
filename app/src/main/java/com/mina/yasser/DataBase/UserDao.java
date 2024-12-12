package com.mina.yasser.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM user WHERE userId = :id")
    User getUserById(int id);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM User WHERE username = :usernameOrEmail OR email = :usernameOrEmail LIMIT 1")
    User getUserByUsernameOrEmail(String usernameOrEmail);

    @Update
    void update(User user);
    @Query("SELECT * FROM User WHERE username = :email  LIMIT 1")
    User getUserByEmail(String email);
}
