package com.example.foodplanner.presentation.filteredmeals;

import com.example.foodplanner.data.model.Meal;

public interface Presenter {
    void getMealsByCategory(String category);
    void getMealsByArea(String area);
    void getMealsByIngredient(String ingredient);
    void onFavoriteClicked(Meal meal);
    void onDestroy();
}
