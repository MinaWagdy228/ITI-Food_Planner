package com.example.foodplanner.presentation.login;

public interface ViewLogin {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void onLoginSuccess();
}