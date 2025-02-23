package com.example.compair;


import static java.security.AccessController.getContext;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalPrice;
    private Button checkoutButton;


    @SuppressLint("MissingInflatedId")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        totalPrice = view.findViewById(R.id.total_price);
        checkoutButton = view.findViewById(R.id.btn_checkout);


        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, getContext(), this::removeItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cartAdapter);


        loadCartItems();


        return view;
    }


    private void loadCartItems() {
        // Fetch cart items from Firestore and update cartItemList
        // Example:
        cartItemList.add(new CartItem("Nike Air Force", "Size 4 - Red", 499, "image_url"));
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }


    private void removeItem(int position) {
        cartItemList.remove(position);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }


    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice();
        }
        totalPrice.setText("£" + total);
    }
}
