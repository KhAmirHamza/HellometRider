package com.hellomet.rider.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import com.hbb20.CountryCodePicker;
import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.MAIN_URL;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    TextInputLayout text_input_layout_phone_number;
    MaterialButton btn_sign_up,btn_goto_sign_in;
    ProgressBar progressbar;
    CountryCodePicker countryCodePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressbar = findViewById(R.id.progressbar);
        text_input_layout_phone_number = findViewById(R.id.text_input_layout_phone_number);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_goto_sign_in = findViewById(R.id.btn_goto_sign_in);
        countryCodePicker = findViewById(R.id.ccp);


        btn_sign_up.setOnClickListener(this);
        btn_goto_sign_in.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId()==text_input_layout_phone_number.getId()){
            text_input_layout_phone_number.getEditText().setCursorVisible(true);
        }else if (v.getId()==btn_sign_up.getId()){

            String phoneNumber = countryCodePicker.getSelectedCountryCode()+text_input_layout_phone_number.getEditText().getText().toString();

            if (!text_input_layout_phone_number.getEditText().getText().toString().isEmpty()){
                progressbar.setVisibility(View.VISIBLE);
                ApiClient.getInstance(MAIN_URL).checkRiderIsAvailableOrNot(phoneNumber).enqueue(new Callback<Rider>() {
                    @Override
                    public void onResponse(Call<Rider> call, Response<Rider> response) {
                        progressbar.setVisibility(View.GONE);
                        if (response.isSuccessful()){
                            if (response.body()!=null){
                                Toast.makeText(SignUpActivity.this, "An Account is already created with this phone number,Try another.", Toast.LENGTH_SHORT).show();
                            }else {
                                startActivity(new Intent(SignUpActivity.this,VerificationActivity.class)
                                        .putExtra("phoneNumber",phoneNumber));
                            }
                        }else {
                            Log.d(TAG, "onResponse: ResponseError:"+response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Rider> call, Throwable t) {
                        progressbar.setVisibility(View.GONE);
                        Log.d(TAG, "onFailure: Error: "+t.getLocalizedMessage());
                    }
                });

            }
            else {
                Toast.makeText(this, "Please insert valid phone number", Toast.LENGTH_SHORT).show();
            }

        } else if (v.getId() == R.id.btn_goto_sign_in) {
            startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            // finish();
        }
    }


}