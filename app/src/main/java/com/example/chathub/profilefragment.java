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
            Intent intent = new Intent(getContext(),SplashScreen.class);
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

    void updateButtonClick(){
        String newusername = usernameInput.getText().toString();
        if(newusername.isEmpty() || newusername.length() < 3){
            usernameInput.setError("Username should be at least 3 characters long!");
        }

        currentUserModel.setFirstName(newusername);
        setInProgress(true);
        updateToFirestore();
    }
    void updateToFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                   if(task.isSuccessful()){
                       AndroidUtil.showToast(getContext(),"Updated successfully!");
                   }else{
                       AndroidUtil.showToast(getContext(),"Update failed!");
                   }
                });
    }

    private void getUserData() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task ->{
            setInProgress(false);
            currentUserModel = task.getResult().toObject(User.class);
            usernameInput.setText(currentUserModel.getFirstName());
            userLastnameInput.setText(currentUserModel.getLastName());
            phoneInput.setText((currentUserModel.getPhoneNumber()));
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


}
