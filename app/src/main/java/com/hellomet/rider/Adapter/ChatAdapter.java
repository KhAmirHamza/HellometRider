package com.hellomet.rider.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.hellomet.rider.Model.Chat;
import com.hellomet.rider.R;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    Context context;
    List<Chat> chats;
    String ownerId;

    public ChatAdapter(Context context, List<Chat> chats, String ownerId) {
        this.context = context;
        this.chats = chats;
        this.ownerId = ownerId;
    }

    @Override
    public int getItemViewType(int position) {
        if (chats.size()>0 && chats.get(position).getOwnerId().equalsIgnoreCase(ownerId)){
            return 1;
        }else{
            return 2;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1){
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_message_me, parent, false));
        }else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_message_other, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtv_message.setText(chats.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtv_message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_message = itemView.findViewById(R.id.txtv_message);
        }
    }
}
