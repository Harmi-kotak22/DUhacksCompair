package com.example.compair;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView;

public class home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark theme if system is in dark mode
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.Theme_Compair_Dark);
        } else {
            setTheme(R.style.Theme_Compair_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        applyDynamicTheme(toolbar);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        applyDynamicTheme(navigationView);

        // Ensure the drawer opens from the right side
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_color));
        }

        // Initialize Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        applyDynamicTheme(bottomNavigationView);

        // Load HomeFragment by default
        loadFragment(new HomeFragment(), false);
        toolbar.setTitle("Home");

        // Bottom Navigation Handling
        bottomNavigationView.setOnItemSelectedListener(this::onBottomNavItemSelected);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    loadFragment(new HomeFragment(),true);  // Load Fragment
                } else if (id == R.id.nav_search) {
                    loadFragment(new ProfileFragment(),true);  // Load Fragment
                }   else if (id == R.id.nav_settings) {
                    // Open an Activity instead of loading a Fragment
                    Intent intent = new Intent(home.this, SettingsActivity.class);
                    startActivity(intent);
                }
                else if (id == R.id.nav_logout) {
                    // Open an Activity instead of loading a Fragment
                    Intent intent = new Intent(home.this, login.class);
                    startActivity(intent);
                }


                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    private boolean onBottomNavItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        applyScaleAnimation(itemId);

        if (itemId == R.id.nav_home) {
            loadFragment(new HomeFragment(), false);
            toolbar.setTitle("Home");
            return true;
        } else if (itemId == R.id.nav_search) {
            loadFragment(new SearchFragment(), true);
            toolbar.setTitle("Search Products");
            return true;
        } else if (itemId == R.id.nav_profile) {
            loadFragment(new ProfileFragment(), true);
            toolbar.setTitle("Profile");
            return true;
        }
        return false;
    }



    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        } else {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        transaction.commit();
    }

    private void applyScaleAnimation(int selectedItemId) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
            View itemView = bottomNavigationView.findViewById(menuItem.getItemId());

            if (itemView != null) {
                if (menuItem.getItemId() == selectedItemId) {
                    itemView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
                } else {
                    itemView.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
                }
            }
        }
    }

    private void applyDynamicTheme(View view) {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int backgroundColor = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES)
                ? ContextCompat.getColor(this, R.color.darkBackground)
                : ContextCompat.getColor(this, R.color.lightBackground);

        view.setBackgroundColor(backgroundColor);

        // Apply text color for toolbar title
        if (view instanceof Toolbar) {
            ((Toolbar) view).setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }
}