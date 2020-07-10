package com.example.kvthub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ListViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.UserHolder>{
    public ArrayList<String> user;
    public OnUserClickListener onUserClickListener;


    public UserAdapter(ArrayList<String> user, OnUserClickListener onUserClickListener){
        this.user = user;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.usertab, parent, false);
        return new UserHolder(v, onUserClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        holder.username.setText(user.get(position));
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView username;
        OnUserClickListener onUserClickListener;
        public UserHolder(@NonNull View itemView, OnUserClickListener onUserClickListener) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            this.onUserClickListener = onUserClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onUserClickListener.OnUserClick(getAdapterPosition());
        }
    }

    public interface OnUserClickListener{
        void OnUserClick(int position);
    }

}
