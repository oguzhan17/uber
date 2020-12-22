package com.ao.uber;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    Button buttonResend, buttonSignIn;
    int resendCounter;
    ProgressBar progressBar;
    TextInputEditText editText1, editText2, editText3, editText4, editText5, editText6;
    private String verificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            mResendToken = forceResendingToken;
            Toast.makeText(VerificationActivity.this, "Verification code sent!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            String code = phoneAuthCredential.getSmsCode();

            boolean phoneNumberExist = false;

            if (code != null) {
                //verifying the code like in normal flow
                // String editText = editText1.getText().toString()  + editText2.getText().toString()  + editText3.getText().toString() + editText4.getText().toString() + editText5.getText().toString() + editText6.getText().toString();
                //editText.setText(code);
                verifyCode(code);
                Toast.makeText(VerificationActivity.this, "Verification success!", Toast.LENGTH_LONG).show();


                if (phoneNumberExist) {
                    // Dashboard
                } else {
                    // Registration
                }

            } else {
                // you dont get any code, it is instant verification
                Toast.makeText(VerificationActivity.this, "Doing Instant Verification without sending code!", Toast.LENGTH_LONG).show();
                signInWithCredential(phoneAuthCredential);

                if (phoneNumberExist) {
                    // Dashboard
                } else {
                    // Registration
                }
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        editText1 = findViewById(R.id.editTextCode1);
        editText2 = findViewById(R.id.editTextCode2);
        editText3 = findViewById(R.id.editTextCode3);
        editText4 = findViewById(R.id.editTextCode4);
        editText5 = findViewById(R.id.editTextCode5);
        editText6 = findViewById(R.id.editTextCode6);
        buttonSignIn = findViewById(R.id.buttonAddCard);
        buttonResend = findViewById(R.id.buttonResend);
        TextView pleaseType = findViewById(R.id.pleaseType);

        final String phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);

        pleaseType.setText(pleaseType.getText().toString() + phoneNumber);


        // save phone number
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();




        buttonSignIn.setOnClickListener(v -> {

            String code = editText1.getText().toString() +
                    editText2.getText().toString() +
                    editText3.getText().toString() +
                    editText4.getText().toString() +
                    editText5.getText().toString() +
                    editText6.getText().toString();

            if (code.isEmpty() || code.length() < 6) {

                editText1.setError("Enter code...");
                editText1.requestFocus();
                return;
            }
            verifyCode(code);
        });

        setupOTPInputs();

        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resendVerificationCode(phoneNumber, mResendToken);

            }
        });

    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(VerificationActivity.this, MapsFirstActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);

                        } else {
                            Toast.makeText(VerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(20L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        progressBar.setVisibility(View.GONE);

        startCountDownTimer();
    }

    private void startCountDownTimer() {

        buttonResend.setEnabled(false);

        //reset counter value on each call
        resendCounter = 60;

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                buttonResend.setText("Resend OTP in " + resendCounter + " Sec");
                resendCounter--;
            }

            public void onFinish() {
                buttonResend.setText("Resend OTP");
                buttonResend.setEnabled(true);
            }
        }.start();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(20L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);        // OnVerificationStateChangedCallbacks
        // ForceResendingToken from callbacks

        startCountDownTimer();

    }

    private void setupOTPInputs() {
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    editText6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}