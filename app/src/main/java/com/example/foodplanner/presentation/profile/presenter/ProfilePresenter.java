package com.example.foodplanner.presentation.profile.presenter;

public interface ProfilePresenter {
    void loadUserData();
    void logout();
    void deleteAccount();
    void onDestroy();
}