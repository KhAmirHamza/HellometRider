package com.hellomet.rider.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.hellomet.rider.Adapter.OrderListAdapter;
import com.hellomet.rider.ApiRequests;
import com.hellomet.rider.Model.Auth;
import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.Model.Order;
import com.hellomet.rider.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.MAIN_URL;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    MaterialToolbar profile_toolbar;
    ImageView imgv_profile_image;
    SharedPreferences sharedPreferences;
    TextView txtv_email;   
    TextView txtv_name;
    TextView txtv_phone_number;
    RecyclerView recy_completed_order;
    ProgressBar progressbar;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("AUTHENTICATION", 0);
        imgv_profile_image = findViewById(R.id.imgv_profile_image);
        txtv_name = findViewById(R.id.txtv_name);
        txtv_email = findViewById(R.id.txtv_email);
        txtv_phone_number = findViewById(R.id.txtv_phone_number);
        progressbar = findViewById(R.id.progressbar);
        scrollView = findViewById(R.id.scrollView);
        profile_toolbar = (MaterialToolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(profile_toolbar);
        setTitle((CharSequence) "Profile");


        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);

        String riderPhoneNumber = sharedPreferences.getString("RIDER_PHONE_NUMBER", "null");
        if (!riderPhoneNumber.equalsIgnoreCase("null")) {
            Log.d(TAG, "onCreateView: riderPhoneNumber: " + riderPhoneNumber);
//        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            riderPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
//            Toast.makeText(ProfileActivity.this, riderPhoneNumber, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "onCreateView: Firebase: riderPhoneNumber: " + riderPhoneNumber);
        } else if (riderPhoneNumber == null){
            Log.d(TAG, "onCreateView: riderPhoneNumber not found.");
        }

        if (!riderPhoneNumber.isEmpty()) {
            progressbar.setVisibility(View.VISIBLE);

            Rider.MetaData metaData = new Rider.MetaData(
                    "...", "...", "...",
                    "...", 0, 0);
            Auth auth = new Auth("...", "...");
            Rider rider = new Rider(metaData, auth);

            apiRequests.getRiderByPhoneNumber(riderPhoneNumber).enqueue(new Callback<Rider>() {
                public void onResponse(Call<Rider> call, Response<Rider> response) {
                    progressbar.setVisibility(View.GONE);
                    if (!response.isSuccessful()) {
                        Log.d(TAG, "onResponse: ResponseError: " + response.errorBody().toString());
                        //Toast.makeText(ProfileActivity.this, "ResponseError:" + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    } else if (response.body() != null) {

                        if (response.body().getMeta_data().getImage_url()!=null){
                            Picasso.with(ProfileActivity.this)
                                    .load(response.body().getMeta_data().getImage_url())
                                    .into(imgv_profile_image);
                            txtv_name.setText(response.body().getMeta_data().getName());
                            //txtv_email.setText(response.body().getMeta_data().get());
                            txtv_phone_number.setText(response.body().getMeta_data().getPhone_number());
                        }

                    }else {
                        Toast.makeText(ProfileActivity.this, "Nothing Found.", Toast.LENGTH_SHORT).show();
                    }
                }

                public void onFailure(Call<Rider> call, Throwable t) {
                    progressbar.setVisibility(View.GONE);
                    Log.d(TAG, "onFailure: "+t.getMessage());

                }
            });
        }

        sharedPreferences = getSharedPreferences("AUTHENTICATION", 0);
        getAllOrderThenSet(riderPhoneNumber);


    }


    private void getAllOrderThenSet(String deliverymanPhoneNumber){
        recy_completed_order = findViewById(R.id.recy_completed_order);
        recy_completed_order.setHasFixedSize(true);
        ApiRequests completedOrderApiRequest = ApiClient.getInstance(MAIN_URL);
        if (deliverymanPhoneNumber.equalsIgnoreCase("null")){
            Toast.makeText(ProfileActivity.this, "Please Sign in.", Toast.LENGTH_SHORT).show();
        }else {
            progressbar.setVisibility(View.VISIBLE);
            completedOrderApiRequest.getAllPickedOrdersByPhoneNumber(deliverymanPhoneNumber).enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    progressbar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().size()>0) {

                            OrderListAdapter completedOrderListAdapter = new OrderListAdapter(ProfileActivity.this, response.body());
                            recy_completed_order.setAdapter(completedOrderListAdapter);
                            //scrollView.scrollTo(0, scrollView.getBottom());
                            //scrollView.fullScroll(View.FOCUS_UP);

                            completedOrderListAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                }
                            });
                        }else {
                            Toast.makeText(ProfileActivity.this, "Nothing Found!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        //Toast.makeText(ProfileActivity.this, "Response Error: "+ response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: Error: "+ response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    progressbar.setVisibility(View.GONE);
                    //Toast.makeText(ProfileActivity.this, "Error: "+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure: Error: "+ t.getLocalizedMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                if (!sharedPreferences.getString("RIDER_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                    sharedPreferences.edit().remove("RIDER_PHONE_NUMBER").apply();
                    startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            } else if (!sharedPreferences.getString("RIDER_PHONE_NUMBER", "null").equalsIgnoreCase("null")) {
                sharedPreferences.edit().remove("RIDER_PHONE_NUMBER").apply();
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                Toast.makeText(ProfileActivity.this, "You are not registered!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}