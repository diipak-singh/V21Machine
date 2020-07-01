package com.example.v21machine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity {
    MaterialButton submit;
    FirebaseAuth mAuth;
    private String otp;
    private String phoneNumber;
    private DatabaseReference phoneRefrence;
    private EditText editText;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    PhoneAuthCredential credential;
    boolean myValue=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        submit = findViewById(R.id.submit);
        mAuth = FirebaseAuth.getInstance();
        editText = findViewById(R.id.editText);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneRefrence = FirebaseDatabase.getInstance().getReference().child("phone");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(OtpVerification.this);
                pd.setMessage("Loading.....");
                pd.show();

                otp = editText.getText().toString();

                if (otp.isEmpty() || otp.length() < 6) {
                    editText.setError("Enter valid otp");
                } else {
                    credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
                pd.dismiss();
            }
        });



    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("phone");
                            //ref.child(phoneNumber).setValue(phoneNumber);

                            //checkForPhoneNumber(phoneNumber);
                            checkUser(phoneNumber);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    void checkUser(final String number){

        phoneRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //Toast.makeText(OtpVerification.this,ds.getValue().toString(),Toast.LENGTH_LONG).show();
                    Log.i("Number",ds.getValue().toString());
                    if (ds.getValue()==number){
                        myValue = true;
                    }else{
                        myValue=false;

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (myValue){
            Toast.makeText(OtpVerification.this,"Welcome Back......",Toast.LENGTH_LONG).show();
            startActivity(new Intent(OtpVerification.this,HomeScreen.class));
        }else {
            Toast.makeText(OtpVerification.this,"Welcome............",Toast.LENGTH_LONG).show();
            phoneRefrence.child(number).setValue(number);
            startActivity(new Intent(OtpVerification.this,Details.class));
        }
    }


    private void requestOtp(final String finalPhoneNumb) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(finalPhoneNumb, 30L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
                /*Intent intent = new Intent(PhoneNumberLogin.this,OtpVerification.class);
                intent.putExtra("MyCredentials",s);
                intent.putExtra("phoneNumber",finalPhoneNumb);
                startActivity(intent);*/
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(OtpVerification.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestOtp(phoneNumber);
    }
}