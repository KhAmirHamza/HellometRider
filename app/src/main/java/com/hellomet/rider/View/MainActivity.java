package com.hellomet.rider.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hellomet.rider.Adapter.ViewPagerAdapter;
import com.hellomet.rider.ApiRequests;
import com.hellomet.rider.Custom.FcmService;
import com.hellomet.rider.Custom.Restarter;
import com.hellomet.rider.Location.DeviceLocation;
import com.hellomet.rider.Model.FCM;
import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.MyFirebaseMessagingService;
import com.hellomet.rider.R;
import com.hellomet.rider.UI.SystemUI;
import com.hellomet.rider.View.Fragment.OnTheWayOrderFragment;
import com.hellomet.rider.View.Fragment.LocalActiveOrderFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Const.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.hellomet.rider.Const.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.hellomet.rider.Const.REQUEST_CHECK_SETTINGS;
import static com.hellomet.rider.Const.REQUEST_CHECK__MANUAL_SETTINGS;
import static com.hellomet.rider.Const.apiKey;
import static com.hellomet.rider.Constants.MAIN_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    DeviceLocation deviceLocation;
    boolean locationPermissionGranted = false;
    RecyclerView recy_pending_order, recy_active_order;
    String RIDER_ID, RIDER_NAME, RIDER_PHONE_NUMBER, RIDER_PASSWORD;
    ProgressBar progressbar;
    MaterialToolbar main_toolbar;
    ViewPagerAdapter viewPagerAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    TabLayout tab_layout;
    ViewPager view_pager;
    MyFirebaseMessagingService myFirebaseMessagingService;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        main_toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(main_toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressbar = findViewById(R.id.progressbar);
        tab_layout = findViewById(R.id.tab_layout);
        view_pager = findViewById(R.id.view_pager);


        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        RIDER_ID = sharedPreferences.getString("RIDER_ID", "null");
        RIDER_NAME = sharedPreferences.getString("RIDER_NAME", "null");
        RIDER_PHONE_NUMBER = sharedPreferences.getString("RIDER_PHONE_NUMBER", "null");
        RIDER_PASSWORD = sharedPreferences.getString("RIDER_PASSWORD", "null");

        SharedPreferences sp_location = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String latitude = sp_location.getString("Lat", "null");
        String longitude = sp_location.getString("Lng", "null");

        setUpViewPagerWithFragmentAndAdapter(view_pager);
        tab_layout.setupWithViewPager(view_pager);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                finish();
//                overridePendingTransition(0, 0);
//                startActivity(getIntent());
//                overridePendingTransition(0, 0);
                //requestDeviceLocation();
               // viewPagerAdapter.notifyDataSetChanged();

                Fragment currentFragment = viewPagerAdapter.getItem(view_pager.getCurrentItem());
                androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(currentFragment);
                ft.attach(currentFragment);
                ft.commit();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        requestDeviceLocation();
        startService();
    }

    private void addToken(String id, String token) {
        Log.d(TAG, "addToken: called");
        FCM fcm = new FCM(id, token);
        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
        apiRequests.createFcmToken(fcm).enqueue(new Callback<FCM>() {
            @Override
            public void onResponse(Call<FCM> call, Response<FCM> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: token: " + response.body().getToken());
                } else {
                    Log.d(TAG, "onResponse: token: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<FCM> call, Throwable t) {
                Log.d(TAG, "onFailure: fcm : " + t.getMessage());
            }
        });
    }

    private void updateToken(String id, String token) {
        Log.d(TAG, "updateToken: called");
        FCM fcm = new FCM(id, token);
        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
        apiRequests.updateFcmToken(id, fcm).enqueue(new Callback<FCM>() {
            @Override
            public void onResponse(Call<FCM> call, Response<FCM> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().getToken());
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<FCM> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void setUpViewPagerWithFragmentAndAdapter(ViewPager view_pager) {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new LocalActiveOrderFragment());
        fragmentList.add(new OnTheWayOrderFragment());

        List<String> titleList = new ArrayList<>();
        titleList.add("Local Order");
        titleList.add("On The Way");

        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager, MainActivity.this, titleList, fragmentList);
        view_pager.setAdapter(viewPagerAdapter);
//
//        SharedPreferences sharedPreferences = getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
//        int targetFragmentPosition = sharedPreferences.getInt("Target", 0);
//        if (targetFragmentPosition == 0) {
//            view_pager.setCurrentItem(0,true);
//        } else if (targetFragmentPosition == 1) {
//            view_pager.setCurrentItem(1);
//        }

        setUpFirebaseMessagingInstanceAndNotificationChannel();

    }

    private void setUpFirebaseMessagingInstanceAndNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        // String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("RIDER_ID", "null");
                        sendRegistrationTokenToServer(token, id);
                    }
                });
        myFirebaseMessagingService = new MyFirebaseMessagingService();
        Intent mServiceIntent = new Intent(this, myFirebaseMessagingService.getClass());
        startService(mServiceIntent);
    }

    private void sendRegistrationTokenToServer(String token, String id){
        Log.d(TAG, "onComplete: Current Token: " + token);
        Log.d(TAG, "onComplete: id: "+id);
        if (!id.equalsIgnoreCase("null")) {
            if (token == null) {
                Log.d(TAG, "onComplete: token is null!");
            } else {
                ApiClient.getInstance(MAIN_URL).getFcmToken(id).enqueue(new Callback<FCM>() {
                    @Override
                    public void onResponse(Call<FCM> call, Response<FCM> response) {
                        if (response.isSuccessful()){
                            if (response.body() == null) {
                                addToken(id, token);
                            }else{
                                updateToken(id, token);
                            }
                        }else{
                            Log.d(TAG, "onResponse: "+response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<FCM> call, Throwable t) {
                        Log.d(TAG, "onFailure: "+t.getMessage());
                    }
                });
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


    public void requestDeviceLocation() {
        deviceLocation = new DeviceLocation(MainActivity.this, REQUEST_CHECK_SETTINGS,
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, apiKey);
        Log.d(TAG, "requestDeviceLocation: called");
        DeviceLocation.RequestCurrentDeviceLocation requestCurrentDeviceLocation =
                new DeviceLocation.RequestCurrentDeviceLocation() {
                    @Override
                    public void onSuccess(Location location) {

                        Log.d(TAG, "onSuccess: latitude: " + location.getLatitude());
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());

                        SharedPreferences sp = getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
                        String phone_number = sp.getString("RIDER_PHONE_NUMBER", "null");
                        updateRider(phone_number, location.getLatitude(), location.getLongitude());


                        SharedPreferences sharedPreferences = getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Lat", latitude);
                        editor.putString("Lng", longitude);
                        editor.apply();
                    }

                    @Override
                    public void currentDeviceAddress(String address) {
                        //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }

                    @Override
                    public void locationRequestError(Exception e) {

                    }
                };

        deviceLocation.requestDeviceLocation(requestCurrentDeviceLocation);
    }

    private void updateRider(String phone_number, double latitude, double longitude) {
        ApiRequests apiRequests = ApiClient.getInstance(MAIN_URL);
        apiRequests.getRiderByPhoneNumber(phone_number).enqueue(new Callback<Rider>() {
            @Override
            public void onResponse(Call<Rider> call, Response<Rider> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Rider rider = response.body();
                        rider.getMeta_data().setLatitude(latitude);
                        rider.getMeta_data().setLongitude(longitude);
                        Log.d(TAG, "onResponse: lat: " + latitude);
                        Log.d(TAG, "onResponse: lat: " + rider.getMeta_data().getLatitude());
                        apiRequests.updateRider(rider.getId(), rider).enqueue(new Callback<Rider>() {
                            @Override
                            public void onResponse(Call<Rider> call, Response<Rider> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "onResponse: Location Update Successfully");
                                } else {
                                    Log.d(TAG, "onResponse: Location can not Update: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<Rider> call, Throwable t) {
                                Log.d(TAG, "onFailure: Location can not Update: " + t.getMessage());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "onResponse: Rider can not get: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Rider> call, Throwable t) {
                Log.d(TAG, "onFailure: Rider can not get: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (locationPermissionGranted) {
                //   checkLocationAndGPS();
            } else {
                //  checkLocationAndGPS();
            }
        }

        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            //Toast.makeText(this, "Well,All Location settings are satisfied now!", Toast.LENGTH_SHORT).show();
            locationPermissionGranted = true;
            requestDeviceLocation();
        }
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Please turn on Location with High Accuracy", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_CHECK__MANUAL_SETTINGS);
        }

        if (requestCode == REQUEST_CHECK__MANUAL_SETTINGS && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    locationPermissionGranted = true;
                    requestDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            } else {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationPermissionGranted = true;
                    //Toast.makeText(this, "Everything is ok", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                } else {
                    Toast.makeText(this, "High Accuracy Location permision required", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            }
        }
        if (requestCode == REQUEST_CHECK__MANUAL_SETTINGS && resultCode == RESULT_CANCELED) {
            requestDeviceLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    //Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                    requestDeviceLocation();
                }
            }
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            new SystemUI(MainActivity.this).hideSystemUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, MyFirebaseMessagingService.class);
        //startService(serviceIntent);
        //ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, FcmService.class);
        stopService(serviceIntent);
    }
}