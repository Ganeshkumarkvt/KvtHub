package com.example.kvthub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class PostAdapter extends FirebaseRecyclerAdapter<PostData, PostAdapter.PostViewHolder> {
    Context context;
    public PostAdapter(@NonNull FirebaseRecyclerOptions<PostData> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostData post) {
    try{
        if (!post.getImageLink().equals("")){
            Glide.with(context).load(post.getImageLink()).into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }}catch (Exception e)
    {
        e.printStackTrace();
    }
        holder.author.setText(post.getFromWhom());
        holder.time.setText(post.getTime());
        holder.description.setText(post.getDescription());
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.postdata, parent, false);

        return new PostViewHolder(view);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView author, time, description;
        ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.posttime);
            imageView = itemView.findViewById(R.id.posimg);
            description = itemView.findViewById(R.id.descrip);

        }
    }

}
