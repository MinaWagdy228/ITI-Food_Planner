package com.example.foodplanner.presentation.mealdetails;

import android.content.Context;
import android.util.Log;


import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.data.model.dataSource.local.FavoriteLocalDataSource;
import com.example.foodplanner.data.model.dataSource.local.SessionManager;
import com.example.foodplanner.data.model.mapper.FavoriteMapper;
import com.example.foodplanner.db.AppDatabase;
import com.example.foodplanner.network.MealApiServices;
import com.example.foodplanner.network.Network;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PresenterImp implements Presenter {

    private final ViewMealDetails view;
    private final MealApiServices apiServices;
    private final CompositeDisposable compositeDisposable;
    private final FavoriteLocalDataSource localDataSource;
    private final SessionManager sessionManager;
    private final int currentUserId;

    public PresenterImp(ViewMealDetails view, Context context) {
        this.view = view;
        this.apiServices = Network.getApiService();
        this.compositeDisposable = new CompositeDisposable();

        AppDatabase db = AppDatabase.getInstance(context);
        this.localDataSource = new FavoriteLocalDataSource(db.favoriteMealDao());

        this.sessionManager = new SessionManager(context);
        this.currentUserId = sessionManager.getUserId();
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

    private void handleError(Throwable throwable) {
        view.hideLoading();
        Log.e("MealDetailsError", throwable.getMessage());
        view.showError("Failed to load meal details");
    }

    private void handleSuccess(MealsResponse response) {
        view.hideLoading();

        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
            Meal meal = response.getMeals().get(0);

            // 🔥 Check favorite state from Room
            observeFavoriteState(meal);

        } else {
            view.showError("Meal not found");
        }
    }

    private void observeFavoriteState(Meal meal) {
        compositeDisposable.add(
                localDataSource.isFavorite(meal.getIdMeal(), currentUserId)
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
                io.reactivex.rxjava3.core.Completable.fromAction(() -> {
                            if (newState) {
                                // INSERT favorite
                                localDataSource.insertFavorite(
                                        FavoriteMapper.toEntity(meal, currentUserId)
                                );
                            } else {
                                // DELETE favorite
                                localDataSource.deleteFavorite(
                                        meal.getIdMeal(),
                                        currentUserId
                                );
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