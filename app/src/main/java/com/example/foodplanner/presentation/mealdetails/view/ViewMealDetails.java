package com.example.foodplanner.presentation.mealdetails.view;

import com.example.foodplanner.data.model.Meal;

public interface ViewMealDetails {
    void showMealDetails(Meal meal);
    void showError(String message);
    void showLoading();
    void hideLoading();
}
