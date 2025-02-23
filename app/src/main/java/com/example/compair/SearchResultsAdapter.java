package com.example.compair;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<SearchResult> results;
    private OnCartActionListener cartActionListener;
    // Adapter-level quantity map (using product name as key)
    private Map<String, Integer> quantityMap = new HashMap<>();
    private List<SearchResult> searchResults;

    // Callback interface for cart actions.
    public interface OnCartActionListener {
        void onQuantityChanged(SearchResult result, int newQuantity);
    }

    public void setOnCartActionListener(OnCartActionListener listener) {
        this.cartActionListener = listener;
    }

    public SearchResultsAdapter(List<SearchResult> results) {
        this.results = results;
    }

    public void updateResults(List<SearchResult> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult result = results.get(position);
        Context context = holder.itemView.getContext();
        holder.resultText.setText(result.getName());
        holder.priceText.setText("₹" + result.getPrice());
        holder.ratingText.setText("⭐ " + result.getRating() + " (" + result.getReviews() + " reviews)");

        Glide.with(context)
                .load(result.getImageUrl())
                .placeholder(R.drawable.compair)
                .error(R.drawable.logout)
                .into(holder.productImage);
        holder.itemView.setOnClickListener(v -> {
            String productId = result.getId(); // Get the product ID
            Toast.makeText(v.getContext(), "Product ID: " + productId, Toast.LENGTH_SHORT).show();
        });
        // Clicking on the item opens the product link.
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getUrl()));
            context.startActivity(intent);
        });

        // Check quantity for this product
        int quantity = quantityMap.getOrDefault(result.getName(), 0);
        if (quantity == 0) {
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.quantityControlLayout.setVisibility(View.GONE);
        } else {
            holder.addToCartButton.setVisibility(View.GONE);
            holder.quantityControlLayout.setVisibility(View.VISIBLE);
            holder.quantityText.setText(String.valueOf(quantity));
        }

        // "Add to Cart" button: when tapped, set quantity to 1.
        holder.addToCartButton.setOnClickListener(v -> {
            quantityMap.put(result.getName(), 1);
            if (cartActionListener != null) {
                cartActionListener.onQuantityChanged(result, 1);
            }
            notifyItemChanged(position);
        });

        // Minus button: decrease quantity. If quantity becomes 0, remove the product.
        holder.minusButton.setOnClickListener(v -> {
            int currentQty = quantityMap.getOrDefault(result.getName(), 0);
            if (currentQty > 1) {
                quantityMap.put(result.getName(), currentQty - 1);
                if (cartActionListener != null) {
                    cartActionListener.onQuantityChanged(result, currentQty - 1);
                }
            } else {
                quantityMap.remove(result.getName());
                if (cartActionListener != null) {
                    cartActionListener.onQuantityChanged(result, 0);
                }
            }
            notifyItemChanged(position);
        });

        // Plus button: increase quantity.
        holder.plusButton.setOnClickListener(v -> {
            int currentQty = quantityMap.getOrDefault(result.getName(), 0);
            quantityMap.put(result.getName(), currentQty + 1);
            if (cartActionListener != null) {
                cartActionListener.onQuantityChanged(result, currentQty + 1);
            }
            notifyItemChanged(position);
        });
    }
    public void updateData(List<SearchResult> newSearchResults) {
        this.searchResults = newSearchResults;  // Update the list
        notifyDataSetChanged();  // Refresh the RecyclerView
    }
    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resultText, priceText, ratingText;
        ImageView productImage;
        Button addToCartButton;
        LinearLayout quantityControlLayout;
        Button minusButton, plusButton;
        TextView quantityText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            resultText = itemView.findViewById(R.id.resultText);
            priceText = itemView.findViewById(R.id.priceText);
            ratingText = itemView.findViewById(R.id.ratingText);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            quantityControlLayout = itemView.findViewById(R.id.quantityControlLayout);
            minusButton = itemView.findViewById(R.id.minusButton);
            plusButton = itemView.findViewById(R.id.plusButton);
            quantityText = itemView.findViewById(R.id.quantityText);
        }
    }
}
