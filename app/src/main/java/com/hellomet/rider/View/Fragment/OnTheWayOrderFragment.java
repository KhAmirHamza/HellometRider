package com.hellomet.rider.View.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import com.hellomet.rider.Adapter.OrderListAdapter;
import com.hellomet.rider.Model.Order;
import com.hellomet.rider.R;
import com.hellomet.rider.View.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.AUTHENTICATION;
import static com.hellomet.rider.Constants.MAIN_URL;
import static com.hellomet.rider.Constants.RIDER_ID;
import static com.hellomet.rider.Constants.RIDER_NAME;
import static com.hellomet.rider.Constants.RIDER_PASSWORD;
import static com.hellomet.rider.Constants.RIDER_PHONE_NUMBER;


public class OnTheWayOrderFragment extends Fragment {
    private static final String TAG = "ActiveOrderFragment";

    RecyclerView recy_active_order;
    String rider_id, rider_name, rider_phone_number, rider_password;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ontheway_order, container, false);

        progressbar = view.findViewById(R.id.progressbar);
        recy_active_order = view.findViewById(R.id.recy_active_order);
        recy_active_order.setHasFixedSize(true);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE);
        rider_id = sharedPreferences.getString(RIDER_ID, "null");
        rider_name = sharedPreferences.getString(RIDER_NAME, "null");
        rider_phone_number = sharedPreferences.getString(RIDER_PHONE_NUMBER, "null");
        rider_password = sharedPreferences.getString(RIDER_PASSWORD, "null");


        SharedPreferences sp_location = getContext().getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String latitude = sp_location.getString("Lat","null");
        String longitude = sp_location.getString("Lng","null");

          getOnTheWayOrderThenSet(rider_phone_number);
        return view;
    }

    public void getOnTheWayOrderThenSet(String phone_number) {
        recy_active_order.removeAllViews();
        progressbar.setVisibility(View.VISIBLE);
        ApiClient.getInstance(MAIN_URL).getAllPickedOrdersByPhoneNumber(phone_number).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                progressbar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size()>0) {
                        Log.d(TAG, "onResponse: Size: "+response.body().size());
                        Log.d(TAG, "onResponse: Id: "+response.body().get(0).getId());
                        Log.d(TAG, "onResponse: Status: "+response.body().get(0).getMeta_data().getStatus());
                        Log.d(TAG, "onResponse: R.Name: "+response.body().get(0).getMeta_data().getRider_name());

                        OrderListAdapter onthewayOrderAdapter = new OrderListAdapter(getActivity(), response.body());
                        recy_active_order.setAdapter(onthewayOrderAdapter);

                        onthewayOrderAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Nothing Found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(MainActivity.this, "ResponseError: " + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Error: " + t.getLocalizedMessage());
            }
        });
    }
}