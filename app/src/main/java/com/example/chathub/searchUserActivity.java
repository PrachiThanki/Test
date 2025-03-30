package com.example.chathub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class searchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backbutton;
    RecyclerView recyclerView;

    @SuppressLint({"WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        backbutton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.searchUserRecycler);

        searchInput.requestFocus();

        backbutton.setOnClickListener((v) -> {
            onBackPressed();
        });

        searchButton.setOnClickListener((v) -> {
            String searchTerm = searchInput.getText().toString().trim();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            Toast.makeText(this, "Searching for: " + searchTerm, Toast.LENGTH_SHORT).show();
            setupSearchRecyclerView(searchTerm);
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        // Implement your RecyclerView search logic here
        Toast.makeText(this, "Search feature not yet implemented", Toast.LENGTH_SHORT).show();
    }
}
