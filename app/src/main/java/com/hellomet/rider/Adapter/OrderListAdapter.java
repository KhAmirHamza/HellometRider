package com.hellomet.rider.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import com.hellomet.rider.Model.Order;
import com.hellomet.rider.R;
import com.hellomet.rider.View.ApiClient;
import com.hellomet.rider.View.ChatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.rider.Constants.AUTHENTICATION;
import static com.hellomet.rider.Constants.CHAT_USER_RIDER;
import static com.hellomet.rider.Constants.MAIN_URL;
import static com.hellomet.rider.Constants.ORDER_ACTIVE;
import static com.hellomet.rider.Constants.ORDER_CANCELLED;
import static com.hellomet.rider.Constants.ORDER_COMPLETED;
import static com.hellomet.rider.Constants.ORDER_ON_THE_WAY;
import static com.hellomet.rider.Constants.ORDER_PENDING;
import static com.hellomet.rider.Constants.RIDER_ID;
import static com.hellomet.rider.Constants.RIDER_NAME;
import static com.hellomet.rider.Constants.RIDER_PHONE_NUMBER;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder>{
    private static final String TAG = "OrderListAdapter";
    Activity activity;
    List<Order> orders;
    int[] detailsVisibility;
    OnItemClickListener onItemClickListener;


    public OrderListAdapter(Activity activity, List<Order> orders) {
        this.activity = activity;
        this.orders = orders;
        detailsVisibility = new int[orders.size()];

        for (int i = 0; i < orders.size(); i++) {
            detailsVisibility[i] = 0;
        }
    }


    @NonNull
    @Override
    public OrderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

     return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.iv_order_list, parent, false));

    }


    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.MyViewHolder holder, int position) {
        holder.txtv_order_id.setText(orders.get(position).getId());
        String statusCode = orders.get(position).getMeta_data().getStatus();
        if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_PENDING)){
            holder.txtv_order_status.setText(ORDER_PENDING);
            holder.txtv_order_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_txtv_pending));
        } else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ACTIVE)) {
            holder.txtv_order_status.setText(ORDER_ACTIVE);
            holder.txtv_order_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_txtv_active));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ON_THE_WAY)) {
            holder.txtv_order_status.setText(ORDER_ON_THE_WAY);
            holder.txtv_order_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_txtv_ontheway));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_COMPLETED)) {
            holder.txtv_order_status.setText(ORDER_COMPLETED);
            holder.txtv_order_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_txtv_completed));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_CANCELLED)) {
            holder.txtv_order_status.setText(ORDER_CANCELLED);
            holder.txtv_order_status.setBackground(activity.getResources().getDrawable(R.drawable.bg_txtv_cancelled));
        }

        holder.txtv_order_price.setText("৳ "+orders.get(position).getMeta_data().getTotal_price());
        Date date = new Date(Long.parseLong(orders.get(position).getMeta_data().getCreated_at()));
        //Date date = new Date(Long.parseLong("1612193842354"));
        holder.txtv_order_date.setText(date.toString());
        holder.txtv_pharmacy_name.setText(orders.get(position).getMeta_data().getPharmacy_name());
        holder.txtv_payment_method.setText("Payment Method: "+orders.get(position).getMeta_data().getPayment_method());
        holder.txtv_requirement.setText("Requirement: "+orders.get(position).getMeta_data().getRequirement());
        holder.txtv_pharmacy_address.setText(orders.get(position).getMeta_data().getPharmacy_address());
        holder.txtv_customer_name.setText(orders.get(position).getMeta_data().getUser_name());
        holder.txtv_customer_address.setText(orders.get(position).getMeta_data().getUser_address());
        //holder.relative_layout_details.setVisibility(View.GONE);

        List<Order.PrescriptionImageUrl> prescriptionImageUrls = orders.get(position).getPrescriptionImageUrls();

        if (prescriptionImageUrls!=null && prescriptionImageUrls.size()>0){
            holder.imgv_prescription.setVisibility(View.VISIBLE);
            holder.recy_order_details.setVisibility(View.GONE);
            //Toast.makeText(context, prescriptionImageUrls.get(0), Toast.LENGTH_SHORT).show();
            Picasso.with(activity).load(prescriptionImageUrls.get(0).getPrescriptionImageUrl()).placeholder(R.drawable.progress_animation).into(holder.imgv_prescription);
        }
        else if (orders.get(position).getItems()!=null){
            holder.recy_order_details.setVisibility(View.VISIBLE);
            holder.imgv_prescription.setVisibility(View.GONE);
            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(activity, orders.get(position).getItems());
            Log.d(TAG, "MyViewHolder: orderItemSize: "+orders.get(position).getItems().size());
            holder.recy_order_details.setAdapter(orderItemAdapter);
        }

        if(detailsVisibility[position] == 0 && orders.size()>1) {
            holder.relative_layout_details.setVisibility(View.GONE);
        } else {
            holder.relative_layout_details.setVisibility(View.VISIBLE);
        }
        if (!orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_CANCELLED)) {
            holder.btn_chat_with_client.setVisibility(View.VISIBLE);
        }else {
            holder.btn_chat_with_client.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
    return orders.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_order_id,txtv_order_status,txtv_order_price,txtv_order_date, txtv_pharmacy_name,txtv_payment_method,
                txtv_requirement,txtv_pharmacy_address,txtv_customer_name,txtv_customer_address;
        RecyclerView recy_order_details;
        RelativeLayout relative_layout_details;
        MaterialButton btn_chat_with_client,btn_userLocation,btn_pharmacy_location;

        ImageView imgv_prescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_order_id = itemView.findViewById(R.id.txtv_order_id);
            txtv_order_status = itemView.findViewById(R.id.txtv_order_status);
            txtv_order_price = itemView.findViewById(R.id.txtv_order_price);
            txtv_order_date = itemView.findViewById(R.id.txtv_order_date);
            txtv_pharmacy_name = itemView.findViewById(R.id.txtv_pharmacy_name);
            txtv_payment_method = itemView.findViewById(R.id.txtv_payment_method);
            txtv_requirement = itemView.findViewById(R.id.txtv_requirement);
            txtv_pharmacy_address = itemView.findViewById(R.id.txtv_pharmacy_address);
            txtv_customer_name = itemView.findViewById(R.id.txtv_customer_name);
            txtv_customer_address = itemView.findViewById(R.id.txtv_customer_address);
            relative_layout_details = itemView.findViewById(R.id.relative_layout_details);
            imgv_prescription = itemView.findViewById(R.id.imgv_prescription);

            btn_chat_with_client = itemView.findViewById(R.id.btn_chat_with_client);
            btn_userLocation = itemView.findViewById(R.id.btn_userLocation);
            btn_pharmacy_location = itemView.findViewById(R.id.btn_pharmacy_location);

            recy_order_details = itemView.findViewById(R.id.recy_order_details);
            recy_order_details.setHasFixedSize(true);


            txtv_order_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txtv_order_status.getText().toString().equalsIgnoreCase(ORDER_ACTIVE)) {
                        updateOrderToOnTheWay(orders.get(getAdapterPosition()),ORDER_ON_THE_WAY, getAdapterPosition());
                    }else if (txtv_order_status.getText().toString().equalsIgnoreCase(ORDER_ON_THE_WAY)){
                        completeOrder(orders.get(getAdapterPosition()),ORDER_COMPLETED, getAdapterPosition());
                    }
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (detailsVisibility[getAdapterPosition()] == 0)
                        detailsVisibility[getAdapterPosition()] = 1;
                    else detailsVisibility[getAdapterPosition()] = 0;
                    notifyItemChanged(getAdapterPosition());
                    Toast.makeText(activity, "Adapter Position: "+getAdapterPosition()+"", Toast.LENGTH_SHORT).show();

                    onItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            btn_chat_with_client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ChatActivity.class)
                            .putExtra("owner_id", orders.get(getAdapterPosition()).getMeta_data().getRider_name())
                            .putExtra("order_id", orders.get(getAdapterPosition()).getId())
                            .putExtra("chat_with", CHAT_USER_RIDER);

                    Log.d(TAG, "onCreate: user_id: "+orders.get(getAdapterPosition()).getMeta_data().getUser_id());
                    Log.d(TAG, "onCreate: order_id: "+orders.get(getAdapterPosition()).getId());
                    Log.d(TAG, "onCreate: chat_with: "+CHAT_USER_RIDER);

                    activity.startActivity(intent);
                }
            });
            btn_userLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double latitude =0.00,longitude = 0.00;
                    latitude = Double.parseDouble(orders.get(getAdapterPosition()).getMeta_data().getUser_lat());
                    longitude = Double.parseDouble(orders.get(getAdapterPosition()).getMeta_data().getUser_lng());
                    if (latitude !=0.00 && longitude !=0.00){
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
                        activity.startActivity(intent);
                    }
                }
            });
            btn_pharmacy_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double latitude =0.00,longitude = 0.00;
                    latitude = Double.parseDouble(orders.get(getAdapterPosition()).getMeta_data().getPharmacy_lat());
                    longitude = Double.parseDouble(orders.get(getAdapterPosition()).getMeta_data().getPharmacy_lng());
                    if (latitude !=0.00 && longitude !=0.00){
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + latitude + "," + longitude));
                        activity.startActivity(intent);
                    }
                }
            });
        }
    }
    private void updateOrderToOnTheWay(Order order, String status, int position){
        AlertDialog.Builder builder = new  AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Please Make Sure");
        builder.setMessage("Do you want to pick order? if you do this, you must have to complete order...");
        builder.setPositiveButton("Pick Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO.....
                Toast.makeText(activity, "Pick Order", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = activity.getSharedPreferences(AUTHENTICATION, Context.MODE_PRIVATE);
                String rider_id = sharedPreferences.getString(RIDER_ID,"null");
                String rider_name = sharedPreferences.getString(RIDER_NAME,"null");
                String rider_phone_number = sharedPreferences.getString(RIDER_PHONE_NUMBER,"null");
                if (rider_phone_number.contains("+")) {
                    rider_phone_number = rider_phone_number.substring(1);
                }

                order.getMeta_data().setStatus(status);
                order.getMeta_data().setRider_id(rider_id);
                order.getMeta_data().setRider_name(rider_name);
                order.getMeta_data().setRider_phone_number(rider_phone_number);

                //Updating Order with some new value.....
                updateOrder(order, position);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO.....
                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void  completeOrder(Order order, String status, int position){
        AlertDialog.Builder builder = new  AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Please Make Sure");
        builder.setMessage("Do you want to Complete order? Did you deliver the medicine to the client?");
        builder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO.....
                order.getMeta_data().setStatus(status);
                //Updating Order with some new value.....
                updateOrder(order, position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO.....
                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void updateOrder(Order order, int position){

        ApiClient.getInstance(MAIN_URL).updateOrder(order.getId(), order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()){
                    if (response.body()==null){
                        Log.d(TAG, "onResponse: Can Not Get Data.");
                        Toast.makeText(activity, "Nothing Found.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(activity, "Order Pick Up Successfully", Toast.LENGTH_SHORT).show();
                        //orders.get(getAdapterPosition()).getMeta_data().setStatus("Active");
                        orders.remove(position);
                        //notifyDataSetChanged();

                        activity.finish();
                        activity.overridePendingTransition(0, 0);
                        activity.startActivity(activity.getIntent());
                        activity.overridePendingTransition(0, 0);
//                        SharedPreferences sharedPreferences = activity.getSharedPreferences("FRAGMENT", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("Target", 1);
//                        editor.apply();

                    }
                }else {
                    Log.d(TAG, "onResponse: ResponseError: "+ response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.d(TAG, "onFailure: Error: "+t.getMessage());
            }
        });
    }




    public interface  OnItemClickListener{
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
