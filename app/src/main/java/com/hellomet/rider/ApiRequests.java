package com.hellomet.rider;

import com.google.gson.JsonObject;

import java.util.List;

import com.hellomet.rider.Model.Rider;
import com.hellomet.rider.Model.FCM;
import com.hellomet.rider.Model.Order;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequests {

    @Multipart
    @POST("uploadImageToGenarateUrl")
    Call<JsonObject> uploadImageToGenerateUrl(
            @Part MultipartBody.Part profileImage
    );

    @POST("rider")
    Call<Rider> createRider(@Body Rider rider);

    @GET("auth")
    Call<Rider> authenticateRider(@Query("phone_number") String phone_number, @Query("password") String password);

    @GET("rider")
    Call<Rider> getRiderByPhoneNumber(@Query("phone_number") String phone_number);

    @GET("all")
    Call<List<Order>> getAllOrder();

    @GET("all")
    Call<List<Order>> getLocalOrderByDistanceAndStatus(@Query("status") String status, @Query("action") String action, @Query("lat") String lat,
                                                     @Query("lng") String lng, @Query("max_distance") String max_distance);

    @GET("order")
    Call<List<Order>> getAllOrderByStatus(@Query("status") String status);

    @GET("order")
    Call<List<Order>> getAllPickedOrdersByPhoneNumber(@Query("rider_phone_number") String rider_phone_number);

    @PATCH("order/{id}")
    Call<Order> updateOrder(@Path("id") String id, @Body Order order);

    @GET("order")
    Call<List<Order>> getOrdersByPhoneNumberAndStatus(@Query("rider_phone_number") String rider_phone_number,
                                                      @Query("status") String status);

    @GET("rider")
    Call<Rider> checkRiderIsAvailableOrNot(@Query("phone_number") String phone_number);

    @POST("fcm")
    Call<FCM> createFcmToken(@Body FCM fcm);
    @GET("fcm")
    Call<FCM> getFcmToken(@Query("id") String id);

    @PATCH("fcm/{id}")
    Call<FCM> updateFcmToken(@Path("id") String id, @Body FCM fcm);

    @PATCH("rider/{id}")
    Call<Rider> updateRider(@Path("id") String id, @Body Rider rider);


}
