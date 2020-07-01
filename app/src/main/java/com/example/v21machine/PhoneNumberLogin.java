package com.example.v21machine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneNumberLogin extends AppCompatActivity {

    Button rqeuestButton;
    EditText phoneNumber;
    CountryCodePicker countryCodePicker;
    FirebaseAuth mAuth;
    private String finalPhoneNumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_login);

        rqeuestButton = findViewById(R.id.request);
        phoneNumber = findViewById(R.id.phoneNumber);
        countryCodePicker = findViewById(R.id.ccp);
        mAuth = FirebaseAuth.getInstance();

        rqeuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(PhoneNumberLogin.this,OtpVerification.class));

                String phoneNumb = phoneNumber.getText().toString();
                if (!phoneNumb.isEmpty() && phoneNumb.length() == 10){
                    finalPhoneNumb = "+"+countryCodePicker.getSelectedCountryCode()+phoneNumb;
                    //Toast.makeText(PhoneNumberLogin.this,"GGGGGGGGoooddddddddd",Toast.LENGTH_LONG).show();
                    //requestOtp(finalPhoneNumb);
                    Intent intent = new Intent(PhoneNumberLogin.this,OtpVerification.class);
                    intent.putExtra("phoneNumber",finalPhoneNumb);
                    startActivity(intent);
                }else {
                    phoneNumber.setError("Enter Valid Phone Number");
                }
            }
        });
    }


}