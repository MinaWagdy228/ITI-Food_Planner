package com.example.foodplanner.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.foodplanner.data.entity.UserEntity;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {

    @Insert
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    Maybe<UserEntity> login(String email, String password);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    Maybe<UserEntity> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id")
    Single<UserEntity> getUserById(int id);

    @Query("DELETE FROM users WHERE id = :id")
    void deleteUserById(int id);
}