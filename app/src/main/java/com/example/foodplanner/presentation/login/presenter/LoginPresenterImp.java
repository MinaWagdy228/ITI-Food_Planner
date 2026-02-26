package com.example.foodplanner.presentation.login.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.example.foodplanner.data.Repository;
import com.example.foodplanner.presentation.login.view.ViewLogin;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImp implements LoginPresenter {

    private final ViewLogin view;
    private final Repository repository;
    private final CompositeDisposable compositeDisposable;

    public LoginPresenterImp(ViewLogin view, Context context) {
        this.view = view;
        this.repository = new Repository(context);
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
                repository.login(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    view.hideLoading();

                                    repository.saveUserId(user.getId());

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