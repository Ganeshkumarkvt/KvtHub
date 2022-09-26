package com.Ganeshkumarkvt.kvthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class MyProfile extends AppCompatActivity implements View.OnClickListener, PostRecycleA.PostPosition {

    private ImageView imageView, imgZoom;
    private Button LoadRecent;
    private CardView  card2;
    private EditText edt, edt1, edt2, edt3, edt4, edt5, edt6, edt7;
    private EditText[] summa;
    private String id, url, userprofname;
    private RecyclerView recyclerView;
    public static Context myprofile;
    private ArrayList<String> Postid;
    private SimpleDateFormat nowtime;
    private PostRecycleA Adap;
    private ArrayList<PostData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        imageView = findViewById(R.id.imgprofile);
        imgZoom = findViewById(R.id.imgperson);
        ToggleButton editable = findViewById(R.id.togglebutton);
        card2 = findViewById(R.id.card);
        TextView phone = findViewById(R.id.TextView5);
        edt4 = findViewById(R.id.UserPhone);
        Button changeImage = findViewById(R.id.change);
        LoadRecent = findViewById(R.id.loadRecent);
        data = new ArrayList<>();
        Postid = new ArrayList<>();
        nowtime = new SimpleDateFormat("ddMMyyyyhmmss");
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LoadRecent.setOnClickListener(this);
        myprofile = this;
        recyclerView = findViewById(R.id.userRecent);
        edt = findViewById(R.id.userProfName);
        edt1 = findViewById(R.id.Usernick);
        edt2 = findViewById(R.id.UserDOB);
        edt3 = findViewById(R.id.UserEmail);
        edt4 = findViewById(R.id.UserPhone);
        edt5 = findViewById(R.id.UserHobbies);
        edt6 = findViewById(R.id.UserFav);
        edt7 = findViewById(R.id.UserWork);
        imageView.setOnClickListener(this);
        card2.setOnClickListener(this);
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
                    edt4.setText((String) dataSnapshot.child("mobileNumber").getValue());
                    edt5.setText((String) dataSnapshot.child("hobbies").getValue());
                    edt6.setText((String) dataSnapshot.child("fav").getValue());
                    edt7.setText((String) dataSnapshot.child("profession").getValue());
                    url = (String) dataSnapshot.child("profileLink").getValue();
                    Glide.with(getApplicationContext()).load(url).placeholder(R.drawable.hold).centerCrop().into(imageView);
                    Glide.with(getApplicationContext()).load(url).placeholder(R.drawable.hold).into(imgZoom);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        summa = new EditText[]{edt1, edt2, edt4, edt5, edt6, edt7};
        changeImage.setOnClickListener(this);
        editable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (EditText editText : summa) {
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        editText.setClickable(true);
                        editText.setCursorVisible(true);
                    }
                } else {
                    UserData userData1 = new UserData();
                    userData1.setProfile(edt.getText().toString());
                    userData1.setNickName(edt1.getText().toString());
                    userData1.setDateOfBirth(edt2.getText().toString());
                    userData1.setEmail(edt3.getText().toString());
                    userData1.setMobileNumber(edt4.getText().toString());
                    userData1.setHobbies(edt5.getText().toString());
                    userData1.setFav(edt6.getText().toString());
                    userData1.setProfession(edt7.getText().toString());
                    userData1.setProfileLink(url);

                    FirebaseDatabase.getInstance().getReference().child("MyUsers").child(id).setValue(userData1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                FancyToast.makeText(MyProfile.this, "Profile info updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            }
                        }
                    });
                    for (EditText editText : summa) {
                        editText.setFocusable(false);
                        editText.setFocusableInTouchMode(false);
                        editText.setClickable(false);
                        editText.setCursorVisible(false);
                    }

                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1001);
                break;
            case R.id.imgprofile:
                card2.setVisibility(View.VISIBLE);
                break;
            case R.id.loadRecent:
                LoadRecent.setVisibility(View.GONE);
                loadrecent();
                break;
            case R.id.card:
                card2.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            try {
                Uri choosen = data.getData();
                String[] filepathcolumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(Objects.requireNonNull(choosen), filepathcolumn, null, null, null);
                Objects.requireNonNull(cursor).moveToFirst();
                int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
                String path = cursor.getString(columnindex);
                cursor.close();
                File file = new File(path);
                Bitmap profIMG;
               profIMG = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(640)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToBitmap(file);

                Glide.with(getApplicationContext()).load(profIMG).centerCrop().into(imageView);

                final ProgressDialog progressDialog = new ProgressDialog(MyProfile.this);
                progressDialog.setMessage("Uploading profile image, Please wait");
                progressDialog.show();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                profIMG.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                try {
                    byteArrayOutputStream.close();
                    byteArrayOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("MyImages").child("UserProfile").child(id + ".jpeg").putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        FancyToast.makeText(MyProfile.this, e.getMessage(), FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Objects.requireNonNull(taskSnapshot.getMetadata().getReference()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    String s = Objects.requireNonNull(task.getResult()).toString();
                                    setProfileimage(s);
                                }
                            }
                        });
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setProfileimage(String link){
        FirebaseDatabase.getInstance().getReference().child("MyUsers").child(id).child("profileLink").setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FancyToast.makeText(MyProfile.this, "Profile image set successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                }else {
                    FancyToast.makeText(MyProfile.this, "Try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
        });
    }

    private void loadrecent(){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        Adap =  new PostRecycleA(data, this, this);
        recyclerView.setAdapter(Adap);
        Query ref = FirebaseDatabase.getInstance().getReference().child("Public").orderByChild("fromWhom").equalTo(userprofname);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Postid.add(snapshot.getKey());
                PostData postData = snapshot.getValue(PostData.class);
                data.add(postData);
                Adap.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String s = (String) snapshot.child("imageName").getValue();
                assert s != null;
                deleteimage(s);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static Context getMyprofile() {
        return myprofile;
    }

    private void deleteimage(String imgname){
        if(!(imgname.equals(""))){
        FirebaseStorage.getInstance().getReference().child("MyImages")
                .child("Public").child(imgname).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) FancyToast.makeText(MyProfile.this, "Post deleted successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
            }
        });}
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
    public void thatPosition(final int position, View v) {
        switch (v.getId()){
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setCancelable(false);
                alert.setMessage("Do you want to delete this post?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Public").child(Postid.get(position)).removeValue()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                FancyToast.makeText(MyProfile.this, "Go back and try again", FancyToast.LENGTH_LONG,
                                                        FancyToast.ERROR, false).show();
                                            }
                                        });
                                data.remove(position);
                                Adap.notifyDataSetChanged();
                                Postid.remove(position);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.download:
                download(MyProfile.this, "KVT" + nowtime.format(new Date()), data.get(position).getImageLink());
                break;
        }

   }


}