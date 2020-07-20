package com.Ganeshkumarkvt.kvthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class UserProfile extends AppCompatActivity implements View.OnClickListener, PostRecycleA.PostPosition {

    ImageView profimg, zoom;
    Button Load;
    RecyclerView UserRecentRecycler;
    EditText edt, edt1, edt2, edt3, edt4, edt5, edt6;
    CardView cardx;
    public static Context con;
    String id, userprofname, url;
    ArrayList<String> POSTID, POSTLink;
    PostRecycleA adapter;
    ArrayList<PostData> postDataArrayList;
    SimpleDateFormat timenowis;
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        edt = findViewById(R.id.userName);
        postDataArrayList = new ArrayList<>();
        con = this;
        edt1 = findViewById(R.id.UserNick);
        edt2 = findViewById(R.id.Userdob);
        edt3 = findViewById(R.id.Useremail);
        edt4 = findViewById(R.id.Userhobbies);
        edt5 = findViewById(R.id.Userfav);
        edt6 = findViewById(R.id.Userwork);
        POSTLink = new ArrayList<>();
        id = Objects.requireNonNull(getIntent().getExtras()).getString("KEY");
        UserRecentRecycler = findViewById(R.id.userrecent);
        Load = findViewById(R.id.loadrecent);
        POSTID = new ArrayList<>();
        timenowis = new SimpleDateFormat("ddMMyyyyhmmss");
        profimg = findViewById(R.id.profileIMG);
        zoom = findViewById(R.id.imgperson);
        cardx = findViewById(R.id.cardzoom);
        profimg.setOnClickListener(this);
        Load.setOnClickListener(this);
        cardx.setOnClickListener(this);
        info();
    }
    private void info(){
        FirebaseDatabase.getInstance().getReference().child("MyUsers").orderByKey()
                .equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userprofname = (String) dataSnapshot.child("profile").getValue();
                    edt.setText(userprofname);
                    edt1.setText((String) dataSnapshot.child("nickName").getValue());
                    edt2.setText((String) dataSnapshot.child("dateOfBirth").getValue());
                    edt3.setText((String) dataSnapshot.child("email").getValue());
                    edt4.setText((String) dataSnapshot.child("hobbies").getValue());
                    edt5.setText((String) dataSnapshot.child("fav").getValue());
                    edt6.setText((String) dataSnapshot.child("profession").getValue());
                    url = (String) dataSnapshot.child("profileLink").getValue();
                    Glide.with(getApplicationContext()).load(url).placeholder(R.drawable.hold).centerCrop().into(profimg);
                    Glide.with(getApplicationContext()).load(url).placeholder(R.drawable.hold).into(zoom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loadrecent:
                Load.setVisibility(View.GONE);
                loadrecent();
                break;
            case R.id.profileIMG:
                cardx.setVisibility(View.VISIBLE);
                break;
            case R.id.cardzoom:
                cardx.setVisibility(View.GONE);
                break;
        }
    }

    private void loadrecent(){
        UserRecentRecycler.setVisibility(View.VISIBLE);
        UserRecentRecycler.setLayoutManager(new LinearLayoutManager(this));
        UserRecentRecycler.setHasFixedSize(true);
        adapter  = new PostRecycleA(postDataArrayList, this, this);
        UserRecentRecycler.setAdapter(adapter);
        Query ref = FirebaseDatabase.getInstance().getReference().child("Public").orderByChild("fromWhom").equalTo(userprofname).limitToFirst(70);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    PostData postData = snapshot.getValue(PostData.class);
                    postDataArrayList.add(postData);
                    adapter.notifyDataSetChanged();

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


    private void download(Context context, String fileName, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName + ".jpeg");
        request.setDescription("Downloading image...");
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , fileName + ".jpeg");
        downloadManager.enqueue(request);
    }

    @Override
    public void thatPosition(int position, View v) {
        if(v.getId() == R.id.download) download(this,"KVT"+timenowis.format(new Date()), postDataArrayList.get(position).getImageLink());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}