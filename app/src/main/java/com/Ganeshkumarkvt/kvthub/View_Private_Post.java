package com.Ganeshkumarkvt.kvthub;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

public class View_Private_Post extends AppCompatActivity implements PostRecycleA.PostPosition {

    private String thisday;

    private static Context privateContext;
    private Query query;
    private RecyclerView recyclerView;
    private ArrayList<String> PrivPostID;
    private ArrayList<PostData> PrivData;
    PostRecycleA postadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__private__post);
        thisday = getIntent().getExtras().getString("thisday");
        if (thisday.equals(View_days.today)) setTitle("Today posts");
        else if (thisday.equals(View_days.yesterday)) setTitle("Yesterday posts");
        else setTitle("Last " + thisday + " posts");
        privateContext = this;
        PrivPostID = new ArrayList<>();
        PrivData = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        query = FirebaseDatabase.getInstance().getReference().child("MyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("ReceivedPosts").child(thisday);
        postadapter = new PostRecycleA(PrivData, this,this);
        recyclerView.setAdapter(postadapter);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                PrivPostID.add(snapshot.getKey());
                PostData postData = snapshot.getValue(PostData.class);
                PrivData.add(postData);
                postadapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static Context getContext() {
        return privateContext;
    }

    @Override
    public void thatPosition(final int position, View v) {
        final int i  = position;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setMessage("Do you want to delete this post?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("MyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("ReceivedPosts").child(thisday).child(PrivPostID.get(i)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    FancyToast.makeText(View_Private_Post.this, "Post deleted successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                    PrivData.remove(position);
                                    postadapter.notifyDataSetChanged();
                                    PrivPostID.remove(position);
                                }
                            }
                        });
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }


}