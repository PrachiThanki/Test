package com.example.chathub;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chathub.utils.AndroidUtil;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerify extends AppCompatActivity {
    private String phone;
    private static final Long TIMEOUT_SECONDS = 60L;
    private String verificationCode;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    private Button verifyBtn;
    private TextView resendOtp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private EditText otp1, otp2, otp3, otp4,otp5,otp6;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        mAuth = FirebaseAuth.getInstance();

        // Initializing UI components
        otp1 = findViewById(R.id.editTextNumber1);
        otp2 = findViewById(R.id.editTextNumber2);
        otp3 = findViewById(R.id.editTextNumber3);
        otp4 = findViewById(R.id.editTextNumber4);
        otp5 = findViewById(R.id.editTextNumber5);
        otp6 = findViewById(R.id.editTextNumber6);
        progressBar = findViewById(R.id.bar);
        verifyBtn = findViewById(R.id.btnVerify);
        resendOtp = findViewById(R.id.tvResendCode);

        if (getIntent() != null && getIntent().hasExtra("phone")) {
            phone = getIntent().getStringExtra("phone");
            Toast.makeText(this, "Phone: " + phone, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Phone number not received", Toast.LENGTH_LONG).show();
        }

        // OTP Verification Button Click Listener
        verifyBtn.setOnClickListener(v -> verifyOtp());

        // Resend OTP Click Listener
        resendOtp.setOnClickListener(v -> sendOtp(phone, true));

        // Send OTP initially
        sendOtp(phone, false);
    }

    private void sendOtp(String phone, boolean isResend) {
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        signIn(credential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(), ""+ e);
                        setInProgress(false);
                    }


                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationCode = verificationId;
                        resendingToken = token;
                        AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });

        if (isResend && resendingToken != null) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void verifyOtp() {
        String otp = otp1.getText().toString() + otp2.getText().toString() +
                otp3.getText().toString() + otp4.getText().toString() +
                otp5.getText().toString() + otp6.getText().toString();


        if (otp.length() != 6) {
            AndroidUtil.showToast(this, "Enter the complete OTP");
            return;
        }

        if (verificationCode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
            signIn(credential);
        } else {
            AndroidUtil.showToast(this, "Verification code is missing");
        }
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            verifyBtn.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(PhoneAuthCredential credential) {
        setInProgress(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(this, "Verification Successful");
                        // Navigate to next screen here
                    } else {
                        AndroidUtil.showToast(this, "Verification Failed");
                    }
                });
    }
}
