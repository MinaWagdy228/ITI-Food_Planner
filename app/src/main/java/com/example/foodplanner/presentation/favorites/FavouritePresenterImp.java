package com.example.foodplanner.presentation.favorites;

import android.content.Context;


import com.example.foodplanner.data.model.dataSource.local.FavoriteLocalDataSource;
import com.example.foodplanner.data.model.dataSource.local.SessionManager;
import com.example.foodplanner.db.AppDatabase;
import com.example.foodplanner.db.FavoriteMealEntity;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavouritePresenterImp implements FavouritePresenter {

    private final ViewFavourite view;
    private final FavoriteLocalDataSource localDataSource;
    private final SessionManager sessionManager;
    private final CompositeDisposable compositeDisposable;

    public FavouritePresenterImp(ViewFavourite view, Context context) {
        this.view = view;
        AppDatabase db = AppDatabase.getInstance(context);
        this.localDataSource = new FavoriteLocalDataSource(db.favoriteMealDao());
        this.sessionManager = new SessionManager(context);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void loadFavorites() {

        int userId = sessionManager.getUserId();

        compositeDisposable.add(
                localDataSource.getFavoritesByUser(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::handleFavorites,
                                throwable -> view.showError("Failed to load favorites")
                        )
        );
    }

    private void handleFavorites(List<FavoriteMealEntity> favorites) {
        if (favorites == null || favorites.isEmpty()) {
            view.showEmptyState();
        } else {
            view.showFavorites(favorites);
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}