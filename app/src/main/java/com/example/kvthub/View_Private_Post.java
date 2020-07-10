package com.example.kvthub;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class View_Private_Post extends AppCompatActivity {

    private String thisday;

    private Context context;

    private RecyclerView recyclerView;
    PostAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__private__post);
        thisday = getIntent().getExtras().getString("thisday");
        if (thisday.equals(View_days.today)) setTitle("Today posts");
        else if (thisday.equals(View_days.yesterday)) setTitle("Yesterday posts");
        else setTitle("Last " + thisday + " posts");
        context = this;
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Query query = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("ReceivedPosts").child(thisday).child("PostData");
        FirebaseRecyclerOptions<PostData> options = new FirebaseRecyclerOptions.Builder<PostData>()
                .setQuery(query, PostData.class)
                .build();
        adapter = new PostAdapter(options, context);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}