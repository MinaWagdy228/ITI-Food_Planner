package com.example.foodplanner.presentation.register;

public interface RegisterPresenter {
    void register(String name, String email, String password, String confirmPassword);
    void onDestroy();
}