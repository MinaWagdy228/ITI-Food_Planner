package com.example.foodplanner.data.dataSource.local;


import com.example.foodplanner.data.db.UserDao;
import com.example.foodplanner.data.entity.UserEntity;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class UserLocalDataSource {

    private final UserDao userDao;

    public UserLocalDataSource(UserDao userDao) {
        this.userDao = userDao;
    }

    public Single<Long> registerUser(UserEntity user) {
        return Single.fromCallable(() -> userDao.insertUser(user));
    }

    public Maybe<UserEntity> login(String email, String password) {
        return userDao.login(email, password);
    }

    public Maybe<UserEntity> getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }
}