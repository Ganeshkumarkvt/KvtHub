package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class Socialmediaactivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private FirebaseAuth mAuth;
    private ImageView postpic;
    private EditText picdes;
    private Button postbtn;
    private Bitmap bm, bmp;
    private String imageIdentifier;
    private ListView listView;
    private ArrayList<String> usernames;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> UIDs;
    private String imagelink;
    private File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialmediaactivity);
        mAuth = FirebaseAuth.getInstance();

        postbtn = findViewById(R.id.postbtn);
        picdes = findViewById(R.id.imgdescrip);
        postpic = findViewById(R.id.imgpic);
        listView = findViewById(R.id.listview);
        usernames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usernames);
        listView.setOnItemClickListener(Socialmediaactivity.this);
        listView.setAdapter(adapter);
        UIDs = new ArrayList<>();

        postpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase();
            }
        });

    }

    private void selectImage() {
        if (VERSION.SDK_INT < 23) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1000);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                finish();
                break;
            case R.id.viewpost:
                Intent intent = new Intent(this, ViewPostActivity.class);
                startActivity(intent);
                break;
            case R.id.App_info:
                Intent intent1 = new Intent(this, Social.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            try {
            Uri choosen = data.getData();
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), choosen);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(choosen, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnindex = cursor.getColumnIndex(filePathColumn[0]);
            String picturepath = cursor.getString(columnindex);
            cursor.close();
            f = new File(picturepath);

                bm =new Compressor.Builder(this)
                        .setMaxHeight(1000)
                        .setMaxWidth(1000)
                        .build()
                        .compressToBitmap(f);
                Glide.with(this).load(bm).into(postpic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    private void uploadImageToFirebase() {

        if (bm != null) {

            postpic.setDrawingCacheEnabled(true);
            postpic.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 65, baos);
            byte[] data = baos.toByteArray();

            imageIdentifier = UUID.randomUUID().toString() + ".jpg";

            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("My Images").child(imageIdentifier).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(Socialmediaactivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FancyToast.makeText(Socialmediaactivity.this, "Uploading process was successfull", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                    if(picdes.getVisibility() == View.GONE) {

                        picdes.setVisibility(View.VISIBLE);

                        FirebaseDatabase.getInstance().getReference().child("my_users")
                                .addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        UIDs.add(snapshot.getKey());

                                        String username = (String) snapshot.child("Name").getValue();
                                        usernames.add(username);
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

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                imagelink = task.getResult().toString();
                            }
                        }
                    });

                }
            });

        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("fromWhom", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        datamap.put("ImageIdentifier", imageIdentifier);
        datamap.put("imagelink", imagelink);
        datamap.put("des", picdes.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("my_users").child(UIDs.get(position)).child("received posts").push().setValue(datamap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FancyToast.makeText(Socialmediaactivity.this, "Image sent", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                }
            }
        });
    }
}

