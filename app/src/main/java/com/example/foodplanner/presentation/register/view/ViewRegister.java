package com.example.foodplanner.presentation.register.view;

public interface ViewRegister {
    void showLoading();
    void hideLoading();
    void showError(String message);
    void onRegisterSuccess();
}