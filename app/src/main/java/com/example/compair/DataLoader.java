package com.example.compair;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private static final String TAG = "DataLoader";

    /**
     * Loads and parses the CSV file (amazon.csv) from the assets folder.
     * Expects a CSV with 15 fields:
     * 0: product_id
     * 1: product_name
     * 2: category
     * 3: discounted_price
     * 4: actual_price
     * 5: discount_percentage
     * 6: rating
     * 7: rating_count
     * 8: about_product
     * 9: user_id
     * 10: user_name
     * 11: review_id
     * 12: review_title
     * 13: review_content
     * 14: img_link
     * 15: product_link
     *
     * @param context     the context
     * @param csvFileName the CSV file name (e.g., "amazon.csv")
     * @return a list of SearchResult objects
     */
    public static List<SearchResult> loadProductsFromCsv(Context context, String csvFileName) {
        List<SearchResult> products = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(csvFileName);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr);
             CSVReader csvReader = new CSVReader(br)) {

            // Read and skip header row
            String[] header = csvReader.readNext();
            if (header == null) {
                Log.w(TAG, "CSV file is empty or header is missing.");
                return products;
            }
            int rowIndex = 1;
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                if (row.length < 16) {
                    Log.w(TAG, "Skipping row " + rowIndex + " due to insufficient columns.");
                    rowIndex++;
                    continue;
                }
                // Skip row if product name (index 1) is empty
                if (row[1] == null || row[1].trim().isEmpty()) {
                    Log.e(TAG, "Skipping row " + rowIndex + " because product name is empty.");
                    rowIndex++;
                    continue;
                }
                SearchResult product = parseCsvRow(row);
                if (product != null) {
                    products.add(product);
                }
                rowIndex++;
            }
        } catch (IOException | CsvException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }
        Log.d(TAG, "Total products loaded: " + products.size());
        return products;
    }

    /**
     * Converts a CSV row into a SearchResult object.
     */
    private static SearchResult parseCsvRow(String[] tokens) {
        SearchResult result = new SearchResult();
        try {
            // product_name from index 1
            result.setName(tokens[1].trim());
            // category from index 2
            result.setCategory(tokens[2].trim());
            // Attempt to parse discounted_price from index 3; if invalid, fallback to actual_price (index 4)
            String discountPriceStr = tokens[3].trim().replaceAll("[^\\d.]+", "");
            try {
                result.setPrice((int) Math.round(Double.parseDouble(discountPriceStr)));
            } catch (NumberFormatException e) {
                String actualPriceFallback = tokens[4].trim().replaceAll("[^\\d.]+", "");
                result.setPrice((int) Math.round(Double.parseDouble(actualPriceFallback)));
            }
            // Parse actual_price from index 4
            String actualPriceStr = tokens[4].trim().replaceAll("[^\\d.]+", "");
            result.setActualPrice((int) Math.round(Double.parseDouble(actualPriceStr)));
            // Parse rating from index 6
            String ratingStr = tokens[6].trim();
            result.setRating(Double.parseDouble(ratingStr));
            // Parse rating_count from index 7 (used as reviews)
            try {
                result.setReviews(Integer.parseInt(tokens[7].trim()));
            } catch (NumberFormatException e) {
                result.setReviews(0);
            }
            // Image URL from index 14
            String imgUrl = tokens[14].trim().replaceAll("^\"|\"$", "");
            result.setImageUrl(imgUrl);
            // Product link from index 15
            result.setUrl(tokens[15].trim());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing CSV row: " + e.getMessage());
            return null;
        }
        return result;
    }

    public void fetchProductsFromFirebase(SearchResultsAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<SearchResult> searchResults = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Fetching the ID from Firebase
                            String name = document.getString("name");
                            String category = document.getString("category");
                            double price = document.getDouble("price");
                            String imageUrl = document.getString("imageUrl");

                            searchResults.add(new SearchResult(id, name, category, price, imageUrl));
                        }
                        // Update the RecyclerView adapter with new data
                        adapter.updateData(searchResults);
                    }
                });
    }
}
