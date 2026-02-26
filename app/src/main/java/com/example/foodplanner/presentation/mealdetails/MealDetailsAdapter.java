package com.example.foodplanner.presentation.mealdetails;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodplanner.R;
import com.example.foodplanner.data.model.Meal;
import com.example.foodplanner.databinding.ItemDetailHeaderBinding;
import com.example.foodplanner.databinding.ItemDetailIngredientBinding;
import com.example.foodplanner.databinding.ItemDetailInstructionBinding;
import com.example.foodplanner.databinding.ItemDetailYoutubeBinding;

import java.util.ArrayList;
import java.util.List;

public class MealDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MealDetailsAdapter";
    private List<DetailItem> items = new ArrayList<>();
    private final OnFavoriteClickListener favoriteClickListener;
    private final List<WebView> activeWebViews = new ArrayList<>();

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
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            holder.binding.cardVideo.setVisibility(View.GONE);
            return;
        }

        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null || videoId.isEmpty()) {
            holder.binding.cardVideo.setVisibility(View.GONE);
            return;
        }

        holder.binding.cardVideo.setVisibility(View.VISIBLE);
        WebView webView = holder.binding.youtubeWebView;

        // Track WebView for lifecycle management
        if (!activeWebViews.contains(webView)) {
            activeWebViews.add(webView);
        }

        setupWebView(webView);
        loadYoutubeVideo(webView, videoId);
    }

    private void setupWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webSettings.setUserAgentString(
                "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 " +
                        "Chrome/120.0.0.0 Mobile Safari/537.36"
        );

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(
                    WebView view,
                    WebResourceRequest request,
                    WebResourceError error
            ) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView error: " + error);
            }
        });
    }

    private void loadYoutubeVideo(WebView webView, String videoId) {
        String html =
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "html, body { margin: 0; padding: 0; background: black; width: 100%; height: 100%; }" +
                        "iframe { width: 100%; height: 100%; border: 0; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<iframe src='https://www.youtube-nocookie.com/embed/" + videoId +
                        "?playsinline=1&rel=0' " +
                        "allow='accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share' " +
                        "allowfullscreen></iframe>" +
                        "</body>" +
                        "</html>";

        webView.loadDataWithBaseURL(
                "https://www.youtube-nocookie.com",
                html,
                "text/html",
                "UTF-8",
                null
        );
    }

    private String extractVideoId(String url) {
        try {
            Uri uri = Uri.parse(url);

            // Handle youtube.com/watch?v=VIDEO_ID format
            if (uri.getQueryParameter("v") != null) {
                return uri.getQueryParameter("v");
            }

            // Handle youtu.be/VIDEO_ID format
            if (url.contains("youtu.be/")) {
                return uri.getLastPathSegment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Video ID extraction failed", e);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Lifecycle management methods
    public void onPause() {
        for (WebView webView : activeWebViews) {
            if (webView != null) {
                webView.onPause();
                webView.pauseTimers();
            }
        }
    }

    public void onResume() {
        for (WebView webView : activeWebViews) {
            if (webView != null) {
                webView.onResume();
                webView.resumeTimers();
            }
        }
    }

    public void onDestroy() {
        for (WebView webView : activeWebViews) {
            if (webView != null) {
                webView.destroy();
            }
        }
        activeWebViews.clear();
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