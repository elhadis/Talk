package com.rgbat.talk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText phoneText,codeText;
    private Button continueAndNexBtn,button;
    private String checker =  "" ,phoneNumber =  "";
    private RelativeLayout relativeLayout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResentToken;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndNexBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);
        mAuth =FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
      //  button = findViewById(R.id.Button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getBaseContext(), ContectsActivity.class);
//                startActivity(intent);
//            }
//        });


        continueAndNexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continueAndNexBtn.getText().equals("Submit")|| checker.equals("Code Sent")){

                    String verificationCod = codeText.getText().toString();
                    if (verificationCod.equals("")){
                        Toast.makeText(RegistrationActivity.this, "please Writ verification code First", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        loadingBar.setTitle("Code Verification");
                        loadingBar.setMessage("please Wait cod number");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCod);
                        signInWithPhoneAuthCredential(credential);
                    }


                }
                else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals("")){
                        loadingBar.setTitle("Phone Number Verification");
                        loadingBar.setMessage("please Wait");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,
                                60,
                                TimeUnit.SECONDS,RegistrationActivity.
                                this,
                                mCallbacks
                        );

                    }
                    else {
                        Toast.makeText(RegistrationActivity.this, "Please Writ Valid Phone Number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegistrationActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                continueAndNexBtn.setText("Continue");
                codeText.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mResentToken = forceResendingToken;


                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueAndNexBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
                Toast.makeText(RegistrationActivity.this, "Code has ben Sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent intent = new Intent(getBaseContext(), ContectsActivity.class);
            startActivity(intent);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           loadingBar.dismiss();
                           sendUserToMainActivity();


                            // ...
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Try Again", Toast.LENGTH_SHORT).show();

                            }
                        }

                });
    }
    private void  sendUserToMainActivity(){
        Intent intent = new Intent(getBaseContext(), ContectsActivity.class);
        startActivity(intent);
    }
}
