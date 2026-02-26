package com.example.foodplanner.presentation.filteredmeals;

public interface Presenter {
    void getMealsByCategory(String category);
    void getMealsByArea(String area);
    void getMealsByIngredient(String ingredient);
    void onDestroy();
}
