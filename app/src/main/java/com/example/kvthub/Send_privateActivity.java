package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.kvthub.Publicpost.mynam;

public class Send_privateActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private RecyclerView listsend;
    private ArrayList<String> S_users, S_Uid;
    private String ImgLink, description, imagename, posttime;
    private SimpleDateFormat dayFormat;
    private DatabaseReference Private;
    private PostData post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_private);
        Private = FirebaseDatabase.getInstance().getReference().child("MyUsers");
        S_users = new ArrayList<>();
        S_Uid = new ArrayList<>();
        S_users = Users.usernames;
        S_Uid = Users.UIDs;
        ImgLink = getIntent().getExtras().getString("ImageLink");
        description = getIntent().getExtras().getString("Descrip");
        imagename = getIntent().getExtras().getString("Imagename");
        posttime = getIntent().getExtras().getString("Time");
        listsend = findViewById(R.id.recyclesend);
        listsend.setLayoutManager(new LinearLayoutManager(this));
        UserAdapter user = new UserAdapter(S_users, this);
        listsend.setAdapter(user);
        dayFormat = new SimpleDateFormat("EEEE");
        post = new PostData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void OnUserClick(final int position) {
        post.setFromWhom(mynam);
        post.setTime(posttime);
        post.setImageName(imagename);
        post.setDescription(description);
        post.setImageLink(ImgLink);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm").setMessage("Do you want to send this post to "+ S_users.get(position))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Private.child(S_Uid.get(position)).child("ReceivedPosts").child(dayFormat.format(new Date())).push().setValue(post)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FancyToast.makeText(Send_privateActivity.this, "Post sent to "+S_users.get(position),FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();

    }
}