package com.Ganeshkumarkvt.kvthub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class PostRecycleA extends RecyclerView.Adapter<PostRecycleA.Recycleview>  {
    ArrayList<PostData> Dta;
    public static Context context;
    PostPosition postPosition;

    public PostRecycleA(ArrayList<PostData> dta, Context context, PostPosition postPosition) {
        Dta = new ArrayList<>();
        Dta = dta;
        PostRecycleA.context = context;
        this.postPosition = postPosition;
    }

    @NonNull
    @Override
    public Recycleview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.postdata,parent,false);
        return new Recycleview(v, postPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull Recycleview holder, int position) {
        PostData postData = Dta.get(position);
        holder.setIsRecyclable(false);
        holder.time.setText(postData.getTime());
        holder.author.setText(postData.getFromWhom());
        if(postData.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setText(postData.getDescription());
        }
        if (postData.getImageLink().equals("")){ holder.imageView.setVisibility(View.GONE);
            if (holder.download.getVisibility() == View.VISIBLE) holder.download.setVisibility(View.GONE);
        }else {
            Glide.with(context).load(postData.getImageLink()).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return Dta.size();
    }

    public static class Recycleview extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView author, time, description;
        ImageView imageView;
        ImageButton download, Delete;
        PostPosition postPosition;
        public Recycleview(@NonNull View itemView, PostPosition postPosition) {
            super(itemView);
            this.postPosition = postPosition;
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.posttime);
            imageView = itemView.findViewById(R.id.posimg);
            description = itemView.findViewById(R.id.descrip);
            download = itemView.findViewById(R.id.download);
            download.setOnClickListener(this);
            Delete = itemView.findViewById(R.id.delete);
            Delete.setOnClickListener(this);
            if (context == MyProfile.getMyprofile()){
                Delete.setVisibility(View.VISIBLE);
            }else if(context == View_Private_Post.getContext()){
                Delete.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.download:
                    postPosition.thatPosition(getAdapterPosition(), download);
                    break;
                case R.id.delete:
                    postPosition.thatPosition(getAdapterPosition(), Delete);
                    break;
            }
        }
    }
    public interface PostPosition{
        void thatPosition(int position, View v);
    }
}
