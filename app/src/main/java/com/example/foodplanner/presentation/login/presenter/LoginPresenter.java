package com.example.foodplanner.presentation.login.presenter;

public interface LoginPresenter {
    void login(String email, String password);
    void onDestroy();
}