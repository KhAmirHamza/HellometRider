package com.hellomet.rider.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import com.hbb20.CountryCodePicker;
import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.R;
import com.hellomet.rider.UI.SystemUI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.AUTHENTICATION;
import static com.hellomet.rider.Constants.MAIN_URL;
import static com.hellomet.rider.Constants.RIDER_ID;
import static com.hellomet.rider.Constants.RIDER_NAME;
import static com.hellomet.rider.Constants.RIDER_PASSWORD;
import static com.hellomet.rider.Constants.RIDER_PHONE_NUMBER;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    TextInputLayout textInputLayout_phone_number, textInputLayout_password;
    MaterialButton btn_sign_in, btn_goto_sign_up;
    MaterialToolbar signin_toolbar;
    ProgressBar progressbar;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressbar = findViewById(R.id.progressbar);

        signin_toolbar = findViewById(R.id.signin_toolbar);
        signin_toolbar = findViewById(R.id.signin_toolbar);
        setSupportActionBar(signin_toolbar);
        setTitle("Sign In");

        textInputLayout_phone_number = findViewById(R.id.textInputLayout_phone_number);
        textInputLayout_password = findViewById(R.id.textInputLayout_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_goto_sign_up = findViewById(R.id.btn_goto_sign_up);

        countryCodePicker = findViewById(R.id.ccp);


        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = countryCodePicker.getSelectedCountryCode() + textInputLayout_phone_number.getEditText().getText().toString();
                String password = textInputLayout_password.getEditText().getText().toString();

                if (textInputLayout_phone_number.getEditText().getText().toString().isEmpty()) {
                    textInputLayout_phone_number.setError("Required.");
                    textInputLayout_phone_number.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    textInputLayout_password.setError("Required.");
                    textInputLayout_password.requestFocus();
                    return;
                }

                //todo......
                progressbar .setVisibility(View.VISIBLE);
                Log.d(TAG, "onResponse: phone_number: "+phoneNumber);
                Log.d(TAG, "onClick: password: "+password);
                ApiClient.getInstance(MAIN_URL+"rider/").authenticateRider(phoneNumber, password).enqueue(new Callback<Rider>() {
                    @Override
                    public void onResponse(Call<Rider> call, Response<Rider> response) {

                        progressbar.setVisibility(View.GONE);
                        if (response.isSuccessful()&& response.body()!=null) {
                            String id = response.body().getId();
                            String name = response.body().getMeta_data().getName();
                            String phone_number = response.body().getMeta_data().getPhone_number();

                            if (id != null && name != null && phone_number != null) {

                                SharedPreferences sharedPreferences = getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE);
                                sharedPreferences.edit().putString(RIDER_ID, id).apply();
                                sharedPreferences.edit().putString(RIDER_NAME, name).apply();
                                sharedPreferences.edit().putString(RIDER_PHONE_NUMBER, phone_number).apply();
                                sharedPreferences.edit().putString(RIDER_PASSWORD, password).apply();
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();

                            } else {
                                Toast.makeText(SignInActivity.this, "Phone Number or Password does not matched.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d(TAG, "onResponse: Code: " + response.code());
                            Toast.makeText(SignInActivity.this, "Phone Number or Password does not matched.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Rider> call, Throwable t) {
                        progressbar.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: Error: "+t.getLocalizedMessage());
                        Toast.makeText(SignInActivity.this, "Something went wrong, Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btn_goto_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);

//        if (FirebaseAuth.getInstance().getCurrentUser() != null
//                && sharedPreferences.getString("DELIVERYMAN_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
//            startActivity(new Intent(SignInActivity.this, ProfileSetUpActivity.class)
//                    .putExtra("phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
//            finish();
//            //Toast.makeText(this, "User Sign in", Toast.LENGTH_SHORT).show();
//        } else
        if(!sharedPreferences.getString("RIDER_PHONE_NUMBER", "null").equalsIgnoreCase("null") &&
                sharedPreferences.getString("RIDER_PHONE_NUMBER", "null") != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
            //Toast.makeText(this, "User Sign in", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "user not sign in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            new SystemUI(SignInActivity.this).hideSystemUI();
        }
    }

}