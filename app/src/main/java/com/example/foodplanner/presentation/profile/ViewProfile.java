package com.example.foodplanner.presentation.profile;


import com.example.foodplanner.db.UserEntity;

public interface ViewProfile {
    void showUserData(UserEntity user);
    void navigateToLogin();
    void showError(String message);
}