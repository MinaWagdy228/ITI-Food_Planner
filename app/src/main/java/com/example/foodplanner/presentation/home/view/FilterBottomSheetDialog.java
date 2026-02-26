package com.example.foodplanner.presentation.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.foodplanner.data.model.Category;
import com.example.foodplanner.databinding.BottomSheetFilterBinding;
import com.example.foodplanner.presentation.home.presenter.Presenter;
import com.example.foodplanner.presentation.home.presenter.PresenterImp;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment implements OnCategoryClicked, ViewHome {

    private BottomSheetFilterBinding binding;
    private CategoriesAdapter categoriesAdapter;
    private Presenter presenter;

    // Popular areas
    private static final String[] AREAS = {
            "American", "British", "Canadian", "Chinese", "Croatian", "Dutch",
            "Egyptian", "French", "Greek", "Indian", "Irish", "Italian",
            "Jamaican", "Japanese", "Kenyan", "Malaysian", "Mexican", "Moroccan",
            "Polish", "Portuguese", "Russian", "Spanish", "Thai", "Tunisian",
            "Turkish", "Vietnamese"
    };

    // Popular ingredients
    private static final String[] INGREDIENTS = {
            "Chicken", "Beef", "Pork", "Salmon", "Tuna", "Pasta",
            "Rice", "Potato", "Tomato", "Cheese", "Egg", "Garlic",
            "Onion", "Mushroom", "Pepper", "Lemon"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategoriesRecyclerView();
        setupAreasChips();
        setupIngredientsChips();
        setupPresenter();
    }

    private void setupCategoriesRecyclerView() {
        categoriesAdapter = new CategoriesAdapter(this::onCategoryClicked);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        binding.rvCategories.setAdapter(categoriesAdapter);
    }

    private void setupAreasChips() {
        for (String area : AREAS) {
            Chip chip = new Chip(requireContext());
            chip.setText(area);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    onAreaSelected(area);
                }
            });
            binding.chipGroupAreas.addView(chip);
        }
    }

    private void setupIngredientsChips() {
        for (String ingredient : INGREDIENTS) {
            Chip chip = new Chip(requireContext());
            chip.setText(ingredient);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                if (chip.isChecked()) {
                    onIngredientSelected(ingredient);
                }
            });
            binding.chipGroupIngredients.addView(chip);
        }
    }

    private void setupPresenter() {
        presenter = new PresenterImp(this , requireContext());
        presenter.getCategories();
    }

    @Override
    public void onCategoryClicked(String categoryName) {
        dismiss();
        if (getActivity() != null) {
            androidx.navigation.NavController navController =
                androidx.navigation.Navigation.findNavController(getActivity(), com.example.foodplanner.R.id.nav_host_fragment);

            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("filterValue", categoryName);
            bundle.putString("filterType", "category");
            navController.navigate(com.example.foodplanner.R.id.filteredMealsFragment, bundle);
        }
    }

    private void onAreaSelected(String area) {
        dismiss();
        if (getActivity() != null) {
            androidx.navigation.NavController navController =
                androidx.navigation.Navigation.findNavController(getActivity(), com.example.foodplanner.R.id.nav_host_fragment);

            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("filterValue", area);
            bundle.putString("filterType", "area");
            navController.navigate(com.example.foodplanner.R.id.filteredMealsFragment, bundle);
        }
    }

    private void onIngredientSelected(String ingredient) {
        dismiss();
        if (getActivity() != null) {
            androidx.navigation.NavController navController =
                androidx.navigation.Navigation.findNavController(getActivity(), com.example.foodplanner.R.id.nav_host_fragment);

            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("filterValue", ingredient);
            bundle.putString("filterType", "ingredient");
            navController.navigate(com.example.foodplanner.R.id.filteredMealsFragment, bundle);
        }
    }

    @Override
    public void showCategories(java.util.List<Category> categories) {
        categoriesAdapter.setCategories(categories);
        binding.progressCategories.setVisibility(View.GONE);
    }

    @Override
    public void showRandomMeal(com.example.foodplanner.data.model.Meal meal) {
        // Not needed for filter
    }

    @Override
    public void showLoading() {
        binding.progressCategories.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        binding.progressCategories.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        binding.progressCategories.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        binding = null;
    }
}










