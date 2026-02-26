package com.example.foodplanner.presentation.filteredmeals.view;

import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface ViewFilteredMeal {
    void showMeals(List<Meal> meals);
    void showError(String message);
    void showLoading();
    void hideLoading();
}
