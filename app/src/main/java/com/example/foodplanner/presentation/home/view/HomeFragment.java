package com.example.foodplanner.presentation.home.view;

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
import com.example.foodplanner.presentation.common.OnMealClickListener;
import com.example.foodplanner.presentation.filteredmeals.view.MealsAdapter;
import com.example.foodplanner.presentation.home.presenter.Presenter;
import com.example.foodplanner.presentation.home.presenter.PresenterImp;
import com.example.foodplanner.presentation.search.presenter.SearchPresenter;
import com.example.foodplanner.presentation.search.presenter.SearchPresenterImp;
import com.example.foodplanner.presentation.search.view.ViewSearch;

import java.util.List;

public class HomeFragment extends Fragment implements ViewHome, OnCategoryClicked, ViewSearch, OnMealClickListener {

    private FragmentHomeBinding binding;
    private Presenter presenter;
    private CategoriesAdapter categoriesAdapter;

    // Search suggestions
    private SearchPresenter searchPresenter;
    private MealsAdapter searchSuggestionsAdapter;

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

        presenter = new PresenterImp(this, requireContext());

        // Initialize categories RecyclerView first
        setupCategoriesRecycler();

        // Initialize search functionality
        setupSearchSuggestions();
        setupSearchClickListeners();

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

    // ===== Search Suggestions Methods =====

    private void setupSearchSuggestions() {
        // Initialize search presenter
        searchPresenter = new SearchPresenterImp(this, requireContext());

        // Setup suggestions RecyclerView
        searchSuggestionsAdapter = new MealsAdapter(this);
        binding.rvSearchSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSearchSuggestions.setAdapter(searchSuggestionsAdapter);
    }

    private void setupSearchClickListeners() {
        // Open search bottom sheet when clicking the search bar
        binding.cardSearch.setOnClickListener(v -> {
            SearchBottomSheetDialog searchDialog = new SearchBottomSheetDialog();
            searchDialog.show(getChildFragmentManager(), "SearchBottomSheet");
        });

        // Open filter bottom sheet when clicking the filter icon
        View filterIcon = binding.cardSearch.getChildAt(0); // LinearLayout
        if (filterIcon instanceof android.view.ViewGroup) {
            android.view.ViewGroup linearLayout = (android.view.ViewGroup) filterIcon;
            View icon = linearLayout.getChildAt(linearLayout.getChildCount() - 1); // Last child is filter icon
            icon.setOnClickListener(v -> {
                FilterBottomSheetDialog filterDialog = new FilterBottomSheetDialog();
                filterDialog.show(getChildFragmentManager(), "FilterBottomSheet");
            });
        }
    }

    // ViewSearch implementation for search suggestions
    @Override
    public void showMeals(List<Meal> meals) {
        if (meals != null && !meals.isEmpty()) {
            searchSuggestionsAdapter.setMeals(meals);
            binding.rvSearchSuggestions.setVisibility(View.VISIBLE);
        } else {
            binding.rvSearchSuggestions.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyState() {
        binding.rvSearchSuggestions.setVisibility(View.GONE);
    }

    // OnMealClickListener implementation for suggestions
    @Override
    public void onMealClick(Meal meal) {
        binding.rvSearchSuggestions.setVisibility(View.GONE);
        HomeFragmentDirections.ActionHomeFragmentToMealDetailsFragment action =
                HomeFragmentDirections.actionHomeFragmentToMealDetailsFragment(meal.getIdMeal());
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
        if (searchPresenter != null) {
            searchPresenter.onDestroy();
        }
        binding = null;
    }

    @Override
    public void onCategoryClicked(String categoryName) {
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString("filterValue", categoryName);
        bundle.putString("filterType", "category");

        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(com.example.foodplanner.R.id.filteredMealsFragment, bundle);
    }
}