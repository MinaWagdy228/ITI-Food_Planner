package com.example.foodplanner.presentation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment implements ViewHome, OnCategoryClicked {

    private FragmentHomeBinding binding;
    private Presenter presenter;
    private CategoriesAdapter categoriesAdapter;

    private Meal randomMeal;

    // For double back press to exit
    private long backPressedTime;
    private Toast backToast;

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

        // Initialize categories RecyclerView first
        setupCategoriesRecycler();

        // Load initial data
        presenter.getRandomMeal();
        presenter.getCategories();

        // Setup SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener(() -> {
            presenter.getRandomMeal();
            presenter.getCategories();
        });

        // Handle back press - exit app on double back
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (backPressedTime + 2000 > System.currentTimeMillis()) {
                            if (backToast != null) {
                                backToast.cancel();
                            }
                            requireActivity().finish();
                        } else {
                            backToast = Toast.makeText(requireContext(),
                                    "Press back again to exit",
                                    Toast.LENGTH_SHORT);
                            backToast.show();
                        }
                        backPressedTime = System.currentTimeMillis();
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload categories when returning (uses cache if available)
        if (presenter != null) {
            presenter.getCategories();
        }
    }

    @Override
    public void showRandomMeal(Meal meal) {
        this.randomMeal = meal;

        binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(requireContext())
                .load(meal.getStrMealThumb())
                .placeholder(android.R.color.transparent)
                .error(android.R.color.transparent)
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
        if (binding != null) {
            binding.loadingCard.setVisibility(View.VISIBLE);
            binding.contentGroup.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null) {
            binding.loadingCard.setVisibility(View.GONE);
            binding.contentGroup.setVisibility(View.VISIBLE);
        }
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