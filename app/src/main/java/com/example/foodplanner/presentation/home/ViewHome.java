package com.example.foodplanner.presentation.home;

import com.example.foodplanner.data.model.Meal;

public interface ViewHome {
    void showRandomMeal(Meal meal);
    void showError(String message);
    void showLoading();
    void hideLoading();
}
