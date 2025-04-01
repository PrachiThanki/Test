package com.example.chathub;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class searchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageView backButton;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user); // This should match your XML filename

        // Initialize UI elements
        searchInput = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.searchUserRecycler);

        // Debugging toast to check if the activity opens
        Toast.makeText(this, "Search Page Opened", Toast.LENGTH_LONG).show();

        // Back button functionality
        backButton.setOnClickListener(v -> onBackPressed());

        // Search button functionality
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString().trim();

            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }

            setupSearchRecyclerView(searchTerm);
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        // Implement search logic here (e.g., fetch data and update RecyclerView)
    }
}