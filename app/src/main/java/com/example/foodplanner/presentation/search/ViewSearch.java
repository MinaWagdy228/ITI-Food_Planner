package com.example.foodplanner.presentation.search;

import com.example.foodplanner.data.model.Meal;
import java.util.List;

public interface ViewSearch {
    void showMeals(List<Meal> meals);
    void showEmptyState();
    void showLoading();
    void hideLoading();
    void showError(String message);
}