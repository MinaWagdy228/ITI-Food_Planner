package com.example.foodplanner.presentation.home;

import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;

import java.util.List;

public interface ViewHome {
    void showRandomMeal(Meal meal);
    void showError(String message);
    void showLoading();
    void hideLoading();
    void showCategories(List<Category> categories);

}
