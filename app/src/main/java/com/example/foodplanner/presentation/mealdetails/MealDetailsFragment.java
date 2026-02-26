package com.example.foodplanner.presentation.mealdetails;

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

        // Safe Args: get mealId
        if (getArguments() != null) {
            MealDetailsFragmentArgs args = MealDetailsFragmentArgs.fromBundle(getArguments());
            mealId = args.getMealId();
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

        //  Ingredients (Dynamic extraction 1–20)
        addIngredient(items, meal.getStrIngredient1());
        addIngredient(items, meal.getStrIngredient2());
        addIngredient(items, meal.getStrIngredient3());
        addIngredient(items, meal.getStrIngredient4());
        addIngredient(items, meal.getStrIngredient5());
        addIngredient(items, meal.getStrIngredient6());
        addIngredient(items, meal.getStrIngredient7());
        addIngredient(items, meal.getStrIngredient8());
        addIngredient(items, meal.getStrIngredient9());
        addIngredient(items, meal.getStrIngredient10());
        addIngredient(items, meal.getStrIngredient11());
        addIngredient(items, meal.getStrIngredient12());
        addIngredient(items, meal.getStrIngredient13());
        addIngredient(items, meal.getStrIngredient14());
        addIngredient(items, meal.getStrIngredient15());
        addIngredient(items, meal.getStrIngredient16());
        addIngredient(items, meal.getStrIngredient17());
        addIngredient(items, meal.getStrIngredient18());
        addIngredient(items, meal.getStrIngredient19());
        addIngredient(items, meal.getStrIngredient20());

        // Instructions Section
        if (!TextUtils.isEmpty(meal.getStrInstructions())) {
            items.add(new DetailItem(
                    DetailItem.TYPE_INSTRUCTIONS,
                    meal.getStrInstructions()
            ));
        }

        // YouTube Section (Embedded Player)
        if (!TextUtils.isEmpty(meal.getStrYoutube())) {
            items.add(new DetailItem(
                    DetailItem.TYPE_YOUTUBE,
                    meal.getStrYoutube()
            ));
        }

        return items;
    }

    private void addIngredient(List<DetailItem> items, String ingredient) {
        if (!TextUtils.isEmpty(ingredient) && !ingredient.equals("null")) {
            items.add(new DetailItem(DetailItem.TYPE_INGREDIENT, ingredient));
        }
    }

    // Favorite Click → UI → Presenter
    @Override
    public void onFavoriteClicked(Meal meal) {
        presenter.onFavoriteClicked(meal);
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