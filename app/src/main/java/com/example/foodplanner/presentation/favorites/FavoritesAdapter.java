package com.example.foodplanner.presentation.favorites;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.databinding.ItemFavoriteMealBinding;
import com.example.foodplanner.db.FavoriteMealEntity;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<FavoriteMealEntity> list = new ArrayList<>();

    public void setList(List<FavoriteMealEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteMealBinding binding = ItemFavoriteMealBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteMealEntity meal = list.get(position);

        holder.binding.tvMealName.setText(meal.getStrMeal());

        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .into(holder.binding.imgMeal);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFavoriteMealBinding binding;

        ViewHolder(ItemFavoriteMealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}