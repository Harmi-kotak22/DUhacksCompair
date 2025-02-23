package com.example.compair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.compair.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize category list (Buttons)
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Mobile", R.drawable.phone));
        categoryList.add(new Category("Laptop/Computer", R.drawable.laptop));
        categoryList.add(new Category("TV", R.drawable.tv));
        categoryList.add(new Category("Camera", R.drawable.camera));
        categoryList.add(new Category("Watch", R.drawable.watch));
        categoryList.add(new Category("AC", R.drawable.ac));
        categoryList.add(new Category("Fridge", R.drawable.fridge));
        categoryList.add(new Category("Iron", R.drawable.iron));
        categoryList.add(new Category("Fans", R.drawable.fan));
        categoryList.add(new Category("Washing Machine", R.drawable.wm));
        categoryList.add(new Category("Electronic Accessories", R.drawable.es));

        categoryAdapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(categoryAdapter);

        return view;
    }

    // Handle category click
    @Override
    public void onCategoryClick(String categoryName) {
        Intent intent = new Intent(getContext(), SearchFragment.class);
        intent.putExtra("CATEGORY_NAME", categoryName);
        startActivity(intent);
    }
}