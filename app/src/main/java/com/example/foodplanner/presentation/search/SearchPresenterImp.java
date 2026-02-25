package com.example.foodplanner.presentation.search;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.network.MealApiServices;
import com.example.foodplanner.network.Network;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImp implements SearchPresenter {

    private final ViewSearch view;
    private final MealApiServices api;
    private final CompositeDisposable compositeDisposable;

    public SearchPresenterImp(ViewSearch view) {
        this.view = view;
        this.api = Network.getApiService();
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadAllMeals() {
        view.showLoading();

        List<Meal> allMeals = new ArrayList<>();

        // Load meals by first letters (a–z)
        for (char c = 'a'; c <= 'c'; c++) { // start small for performance
            compositeDisposable.add(
                    api.searchMealsByName(String.valueOf(c))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if (response != null && response.getMeals() != null) {
                                    allMeals.addAll(response.getMeals());
                                    view.showMeals(allMeals);
                                }
                                view.hideLoading();
                            }, throwable -> view.showError("Failed to load meals"))
            );
        }
    }

    @Override
    public void searchMeals(String query) {

        if (query == null || query.trim().isEmpty()) {
            loadAllMeals();
            return;
        }

        compositeDisposable.add(
                api.searchMealsByName(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            if (response != null && response.getMeals() != null) {
                                view.showMeals(response.getMeals());
                            } else {
                                view.showEmptyState();
                            }
                        }, throwable -> view.showError("Search failed"))
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}