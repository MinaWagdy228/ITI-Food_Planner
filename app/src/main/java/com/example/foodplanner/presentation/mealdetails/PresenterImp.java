package com.example.foodplanner.presentation.mealdetails;

import com.example.foodplanner.data.model.Meal;

public class PresenterImp implements Presenter {
    private ViewMealDetails view;

    public PresenterImp(ViewMealDetails view) {
        this.view = view;
    }

    @Override
    public void getMealDetails(String mealId) {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onFavoriteClicked(Meal meal) {

    }
}
