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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class PublicAdapter extends FirebaseRecyclerAdapter<PostData, PublicAdapter.Publicview> {
    Context context;


    public PublicAdapter(@NonNull FirebaseRecyclerOptions<PostData> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Publicview viewHolder, int position, @NonNull PostData model) {

        viewHolder.setIsRecyclable(false);
        viewHolder.time.setText(model.getTime());
        viewHolder.author.setText(model.getFromWhom());
        if (model.getImageLink().equals("")) {
            viewHolder.imageView.setVisibility(View.GONE);
            if (viewHolder.download.getVisibility() == View.VISIBLE)
                viewHolder.download.setVisibility(View.GONE);
        } else {
            Glide.with(Publicpost.context).load(model.getImageLink()).into(viewHolder.imageView);
        }
        if (model.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setText(model.getDescription());
        }
    }

    @NonNull
    @Override
    public Publicview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.postdata, parent, false);
        return new Publicview(v);
    }


//    @Override
//    protected void onLoadingStateChanged(@NonNull LoadingState state) {
//        switch (state){
//            case FINISHED:
//                break;
//            case ERROR:
//                retry();
//                break;
//        }
//    }
//
//    @NonNull
//    @Override
//    public Publicview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.postdata, parent, false);
//        return new Publicview(v);
//    }


    public static class Publicview extends RecyclerView.ViewHolder {

        TextView author, time, description;
        ImageView imageView;
        ImageButton download;

        public Publicview(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.posttime);
            imageView = itemView.findViewById(R.id.posimg);
            description = itemView.findViewById(R.id.descrip);
            download = itemView.findViewById(R.id.download);
            download.setVisibility(View.GONE);
        }
    }

}
