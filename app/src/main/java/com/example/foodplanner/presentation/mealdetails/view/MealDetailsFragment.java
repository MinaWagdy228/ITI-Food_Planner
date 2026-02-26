package com.example.foodplanner.presentation.mealdetails.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.FragmentMealDetailsBinding;
import com.example.foodplanner.presentation.mealdetails.presenter.Presenter;
import com.example.foodplanner.presentation.mealdetails.presenter.PresenterImp;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsFragment extends Fragment
        implements ViewMealDetails, OnFavoriteClickListener {

    private FragmentMealDetailsBinding binding;
    private MealDetailsAdapter adapter;
    private Presenter presenter;
    private String mealId;

    public MealDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMealDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get mealId from arguments
        if (getArguments() != null) {
            mealId = getArguments().getString("mealId");
        }

        setupRecyclerView();

        presenter = new PresenterImp(this, requireContext());
        presenter.getMealDetails(mealId);
    }

    private void setupRecyclerView() {
        adapter = new MealDetailsAdapter(this); // interface passed to adapter
        binding.rvMealDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMealDetails.setAdapter(adapter);
    }

    @Override
    public void showMealDetails(Meal meal) {
        List<DetailItem> detailItems = buildDetailItems(meal);
        adapter.setItems(detailItems);
    }

    private List<DetailItem> buildDetailItems(Meal meal) {
        List<DetailItem> items = new ArrayList<>();

        // Header (Image + Title + Favorite Icon)
        items.add(new DetailItem(DetailItem.TYPE_HEADER, meal));

        // Section Header: Ingredients
        items.add(new DetailItem(DetailItem.TYPE_SECTION_HEADER, "Required Ingredients"));

        //  Ingredients with Measures (Dynamic extraction 1–20)
        addIngredientWithMeasure(items, meal.getStrIngredient1(), meal.getStrMeasure1());
        addIngredientWithMeasure(items, meal.getStrIngredient2(), meal.getStrMeasure2());
        addIngredientWithMeasure(items, meal.getStrIngredient3(), meal.getStrMeasure3());
        addIngredientWithMeasure(items, meal.getStrIngredient4(), meal.getStrMeasure4());
        addIngredientWithMeasure(items, meal.getStrIngredient5(), meal.getStrMeasure5());
        addIngredientWithMeasure(items, meal.getStrIngredient6(), meal.getStrMeasure6());
        addIngredientWithMeasure(items, meal.getStrIngredient7(), meal.getStrMeasure7());
        addIngredientWithMeasure(items, meal.getStrIngredient8(), meal.getStrMeasure8());
        addIngredientWithMeasure(items, meal.getStrIngredient9(), meal.getStrMeasure9());
        addIngredientWithMeasure(items, meal.getStrIngredient10(), meal.getStrMeasure10());
        addIngredientWithMeasure(items, meal.getStrIngredient11(), meal.getStrMeasure11());
        addIngredientWithMeasure(items, meal.getStrIngredient12(), meal.getStrMeasure12());
        addIngredientWithMeasure(items, meal.getStrIngredient13(), meal.getStrMeasure13());
        addIngredientWithMeasure(items, meal.getStrIngredient14(), meal.getStrMeasure14());
        addIngredientWithMeasure(items, meal.getStrIngredient15(), meal.getStrMeasure15());
        addIngredientWithMeasure(items, meal.getStrIngredient16(), meal.getStrMeasure16());
        addIngredientWithMeasure(items, meal.getStrIngredient17(), meal.getStrMeasure17());
        addIngredientWithMeasure(items, meal.getStrIngredient18(), meal.getStrMeasure18());
        addIngredientWithMeasure(items, meal.getStrIngredient19(), meal.getStrMeasure19());
        addIngredientWithMeasure(items, meal.getStrIngredient20(), meal.getStrMeasure20());

        // Instructions Section
        if (!TextUtils.isEmpty(meal.getStrInstructions())) {
            // Section Header: Instructions
            items.add(new DetailItem(DetailItem.TYPE_SECTION_HEADER, "Meal Instructions"));

            items.add(new DetailItem(
                    DetailItem.TYPE_INSTRUCTIONS,
                    meal.getStrInstructions()
            ));
        }

        // YouTube Section (Embedded Player)
        if (!TextUtils.isEmpty(meal.getStrYoutube())) {
            // Section Header: Video Tutorial
            items.add(new DetailItem(DetailItem.TYPE_SECTION_HEADER, "Video Tutorial"));

            items.add(new DetailItem(
                    DetailItem.TYPE_YOUTUBE,
                    meal.getStrYoutube()
            ));
        }

        return items;
    }

    private void addIngredientWithMeasure(List<DetailItem> items, String ingredientName, String measure) {
        if (!TextUtils.isEmpty(ingredientName) && !ingredientName.equals("null") &&
                !TextUtils.isEmpty(measure) && !measure.equals("null")) {
            Ingredient ingredient = new Ingredient(ingredientName, measure);
            items.add(new DetailItem(DetailItem.TYPE_INGREDIENT, ingredient));
        }
    }

    // Favorite Click → UI → Presenter
    @Override
    public void onFavoriteClicked(Meal meal) {
        if (meal.isFavorite()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Remove from Favorites")
                    .setMessage("Are you sure you want to remove this meal from your favorites?")
                    .setPositiveButton("Yes", (dialog, which) -> presenter.onFavoriteClicked(meal))
                    .setNegativeButton("No", null)
                    .show();
        } else {
            presenter.onFavoriteClicked(meal);
        }

    }

    @Override
    public void showError(String message) {
        if (binding != null) {
            android.widget.Toast.makeText(requireContext(),
                    "Error: " + message,
                    android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.loadingCard.setVisibility(android.view.View.VISIBLE);
            binding.rvMealDetails.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null && binding.loadingCard != null) {
            binding.loadingCard.setVisibility(android.view.View.GONE);
            binding.rvMealDetails.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.onDestroy();
        }
        binding = null;
        presenter.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.onResume();
        }
    }
}