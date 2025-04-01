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
    ImageView search;
    private Fragment chatFragment;
    private Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Fragments
        chatFragment = new chatfragment();
        profileFragment = new profilefragment();

        // Initialize UI elements
        bottomNavigationView = findViewById(R.id.bottomNav);
        search = findViewById(R.id.search);

        // Search button functionality
        search.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, searchUserActivity.class);
            startActivity(intent);
        });

        // Set initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, chatFragment)
                    .commit();
        }

        // Bottom Navigation Item Selection
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();
                if (itemId == R.id.menu_chat) {
                    selectedFragment = chatFragment;
                } else if (itemId == R.id.profile) {
                    selectedFragment = profileFragment;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame_layout, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}
