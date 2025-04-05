package com.example.chathub;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView search;
    private Fragment chatFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize fragments
        chatFragment = new chatfragment();
        profileFragment = new profilefragment();

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNav);
        search = findViewById(R.id.search);

        if (search == null) {
            throw new NullPointerException("Search ImageView is null. Check activity_main.xml");
        }

        search.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, searchUserActivity.class));
        });

        // Set initial fragment state
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_frame_layout, profileFragment, "profile")
                    .hide(profileFragment)
                    .add(R.id.main_frame_layout, chatFragment, "chat")
                    .commit();
        }

        // Bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                Fragment unselectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.menu_chat) {
                    selectedFragment = getSupportFragmentManager().findFragmentByTag("chat");
                    unselectedFragment = getSupportFragmentManager().findFragmentByTag("profile");
                } else if (itemId == R.id.profile) {
                    selectedFragment = getSupportFragmentManager().findFragmentByTag("profile");
                    unselectedFragment = getSupportFragmentManager().findFragmentByTag("chat");
                }

                if (selectedFragment != null && unselectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .hide(unselectedFragment)
                            .show(selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}