package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.stream.UrlLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ListView postlistview;
    private ArrayList<String> usernames;
    private ArrayAdapter arrayAdapter;
    private FirebaseAuth firebaseAuth;
    private ImageView postview;
    private TextView desview;
    private ArrayList<DataSnapshot> dataSnapshots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        firebaseAuth = FirebaseAuth.getInstance();

        postlistview = findViewById(R.id.listpostu);
        postview = findViewById(R.id.postview);
        desview = findViewById(R.id.descriptionview);
        usernames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
        postlistview.setAdapter(arrayAdapter);
        dataSnapshots = new ArrayList<>();
        postlistview.setOnItemClickListener(ViewPostActivity.this);
        postlistview.setOnItemLongClickListener(ViewPostActivity.this);

        FirebaseDatabase.getInstance().getReference().child("my_users").child(firebaseAuth.getCurrentUser().getUid()).child("received posts")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        dataSnapshots.add(snapshot);
                        String fromWhom = (String) snapshot.child("fromWhom").getValue();
                        usernames.add(fromWhom);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        int i = 0;
                        for (DataSnapshot shot : dataSnapshots){
                            if (shot.getKey().equals(snapshot.getKey())){
                                dataSnapshots.remove(i);
                                usernames.remove(i);
                            }
                            i++;
                        }
                        arrayAdapter.notifyDataSetChanged();
                        postview.setImageResource(R.drawable.photoholder);
                        desview.setText("Description shown here");
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataSnapshot mydatasnapshot = dataSnapshots.get(position);
        String link = (String) mydatasnapshot.child("imagelink").getValue();
        try {
            Glide.with(ViewPostActivity.this).load(link).placeholder(R.drawable.photoholder).into(postview);
            desview.setText((String) mydatasnapshot.child("des").getValue());
    }catch (Exception e){
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        }else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("Delate entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseStorage.getInstance().getReference().child("My Images")
                                .child((String) dataSnapshots.get(position).child("ImageIdentifier").getValue())
                                .delete();

                        FirebaseDatabase.getInstance().getReference()
                                .child("my_users").child(firebaseAuth.getCurrentUser().getUid())
                                .child("received posts").child(Objects.requireNonNull(dataSnapshots.get(position).getKey())).removeValue();

                        dialog.dismiss();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


        return true;
    }
}