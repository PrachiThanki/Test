package com.example.chathub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chathub.model.User;
import com.example.chathub.utils.AndroidUtil;
import com.example.chathub.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class profilefragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput;
    EditText userLastnameInput;
    EditText phoneInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutButton;
    User currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUrl;

    public profilefragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null && data.getData() != null){
                            selectedImageUrl = data.getData();
                            AndroidUtil.setProffile(getContext(),selectedImageUrl,profilePic);
                        }
                    }
                }
        );
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilefragment, container, false);
        profilePic = view.findViewById(R.id.ivProfile);
        usernameInput = view.findViewById(R.id.firstNameInput);
        userLastnameInput = view.findViewById(R.id.lastnameinput);
        phoneInput = view.findViewById(R.id.PhoneInput);
        updateProfileBtn = view.findViewById(R.id.UpdateProfile);
        progressBar = view.findViewById(R.id.progress);
        logoutButton = view.findViewById(R.id.logout);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateButtonClick();
        }));

        logoutButton.setOnClickListener((v ->{
            FirebaseUtil.logout();
            Intent intent = new Intent(getContext(), SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }));

        profilePic.setOnClickListener((v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        }));
        return view;
    }

    void updateButtonClick() {
        String newUsername = usernameInput.getText().toString().trim();
        String newLastName = userLastnameInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();

        // Validate inputs
        if (!validateInputs(newUsername, newLastName, newPhone)) {
            return;
        }

        // Update user model
        currentUserModel.setFirstName(newUsername);
        currentUserModel.setLastName(newLastName);
        currentUserModel.setPhoneNumber(newPhone);

        setInProgress(true);
        if(selectedImageUrl != null) {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUrl)
                    .addOnCompleteListener(task -> {
                        updateToFirestore();
                    });
        }else {
            updateToFirestore();
        }


    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully!");
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed!");
                    }
                });
    }

    private void getUserData() {
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProffile(getContext(), uri, profilePic);
                    }
                });
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(User.class);
            usernameInput.setText(currentUserModel.getFirstName());
            userLastnameInput.setText(currentUserModel.getLastName());
            phoneInput.setText(currentUserModel.getPhoneNumber());
        });
    }

    void setInProgress(Boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInputs(String username, String lastName, String phone) {
        // Username (First Name) validation
        if (username.isEmpty()) {
            usernameInput.setError("First name is required");
            usernameInput.requestFocus();
            return false;
        }

        if (username.length() < 2) {
            usernameInput.setError("First name must be at least 2 characters");
            usernameInput.requestFocus();
            return false;
        }

        if (username.length() > 50) {
            usernameInput.setError("First name cannot exceed 50 characters");
            usernameInput.requestFocus();
            return false;
        }

        if (!username.matches("[a-zA-Z]+")) {
            usernameInput.setError("First name must contain only letters");
            usernameInput.requestFocus();
            return false;
        }

        // Last Name validation (optional field, but if provided, validate it)
        if (!lastName.isEmpty()) {
            if (lastName.length() < 2) {
                userLastnameInput.setError("Last name must be at least 2 characters");
                userLastnameInput.requestFocus();
                return false;
            }

            if (lastName.length() > 50) {
                userLastnameInput.setError("Last name cannot exceed 50 characters");
                userLastnameInput.requestFocus();
                return false;
            }

            if (!lastName.matches("[a-zA-Z]+")) {
                userLastnameInput.setError("Last name must contain only letters");
                userLastnameInput.requestFocus();
                return false;
            }
        }

        // Phone number validation
        if (phone.isEmpty()) {
            phoneInput.setError("Phone number is required");
            phoneInput.requestFocus();
            return false;
        }

        // Updated regex: Requires optional + followed by 1-3 digit country code and 9-12 digit number
        if (!phone.matches("^\\+?[1-9]\\d{1,14}$")) {
            phoneInput.setError("Enter a valid phone number (e.g., +12345678901)");
            phoneInput.requestFocus();
            return false;
        }

        // Check if user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            AndroidUtil.showToast(getContext(), "User not authenticated");
            return false;
        }

        return true;
    }
}