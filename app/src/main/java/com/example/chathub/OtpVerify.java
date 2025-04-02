package com.example.chathub; // Change to your package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpVerify extends AppCompatActivity {

    String phoneNumber;
    Long timeOut = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken ResendingToken;

    EditText otpInput;
    Button nextButton;
    ProgressBar progressBar;
    TextView resend;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private CountDownTimer resendTimer;
    private boolean isResendEnabled = true;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        otpInput = findViewById(R.id.editTextOtp);
        nextButton = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.bar);
        resend = findViewById(R.id.tvResendCode);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_LONG).show();

        // Set up the verify button click listener
        nextButton.setOnClickListener(v -> {
            String userInputOtp = otpInput.getText().toString().trim();

            if (userInputOtp.isEmpty() || userInputOtp.length() < 6) {
                AndroidUtil.showToast(getApplicationContext(), "Please enter a valid 6-digit OTP");
                return;
            }

            setInProgress(true);

            if (verificationCode != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                        verificationCode, userInputOtp);
                signIn(credential);
            } else {
                setInProgress(false);
                AndroidUtil.showToast(getApplicationContext(),
                        "Verification code not received yet. Please wait or try again.");
            }
        });

        // Set up resend code click listener
        resend.setOnClickListener(v -> {
            if (isResendEnabled) {
                sendOtp(phoneNumber, true);
                startResendTimer();
            } else {
                AndroidUtil.showToast(getApplicationContext(), "Please wait before requesting a new OTP");
            }
        });

        // Send OTP initially
        sendOtp(phoneNumber, false);
    }

    void sendOtp(String phoneNumber, Boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeOut, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                String errorMsg = "OTP verification failed: " + e.getMessage();
                                AndroidUtil.showToast(getApplicationContext(), errorMsg);
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                ResendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully!");
                                setInProgress(false);
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void setInProgress(Boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        // Verification successful one can proceed to next activity
                        AndroidUtil.showToast(getApplicationContext(), "Verification successful!");

                        Intent intent = new Intent(OtpVerify.this, Profile.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If verification fails, display a message and log the error
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        AndroidUtil.showToast(getApplicationContext(), errorMessage);
                    }
                });
    }

    private void startResendTimer() {
        // Cancel any existing timer
        if (resendTimer != null) {
            resendTimer.cancel();
        }

        // Disable resend button
        isResendEnabled = false;
        resend.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Start a 60-second countdown timer
        resendTimer = new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the resend text to show countdown
                resend.setText("Resend OTP (" + (millisUntilFinished / 1000) + "s)");
            }

            @Override
            public void onFinish() {
                // resend button
                isResendEnabled = true;
                resend.setText("Resend OTP");
                resend.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}