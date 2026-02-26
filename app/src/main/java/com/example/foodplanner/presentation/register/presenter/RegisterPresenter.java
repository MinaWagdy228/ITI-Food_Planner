package com.example.foodplanner.presentation.register.presenter;

public interface RegisterPresenter {
    void register(String name, String email, String password, String confirmPassword);
    void onDestroy();
}