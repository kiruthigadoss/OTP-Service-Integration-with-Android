package com.example.richa.otpapp;

import android.content.Intent;
import android.icu.util.TimeUnit;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class AuthActivity extends AppCompatActivity {
    private ConstraintLayout layout;

    private EditText mPhoneText, mCodeText;

    private ProgressBar mPhoneBar, mCodeBar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private Button mSendBtn;

    private int btntype = 0;
    private TextView errortextview;

    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        layout = findViewById(R.id.constrainlayout);

        mPhoneText = (EditText) findViewById(R.id.EdittextPhoneNo);
        mCodeText = (EditText) findViewById(R.id.EdittextVerfication);

        mPhoneBar = (ProgressBar) findViewById(R.id.phoneprogressBar);
        mCodeBar = (ProgressBar) findViewById(R.id.codeprogressBar2);

        mSendBtn = (Button) findViewById(R.id.Sendbutton);

        errortextview = (TextView) findViewById(R.id.TextviewError);

        mAuth = FirebaseAuth.getInstance();
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btntype == 0) {
                    mPhoneBar.setVisibility(View.VISIBLE);
                    mPhoneText.setEnabled(false);
                    mSendBtn.setEnabled(false);

                    String PhoneNumber = mPhoneText.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            PhoneNumber,
                            60,
                            java.util.concurrent.TimeUnit.SECONDS,
                            AuthActivity.this,
                            mcallbacks);
                }
            }
        });
        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                Log.d("Verification Success", "success");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                errortextview.setText("There was some error in verification.");
                errortextview.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                btntype = 1;
                mPhoneBar.setVisibility(View.INVISIBLE);
                mSendBtn.setText("verify code");
                mSendBtn.setEnabled(true);

                // ...
            }

        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            errortextview.setText("There was some error in login.");
                            errortextview.setVisibility(View.VISIBLE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
