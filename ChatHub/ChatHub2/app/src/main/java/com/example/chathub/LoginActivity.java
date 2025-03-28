package com.example.chathub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneInput = findViewById(R.id.phoneInput);
        sendOtpBtn = findViewById(R.id.sendotpbtn);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);

        sendOtpBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Invalid Phone Number");
                return;
            }
            Intent intent = new Intent(LoginActivity.this, OtpVerify.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });



    }
}
