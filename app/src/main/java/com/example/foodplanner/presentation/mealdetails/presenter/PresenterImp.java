package com.example.foodplanner.presentation.mealdetails.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.data.mapper.FavoriteMapper;
import com.example.foodplanner.presentation.mealdetails.view.ViewMealDetails;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PresenterImp implements Presenter {

    private final ViewMealDetails view;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable;
    private final int currentUserId;

    public PresenterImp(ViewMealDetails view, Context context) {
        this.view = view;
        this.repository = new Repository(context);
        this.compositeDisposable = new CompositeDisposable();
        this.currentUserId = repository.getCurrentUserId();
    }

    @Override
    public void getMealDetails(String mealId) {
        view.showLoading();

        compositeDisposable.add(
                repository.getMealDetailsById(mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleSuccess,
                                this::handleError
                        )
        );
    }

    private void handleError(Throwable throwable) {
        view.hideLoading();
        Log.e("MealDetailsError", throwable.getMessage());
        view.showError("Failed to load meal details");
    }

    private void handleSuccess(MealsResponse response) {
        view.hideLoading();

        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
            Meal meal = response.getMeals().get(0);

            // Check favorite state from Repository
            observeFavoriteState(meal);

        } else {
            view.showError("Meal not found");
        }
    }

    private void observeFavoriteState(Meal meal) {
        compositeDisposable.add(
                repository.isMealFavorite(meal.getIdMeal(), currentUserId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isFav -> {
                            meal.setFavorite(isFav);
                            view.showMealDetails(meal);
                        }, throwable -> {
                            Log.e("FavoriteCheck", throwable.getMessage());
                            view.showMealDetails(meal);
                        })
        );
    }

    @Override
    public void onFavoriteClicked(Meal meal) {

        boolean newState = !meal.isFavorite();
        meal.setFavorite(newState);

        compositeDisposable.add(
                Completable.fromAction(() -> {
                            if (newState) {
                                // INSERT favorite via Repository
                                repository.insertFavorite(
                                        FavoriteMapper.toEntity(meal, currentUserId)
                                ).blockingAwait();
                            } else {
                                // DELETE favorite via Repository
                                repository.deleteFavorite(
                                        meal.getIdMeal(),
                                        currentUserId
                                ).blockingAwait();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showMealDetails(meal),
                                throwable -> Log.e("FavoriteError", throwable.getMessage())
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }

}