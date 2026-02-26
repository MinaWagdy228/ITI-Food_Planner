package com.example.foodplanner.presentation.profile.view;


import com.example.foodplanner.data.entity.UserEntity;

public interface ViewProfile {
    void showUserData(UserEntity user);
    void navigateToLogin();
    void showError(String message);
}