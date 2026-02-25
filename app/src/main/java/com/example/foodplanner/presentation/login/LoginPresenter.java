package com.example.foodplanner.presentation.login;

public interface LoginPresenter {
    void login(String email, String password);
    void onDestroy();
}