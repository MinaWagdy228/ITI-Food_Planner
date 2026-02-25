package com.example.foodplanner.presentation.login;

import android.content.Context;
import android.text.TextUtils;


import com.example.foodplanner.data.model.dataSource.local.SessionManager;
import com.example.foodplanner.data.model.dataSource.local.UserLocalDataSource;
import com.example.foodplanner.db.AppDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImp implements LoginPresenter {

    private final ViewLogin view;
    private final UserLocalDataSource localDataSource;
    private final SessionManager sessionManager;
    private final CompositeDisposable compositeDisposable;

    public LoginPresenterImp(ViewLogin view, Context context) {
        this.view = view;
        AppDatabase db = AppDatabase.getInstance(context);
        this.localDataSource = new UserLocalDataSource(db.userDao());
        this.sessionManager = new SessionManager(context);
        this.compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void login(String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            view.showError("Email and password required");
            return;
        }

        view.showLoading();

        compositeDisposable.add(
                localDataSource.login(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    view.hideLoading();

                                    sessionManager.saveUserId(user.getId());

                                    view.onLoginSuccess();
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError("Login failed");
                                },
                                () -> {
                                    view.hideLoading();
                                    view.showError("Invalid credentials");
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
    }
}