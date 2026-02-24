package com.example.foodplanner.presentation.mealdetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.ItemDetailHeaderBinding;
import com.example.foodplanner.databinding.ItemDetailIngredientBinding;
import com.example.foodplanner.databinding.ItemDetailInstructionBinding;
import com.example.foodplanner.databinding.ItemDetailYoutubeBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DetailItem> items = new ArrayList<>();
    private final OnFavoriteClickListener favoriteClickListener;

    public MealDetailsAdapter(OnFavoriteClickListener favoriteClickListener) {
        this.favoriteClickListener = favoriteClickListener;
    }

    public void setItems(List<DetailItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == DetailItem.TYPE_HEADER) {
            ItemDetailHeaderBinding binding =
                    ItemDetailHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(binding);
        } else if (viewType == DetailItem.TYPE_INGREDIENT) {
            ItemDetailIngredientBinding binding =
                    ItemDetailIngredientBinding.inflate(inflater, parent, false);
            return new IngredientViewHolder(binding);
        } else if (viewType == DetailItem.TYPE_INSTRUCTIONS) {
            ItemDetailInstructionBinding binding =
                    ItemDetailInstructionBinding.inflate(inflater, parent, false);
            return new InstructionsViewHolder(binding);
        } else {
            ItemDetailYoutubeBinding binding =
                    ItemDetailYoutubeBinding.inflate(inflater, parent, false);
            return new YoutubeViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DetailItem item = items.get(position);

        switch (item.getType()) {

            case DetailItem.TYPE_HEADER:
                bindHeader((HeaderViewHolder) holder, (Meal) item.getData());
                break;

            case DetailItem.TYPE_INGREDIENT:
                bindIngredient((IngredientViewHolder) holder, (String) item.getData());
                break;

            case DetailItem.TYPE_INSTRUCTIONS:
                bindInstructions((InstructionsViewHolder) holder, (String) item.getData());
                break;

            case DetailItem.TYPE_YOUTUBE:
                bindYoutube((YoutubeViewHolder) holder, (String) item.getData());
                break;
        }
    }

    private void bindHeader(HeaderViewHolder holder, Meal meal) {
        // Title
        holder.binding.tvMealTitle.setText(meal.getStrMeal());

        // Image
        Glide.with(holder.itemView.getContext())
                .load(meal.getStrMealThumb())
                .into(holder.binding.imgMealHeader);

        // Favorite Icon State (UI only, Presenter controls actual logic)
        if (meal.isFavorite()) {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite_filled);
            holder.binding.imgFavorite.setColorFilter(
                    holder.itemView.getContext().getColor(android.R.color.holo_red_light)
            );
        } else {
            holder.binding.imgFavorite.setImageResource(R.drawable.ic_favorite_border);
            holder.binding.imgFavorite.setColorFilter(
                    holder.itemView.getContext().getColor(android.R.color.white)
            );
        }

        // Click delegated OUTSIDE adapter (Clean MVP)
        holder.binding.imgFavorite.setOnClickListener(v -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClicked(meal);
            }
        });
    }

    private void bindIngredient(IngredientViewHolder holder, String ingredient) {
        holder.binding.tvIngredient.setText("• " + ingredient);
    }

    private void bindInstructions(InstructionsViewHolder holder, String instructions) {
        holder.binding.tvInstructions.setText(instructions);
    }

    private void bindYoutube(YoutubeViewHolder holder, String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) return;

        String videoId = extractYoutubeId(youtubeUrl);
        if (videoId == null) return;

        YouTubePlayerView playerView = holder.binding.youtubePlayerView;

        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }

    private String extractYoutubeId(String url) {
        if (url.contains("v=")) {
            return url.substring(url.indexOf("v=") + 2);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemDetailHeaderBinding binding;
        HeaderViewHolder(ItemDetailHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ItemDetailIngredientBinding binding;
        IngredientViewHolder(ItemDetailIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class InstructionsViewHolder extends RecyclerView.ViewHolder {
        ItemDetailInstructionBinding binding;
        InstructionsViewHolder(ItemDetailInstructionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class YoutubeViewHolder extends RecyclerView.ViewHolder {
        ItemDetailYoutubeBinding binding;
        YoutubeViewHolder(ItemDetailYoutubeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}