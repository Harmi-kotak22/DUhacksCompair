package com.example.compair;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class home extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private DashboardAdapter adapter;
    private List<Product> itemList;
    private List<Product> filteredList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Set up Search View
        searchView = findViewById(R.id.searchView);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 Columns

        // Sample Data
        itemList = new ArrayList<>();
        itemList.add(new Product("Grocery", R.drawable.grocery));
        itemList.add(new Product("Electronic", R.drawable.electronics));
        itemList.add(new Product("Toys", R.drawable.toys));
        itemList.add(new Product("Fashion", R.drawable.fashion));
        itemList.add(new Product("Shoes", R.drawable.shoe   ));

        filteredList = new ArrayList<>(itemList);
        adapter = new DashboardAdapter(filteredList);
        recyclerView.setAdapter(adapter);

        // Search filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
       /* bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;

                    case R.id.nav_cart:
                        startActivity(new Intent(home.this, CartActivity.class));
                        return true;

                    case R.id.nav_profile:
                        startActivity(new Intent(home.this, ProfileActivity.class));
                        return true;
                }
                return false;
            }
        });*/
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(itemList);
        } else {
            for (Product item : itemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
