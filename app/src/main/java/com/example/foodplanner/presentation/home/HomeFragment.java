package com.example.foodplanner.presentation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements ViewHome {

    private FragmentHomeBinding binding;
    private Presenter presenter;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public android.view.View onCreateView(@NonNull LayoutInflater inflater,
                                          ViewGroup container,
                                          Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new PresenterImp(this);
        presenter.getRandomMeal(); // MVP call (NOT direct Network call)
    }

    @Override
    public void showRandomMeal(Meal meal) {
        binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .into(binding.imgMeal);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        // Optional: show ProgressBar here
    }

    @Override
    public void hideLoading() {
        // Optional: hide ProgressBar later
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        binding = null;
    }
}