package com.example.foodplanner.presentation.mealdetails;

import android.util.Log;

import com.example.foodplanner.network.Network;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.network.MealApiServices;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PresenterImp implements Presenter {

    private final ViewMealDetails view;
    private final MealApiServices apiServices;
    private final CompositeDisposable compositeDisposable;

    public PresenterImp(ViewMealDetails view) {
        this.view = view;
        this.apiServices = Network.getApiService();
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getMealDetails(String mealId) {

        view.showLoading();

        compositeDisposable.add(
                apiServices.getMealDetailsById(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleSuccess,
                                this::handleError
                        )
        );
    }

    private void handleSuccess(MealsResponse response) {

        view.hideLoading();

        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
            Meal meal = response.getMeals().get(0);
            view.showMealDetails(meal);
        } else {
            view.showError("Meal not found.");
        }
    }

    private void handleError(Throwable throwable) {
        view.hideLoading();
        Log.e("MealDetailsPresenter", throwable.getMessage());
        view.showError("Error loading meal details.");
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        boolean newState = !meal.isFavorite();
        meal.setFavorite(newState);

        // TODO: Insert/Delete from Room here


        view.showMealDetails(meal);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}