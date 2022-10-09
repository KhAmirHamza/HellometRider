package com.hellomet.rider.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;


import com.hellomet.rider.ApiRequests;
import com.hellomet.rider.Model.Auth;
import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.R;
import com.hellomet.rider.UI.SystemUI;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hellomet.rider.Const.PICK_IMAGE;
import static com.hellomet.rider.Constants.MAIN_URL;

public class ProfileSetUpActivity extends AppCompatActivity {
    private static final String TAG = "ProfileSetUpActivity";

    TextInputLayout text_input_layout_name,text_input_layout_date_of_birth,text_input_layout_phone_number,
            text_input_layout_password, text_input_layout_confirm_pass;
    String name, date_of_birth, phone_number, password;
    Button btn_choose_image,btn_submit_profile;
    ImageView imgv_pharmacy_image;
    ProgressBar progressbar_add_deliveryman;
    MaterialToolbar profile_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);

        profile_toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profile_toolbar);

        imgv_pharmacy_image = findViewById(R.id.imgv_pharmacy_image);
        btn_choose_image = findViewById(R.id.btn_choose_image);
        text_input_layout_name = findViewById(R.id.text_input_layout_name);
        text_input_layout_date_of_birth = findViewById(R.id.text_input_layout_date_of_birth);
        text_input_layout_phone_number = findViewById(R.id.text_input_layout_phone_number);
        text_input_layout_password = findViewById(R.id.text_input_layout_password);
        text_input_layout_confirm_pass = findViewById(R.id.text_input_layout_confirm_pass);

        btn_submit_profile = findViewById(R.id.btn_submit_profile);
        progressbar_add_deliveryman = findViewById(R.id.progressbar_add_deliveryman);

        progressbar_add_deliveryman.setVisibility(View.GONE);
        text_input_layout_phone_number.getEditText().setText(getIntent().getStringExtra("phoneNumber"));
        text_input_layout_phone_number.setFocusable(false);
        text_input_layout_phone_number.getEditText().setFocusable(false);

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        receiveImageFromDeviceRequest(requestCode,resultCode, data);
    }

    private void receiveImageFromDeviceRequest(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imgv_pharmacy_image.setImageURI(imageUri);

            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);

                final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(360, TimeUnit.SECONDS)
                        .connectTimeout(360, TimeUnit.SECONDS)
                        .build();
                ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);


                btn_submit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        progressbar_add_deliveryman.setVisibility(View.VISIBLE);
                        name = text_input_layout_name.getEditText().getText().toString();
                        date_of_birth = text_input_layout_date_of_birth.getEditText().getText().toString();
                        phone_number = text_input_layout_phone_number.getEditText().getText().toString();
                        password = text_input_layout_password.getEditText().getText().toString();
                        String confirm_pass = text_input_layout_confirm_pass.getEditText().getText().toString();

                        if (name.isEmpty()) {
                            text_input_layout_name.setError("Name required!");
                            text_input_layout_name.requestFocus();
                        } else if (date_of_birth.isEmpty()) {
                            text_input_layout_date_of_birth.setError("Date of Birth required!");
                            text_input_layout_date_of_birth.requestFocus();
                        } else if (password.isEmpty()) {
                            text_input_layout_password.setError("Password required!");
                            text_input_layout_password.requestFocus();
                        } else if (confirm_pass.isEmpty() || !confirm_pass.equals(password)) {
                            text_input_layout_confirm_pass.setError("Password can't match!");
                            text_input_layout_confirm_pass.requestFocus();
                        } else {
                            addDeliveryMan(selectedImageBitmap,apiRequests);
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void addDeliveryMan(Bitmap selectedImageBitmap, ApiRequests apiRequests) {
        File filesDir = getApplicationContext().getFilesDir();
        File file = new File(filesDir, "Image_" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapdata = byteArrayOutputStream.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploadImage", file.getName(), reqFile);

            progressbar_add_deliveryman.setVisibility(View.VISIBLE);
            apiRequests.uploadImageToGenerateUrl(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressbar_add_deliveryman.setVisibility(View.GONE);
                    if (response.code()==200) {
                        //Toast.makeText(ProfileSetUpActivity.this, "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            String image_url = jsonObject.getString("url");
                            Toast.makeText(ProfileSetUpActivity.this, image_url, Toast.LENGTH_SHORT).show();
                            //todo.....
                            Auth auth = new Auth(phone_number, password);
                            Rider.MetaData metaData = new Rider.MetaData(name, date_of_birth, image_url, phone_number, 0, 0);
                            Rider rider = new Rider(metaData,auth);
                            rider.setAuth(auth);
                            progressbar_add_deliveryman.setVisibility(View.VISIBLE);
                            ApiClient.getInstance(MAIN_URL).createRider(rider).enqueue(new Callback<Rider>() {
                                @Override
                                public void onResponse(Call<Rider> call, Response<Rider> response2) {
                                    progressbar_add_deliveryman.setVisibility(View.GONE);
                                    if (response2.isSuccessful()) {
                                        if (response2.body()==null){
                                            Log.d(TAG, "onResponse: Can't get Profile");
                                            progressbar_add_deliveryman.setVisibility(View.GONE);
                                            return;
                                        }
                                        Toast.makeText(ProfileSetUpActivity.this, "Profile successfully set up.", Toast.LENGTH_SHORT).show();
                                        String id = response2.body().getId();
                                        String name = response2.body().getMeta_data().getName();
                                        String phone_number = response2.body().getMeta_data().getPhone_number();

                                        if (id != null && name != null && phone_number != null) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                                            sharedPreferences.edit().putString("RIDER_PHONE_NUMBER", phone_number).apply();
                                            sharedPreferences.edit().putString("RIDER_NAME", name).apply();
                                            sharedPreferences.edit().putString("RIDER_ID", id).apply();
                                            sharedPreferences.edit().putString("RIDER_PASSWORD", password).apply();
                                            startActivity(new Intent(ProfileSetUpActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        progressbar_add_deliveryman.setVisibility(View.GONE);
                                        Log.d(TAG, "onResponse: ResponseError: MetaData: "+response2.errorBody().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Rider> call, Throwable t) {
                                    progressbar_add_deliveryman.setVisibility(View.GONE);
                                    Log.d(TAG, "onFailure: Error: MetaData: "+t.getMessage());
                                }
                            });


                        } catch (JSONException e) {
                            progressbar_add_deliveryman.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    } else {
                        progressbar_add_deliveryman.setVisibility(View.GONE);
                        Toast.makeText(ProfileSetUpActivity.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: responseFailed: Image: "+response.errorBody().toString());

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressbar_add_deliveryman.setVisibility(View.GONE);
                    Toast.makeText(ProfileSetUpActivity.this, "Error: " + t.getCause(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: Error: Image: "+t.getMessage());
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            new SystemUI(ProfileSetUpActivity.this).hideSystemUI();
        }
    }

}