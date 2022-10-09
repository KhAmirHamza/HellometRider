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

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.hellomet.rider.Adapter.OrderListAdapter;
import com.hellomet.rider.Model.Order;
import com.hellomet.rider.R;
import com.hellomet.rider.View.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.MAIN_URL;


public class LocalActiveOrderFragment extends Fragment {
    private static final String TAG = "LocalActiveOrderFragme";

    RecyclerView recy_pending_order;
    String RIDER_ID, RIDER_NAME, RIDER_PHONE_NUMBER, RIDER_PASSWORD;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_active_order, container, false);

        recy_pending_order = view.findViewById(R.id.recy_pending_order);
        progressbar = view.findViewById(R.id.progressbar);

        recy_pending_order.setHasFixedSize(true);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        RIDER_ID =   sharedPreferences.getString("RIDER_ID", "null");
        RIDER_NAME = sharedPreferences.getString("RIDER_NAME", "null");
        RIDER_PHONE_NUMBER = sharedPreferences.getString("RIDER_PHONE_NUMBER", "null");
        RIDER_PASSWORD = sharedPreferences.getString("RIDER_PASSWORD", "null");

        SharedPreferences sp_location = getContext().getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String latitude = sp_location.getString("Lat","0.00");
        String longitude = sp_location.getString("Lng","0.00");
        Log.d(TAG, "onCreateView: latitude: "+latitude);
        Log.d(TAG, "onCreateView: longitude: "+longitude);

        LatLng currentPositionLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        getLocalOrderByDistanceAndStatusThenSet(currentPositionLatLng);
        return view;
    }

    private void getLocalOrderByDistanceAndStatusThenSet(LatLng currentPositionLatlng){
        progressbar.setVisibility(View.VISIBLE);
        ApiClient.getInstance(MAIN_URL+"order/").getLocalOrderByDistanceAndStatus(
                "Active","local", String.valueOf(currentPositionLatlng.latitude),
                String.valueOf(currentPositionLatlng.longitude), "3")
                .enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        progressbar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            if (response.body() == null) {
                                Toast.makeText(getContext(), "Nothing Found.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //List<Order> localOrders = getLocalOrders(currentPositionLatlng, response.body(), 3.0);
                            Log.d(TAG, "onResponse: local orders: length: "+response.body().size());
                            Toast.makeText(getContext(), "Size: "+response.body().size(), Toast.LENGTH_SHORT).show();
                            OrderListAdapter orderListAdapter = new OrderListAdapter(getActivity(), response.body());
                            recy_pending_order.setAdapter(orderListAdapter);
                            orderListAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                }
                            });

                        } else {
                            //assert response.errorBody() != null;
                            //Toast.makeText(MainActivity.this, "ResponseError: "+ response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: ResponseError: " + response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        progressbar.setVisibility(View.GONE);
                        //Toast.makeText(MainActivity.this, "Error: "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: Error: " + t.getLocalizedMessage());
                    }
                });
    }

    public List<Order> getLocalOrders(LatLng currentPositionLatLng,
                                      List<Order> allOrders, double targetedAreaInKM) {
        Log.d(TAG, "getLocalOrders: called");
        List<Order> localOrders = new ArrayList<>();

        for (int i = 0; i < allOrders.size(); i++) {

            double latitude = Double.parseDouble(allOrders.get(i).getMeta_data().getPharmacy_lat());
            double longitude = Double.parseDouble(allOrders.get(i).getMeta_data().getPharmacy_lng());

            double distance = getDistanceOfAreaFromOnePlaceToAnother(currentPositionLatLng, new LatLng(latitude, longitude));
            distance = new BigDecimal(distance).setScale(3, RoundingMode.HALF_UP).doubleValue();
            Log.d(TAG, "getLocalPharmacy: Position:" + i + "   Distance: " + distance);
            if (targetedAreaInKM >= distance) {
                Order order = allOrders.get(i);
                // order.getMeta_data().setDistance(distance + " km");
                localOrders.add(order);
            }
        }
        return localOrders;

    }

    public double getDistanceOfAreaFromOnePlaceToAnother(LatLng firstPlaceLatLng, LatLng secondPlaceLatLng) {
        Log.d(TAG, "getDistanceOfAreaFromUserToPharmacy: called");

        double distanceLat = 0, distanceLng = 0;
        double disLatKM = 0, disLngKM = 0;
        double distance = 0;

        if (firstPlaceLatLng.latitude < secondPlaceLatLng.latitude) {

            distanceLat = secondPlaceLatLng.latitude - firstPlaceLatLng.latitude;
            distanceLng = secondPlaceLatLng.longitude - firstPlaceLatLng.longitude;

            disLatKM = (40075 / 360) * distanceLat;
            disLngKM = (40075 / 360) * distanceLng;

            distance = Math.sqrt(((disLatKM * disLatKM) + (disLngKM * disLngKM)));

            return distance;
        } else {
            distanceLat = firstPlaceLatLng.latitude - secondPlaceLatLng.latitude;
            distanceLng = firstPlaceLatLng.longitude - secondPlaceLatLng.longitude;

            disLatKM = (40075 / 360) * distanceLat;
            disLngKM = (40075 / 360) * distanceLng;

            distance = Math.sqrt(((disLatKM * disLatKM) + (disLngKM * disLngKM)));

            return distance;
        }

    }


}