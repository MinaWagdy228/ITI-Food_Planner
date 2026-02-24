package com.example.foodplanner.presentation.filteredmeals;

public interface Presenter {
    void getMealsByCategory(String category);
    void onDestroy();
}
