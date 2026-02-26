package com.example.foodplanner.presentation.favorites.presenter;

import android.content.Context;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.data.entity.FavoriteMealEntity;
import com.example.foodplanner.presentation.favorites.view.ViewFavourite;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouritePresenterImp implements FavouritePresenter {

    private final ViewFavourite view;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable;

    public FavouritePresenterImp(ViewFavourite view, Context context) {
        this.view = view;
        this.repository = new Repository(context);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadFavorites() {
        int userId = repository.getCurrentUserId();

        view.showLoading();

        compositeDisposable.add(
                repository.getFavoritesByUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleFavorites,
                                throwable -> {
                                    view.hideLoading();
                                    view.showError("Failed to load favorites");
                                }
                        )
        );
    }

    private void handleFavorites(List<FavoriteMealEntity> favorites) {
        view.hideLoading();
        if (favorites == null || favorites.isEmpty()) {
            view.showEmptyState();
        } else {
            view.showFavorites(favorites);
        }
    }

    @Override
    public void removeFavorite(FavoriteMealEntity meal) {
        int userId = repository.getCurrentUserId();

        compositeDisposable.add(
                repository.deleteFavorite(meal.getIdMeal(), userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Reload favorites after removal
                                    loadFavorites();
                                },
                                error -> view.showError("Failed to remove favorite")
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}