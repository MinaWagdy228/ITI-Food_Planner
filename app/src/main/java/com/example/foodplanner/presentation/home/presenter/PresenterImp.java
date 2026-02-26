package com.example.foodplanner.presentation.home.presenter;

import android.content.Context;
import android.util.Log;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.model.CategoriesResponse;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.data.model.MealsResponse;
import com.example.foodplanner.presentation.home.view.ViewHome;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class PresenterImp implements Presenter {

    private ViewHome viewHome;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Cache for categories to avoid redundant API calls
    private List<Category> cachedCategories = null;

    public PresenterImp(ViewHome viewHome, Context context) {
        this.viewHome = viewHome;
        this.repository = new Repository(context);
    }

    @Override
    public void getRandomMeal() {
        if (viewHome != null) {
            viewHome.showLoading();
        }

        compositeDisposable.add(
                repository.getRandomMeal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleSuccess,
                                this::handleError
                        )
        );
    }

    private void handleSuccess(MealsResponse response) {
        if (viewHome != null) {
            viewHome.hideLoading();
        }

        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
            Meal meal = response.getMeals().get(0);
            if (viewHome != null) {
                viewHome.showRandomMeal(meal);
            }
        }
    }

    private void handleError(Throwable throwable) {
        if (viewHome != null) {
            viewHome.hideLoading();
            viewHome.showError(throwable.getMessage());
        }
        Log.e("HomePresenter", "API Error: " + throwable.getMessage());
    }

    @Override
    public void getCategories() {
        // Return cached categories if available
        if (cachedCategories != null && !cachedCategories.isEmpty()) {
            if (viewHome != null) {
                viewHome.showCategories(cachedCategories);
            }
            return;
        }

        compositeDisposable.add(
                repository.getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleCategoriesSuccess,
                                this::handleError
                        )
        );
    }

    private void handleCategoriesSuccess(CategoriesResponse response) {
        if (response != null && response.getCategories() != null) {
            List<Category> categories = response.getCategories();

            // Cache the categories
            cachedCategories = categories;

            if (viewHome != null) {
                viewHome.showCategories(categories);
            }
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        cachedCategories = null;
        viewHome = null;
    }
}