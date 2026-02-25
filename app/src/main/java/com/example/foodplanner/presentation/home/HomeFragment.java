package com.example.foodplanner.presentation.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment implements ViewHome, OnCategoryClicked {

    private FragmentHomeBinding binding;
    private Presenter presenter;
    private CategoriesAdapter categoriesAdapter;

    private Meal randomMeal;

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
        presenter.getRandomMeal();

        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener(() -> {
            presenter.getRandomMeal();
            presenter.getCategories();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragment", "onResume: triggered");
        if (presenter != null) {
            presenter.getCategories();
            Log.d("HomeFragment", "onResume: Refreshed categories");
        }
    }

    @Override
    public void showRandomMeal(Meal meal) {
        this.randomMeal = meal;

        binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .into(binding.imgMeal);

        binding.cardMealOfDay.setOnClickListener(v -> {
            if (randomMeal != null && randomMeal.getIdMeal() != null) {

                HomeFragmentDirections.ActionHomeFragmentToMealDetailsFragment action =
                        HomeFragmentDirections
                                .actionHomeFragmentToMealDetailsFragment(randomMeal.getIdMeal());

                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(action);
            }
        });
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_SHORT).show();

        // Stop refreshing animation on error
        if (binding.swipeRefresh.isRefreshing()) {
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void showLoading() {
        // Optional: show ProgressBar here
    }

    @Override
    public void hideLoading() {
        // Optional: hide ProgressBar later
    }

    private void setupCategoriesRecycler() {
        categoriesAdapter = new CategoriesAdapter(this);
        binding.rvCategories.setAdapter(categoriesAdapter);
        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
    }

    @Override
    public void showCategories(List<Category> categories) {
        if (categoriesAdapter == null) {
            setupCategoriesRecycler();
        }
        categoriesAdapter.setCategories(categories);

        // Stop refreshing animation
        if (binding.swipeRefresh.isRefreshing()) {
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        binding = null;
    }

    @Override
    public void onCategoryClicked(String categoryName) {
        HomeFragmentDirections.ActionHomeFragmentToFilteredMealsFragment action =
                HomeFragmentDirections.actionHomeFragmentToFilteredMealsFragment(categoryName);

        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(action);
    }
}