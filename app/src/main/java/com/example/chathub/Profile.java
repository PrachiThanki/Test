package com.example.chathub;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chathub.model.User;
import com.example.chathub.utils.AndroidUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private EditText etFirstName, etLastName;
    private Button btnSave;
    private ImageView ivProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Initialize Views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnSave = findViewById(R.id.btnSave);
        ivProfile = findViewById(R.id.ivProfile);

        // Profile Image Click Listener
        ivProfile.setOnClickListener(v -> openImagePicker());

        // Save Button Click Listener
        btnSave.setOnClickListener(v -> saveUserProfile());

        // Load existing profile data
        loadExistingProfile();
    }

    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(galleryIntent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    ivProfile.setImageURI(imageUri);
                    uploadProfileImage();
                }
            }
    );

    private void uploadProfileImage() {
        if (imageUri != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = storageReference.child("profile_images/" + userId + ".jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save image URL to Firestore
                            updateProfileImageUrl(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveUserProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(firstName, lastName)) {
            return;
        }

        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();
        String phoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        // Create user map for Firestore
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("phoneNumber", phoneNumber);

        // Save to Firestore
        firestore.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(documentReference -> {
                    AndroidUtil.showToast(getApplicationContext(), "Profile saved successfully!");
                    // Navigate to next screen or update UI
                    Intent intent = new Intent(Profile.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    AndroidUtil.showToast(getApplicationContext(), "Failed to save profile: " + e.getMessage());
                });
    }

    private boolean validateInputs(String firstName, String lastName) {

        if (firstName.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return false;
        }

        if (firstName.length() < 2) {
            etFirstName.setError("First name must be at least 2 characters");
            etFirstName.requestFocus();
            return false;
        }

        if (firstName.length() > 50) {
            etFirstName.setError("First name cannot exceed 50 characters");
            etFirstName.requestFocus();
            return false;
        }

        if (!firstName.matches("[a-zA-Z]+")) {
            etFirstName.setError("First name must contain only letters");
            etFirstName.requestFocus();
            return false;
        }


        if (!lastName.isEmpty()) {
            if (lastName.length() < 2) {
                etLastName.setError("Last name must be at least 2 characters");
                etLastName.requestFocus();
                return false;
            }

            if (lastName.length() > 50) {
                etLastName.setError("Last name cannot exceed 50 characters");
                etLastName.requestFocus();
                return false;
            }

            if (!lastName.matches("[a-zA-Z]+")) {
                etLastName.setError("Last name must contain only letters");
                etLastName.requestFocus();
                return false;
            }
        }

        // Check if user is authenticated
        if (mAuth.getCurrentUser() == null) {
            AndroidUtil.showToast(getApplicationContext(), "User not authenticated");
            return false;
        }

        return true;
    }

    private void loadExistingProfile() {
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate existing data
                        etFirstName.setText(documentSnapshot.getString("firstName"));
                        etLastName.setText(documentSnapshot.getString("lastName"));

                        // Load profile image if exists
                        String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            // Use a library like Glide or Picasso to load image
                            // Glide.with(this).load(profileImageUrl).into(ivProfile);
                        }
                    }
                });
    }

    private void updateProfileImageUrl(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();

        firestore.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                });
    }
}