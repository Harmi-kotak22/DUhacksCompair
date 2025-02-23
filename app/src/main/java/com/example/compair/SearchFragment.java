package com.example.compair;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.opencsv.exceptions.CsvException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchFragment extends Fragment {

    private EditText searchBar;
    private Spinner sortBySpinner, websiteFilterSpinner;
    private RecyclerView searchResultsRecyclerView;
    private ProgressBar progressBar;
    private TextView cartSummaryText;
    private Button viewCartButton;
    private TextView noResultsText;

    private SearchResultsAdapter searchResultsAdapter;
    private List<SearchResult> searchResultsList;

    // Maintain a cart list using a simple CartItem class.
    private List<CartItem> cartItems;

    // Executor for background tasks.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Simple CartItem class.
    private void navigateToCartFragment() {
        CartFragment cartFragment = new CartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("cartItems", new ArrayList<>(cartItems)); // Pass cart items
        cartFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit();
    }



    public SearchFragment() {
        // Required empty constructor.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_page, container, false);

        // Initialize views.
        searchBar = view.findViewById(R.id.searchInput);
        sortBySpinner = view.findViewById(R.id.sortBySpinner);
        websiteFilterSpinner = view.findViewById(R.id.websiteFilterSpinner);
        progressBar = view.findViewById(R.id.progressBar);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        cartSummaryText = view.findViewById(R.id.cartSummaryText);
        viewCartButton = view.findViewById(R.id.viewCartButton);
        noResultsText = view.findViewById(R.id.noResultsText);

        searchResultsList = new ArrayList<>();
        cartItems = new ArrayList<>();

        // Setup RecyclerView and adapter.
        searchResultsAdapter = new SearchResultsAdapter(searchResultsList);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Set adapter's cart action listener.
        searchResultsAdapter.setOnCartActionListener((result, newQuantity) -> {
            updateCart(result, newQuantity);
            updateCartSummary();
            Toast.makeText(getContext(), result.getName() + " quantity: " + newQuantity, Toast.LENGTH_SHORT).show();
        });

        setupSpinners();

        // Listen for search bar changes.
        searchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        viewCartButton.setOnClickListener(v -> navigateToCartFragment());

        // Initial product load.
        performSearch("");
        return view;
    }

    private void setupSpinners() {
        // Setup sort spinner (if needed)
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortAdapter);

        // Populate category spinner (websiteFilterSpinner) with "All" + unique categories.
        executorService.execute(() -> {
            List<SearchResult> allProducts = DataLoader.loadProductsFromCsv(getContext(), "amazon.csv");
            Set<String> categorySet = new HashSet<>();
            for (SearchResult product : allProducts) {
                categorySet.add(product.getCategory());
            }
            List<String> categories = new ArrayList<>();
            categories.add("All");
            categories.addAll(categorySet);
            mainHandler.post(() -> {
                // Use our custom spinner_item.xml and spinner_dropdown_item.xml.
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.spinner_item, categories);
                categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                websiteFilterSpinner.setAdapter(categoryAdapter);
            });
        });

        websiteFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                performSearch(searchBar.getText().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void updateCart(SearchResult result, int newQuantity) {
        CartItem found = null;
        for (CartItem item : cartItems) {
            if (item.productName.equals(result.getName())) {
                found = item;
                break;
            }
        }
        if (found != null) {
            if (newQuantity == 0) {
                cartItems.remove(found);
            } else {
                found.quantity = newQuantity;
            }
        } else if (newQuantity > 0) {
            cartItems.add(new CartItem(result.getName(), newQuantity));
        }
    }

    private void updateCartSummary() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.quantity;
        }
        cartSummaryText.setText("Cart: " + total + " item(s)");
    }

    private void showCartDialog() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (CartItem item : cartItems) {
            sb.append(item.productName).append(" x ").append(item.quantity).append("\n");
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Cart Details")
                .setMessage(sb.toString())
                .setPositiveButton("Close", null)
                .create().show();
    }

    private void performSearch(final String queryText) {
        progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> {
            List<SearchResult> allProducts = DataLoader.loadProductsFromCsv(getContext(), "amazon.csv");
            List<SearchResult> filteredResults = new ArrayList<>();
            String query = queryText.trim().toLowerCase();
            String selectedCategory = websiteFilterSpinner.getSelectedItem() != null ?
                    websiteFilterSpinner.getSelectedItem().toString() : "All";
            for (SearchResult product : allProducts) {
                boolean matchesQuery = query.isEmpty() || product.getName().toLowerCase().contains(query);
                boolean matchesCategory = "All".equalsIgnoreCase(selectedCategory)
                        || product.getCategory().equalsIgnoreCase(selectedCategory);
                if (matchesQuery && matchesCategory) {
                    filteredResults.add(product);
                }
            }
            mainHandler.post(() -> {
                searchResultsList.clear();
                searchResultsList.addAll(filteredResults);
                searchResultsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if (filteredResults.isEmpty()) {
                    Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
