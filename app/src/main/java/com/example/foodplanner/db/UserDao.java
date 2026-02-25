package com.example.foodplanner.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Maybe;

@Dao
public interface UserDao {

    @Insert
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    Maybe<UserEntity> login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    Maybe<UserEntity> getUserByEmail(String email);
}