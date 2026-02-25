package com.example.foodplanner.presentation.profile;

public interface ProfilePresenter {
    void loadUserData();
    void logout();
    void deleteAccount();
    void onDestroy();
}