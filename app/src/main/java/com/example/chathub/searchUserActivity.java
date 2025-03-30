package com.example.chathub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class searchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchButton;
    ImageButton backbutton;
    RecyclerView recyclerView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user); // Ensure this matches the XML filename

        searchInput = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        backbutton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.searchUserRecycler);

        searchInput.requestFocus();

        backbutton.setOnClickListener((v) -> onBackPressed());

        searchButton.setOnClickListener((v) -> {
            String searchTerm = searchInput.getText().toString();
            if (searchTerm.isEmpty() || searchTerm.length() < 3) {
                searchInput.setError("Invalid Username");
                return;
            }
            setupSearchRecyclerView(searchTerm);
        });
    }

    private void setupSearchRecyclerView(String searchTerm) {
        // Implement search logic
    }
}
