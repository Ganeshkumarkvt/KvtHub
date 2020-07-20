package com.Ganeshkumarkvt.kvthub;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Send_privateActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<String> S_users, S_Uid;
    private String ImgLink, description, imagename, posttime;
    private SimpleDateFormat dayFormat;
    private PostData post;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_private);
        S_users = new ArrayList<>();
        S_Uid = new ArrayList<>();
        S_users = Users.usernames;
        S_Uid = Users.UIDs;
        ImgLink = Objects.requireNonNull(getIntent().getExtras()).getString("ImageLink");
        description = getIntent().getExtras().getString("Descrip");
        imagename = getIntent().getExtras().getString("Imagename");
        posttime = getIntent().getExtras().getString("Time");
        ListView listsend = findViewById(R.id.recyclesend);
        dayFormat = new SimpleDateFormat("EEEE");
        UserAdapter userAdapter = new UserAdapter(S_users);
        listsend.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
        post = new PostData();
        listsend.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        post.setFromWhom(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        post.setDescription(description);
        post.setImageLink(ImgLink);
        post.setImageName(imagename);
        post.setTime(posttime);
        AlertDialog.Builder alert = new AlertDialog.Builder(Send_privateActivity.this);
        alert.setTitle("Send")
                .setMessage("Do you want to send this post to " + S_users.get(position))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("MyUsers").child(S_Uid.get(position))
                                .child("ReceivedPosts").child(dayFormat.format(new Date()))
                                .push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FancyToast.makeText(Send_privateActivity.this, "Post sent to " + S_users.get(position), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                }
                            }
                        });

                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();

    }

}
