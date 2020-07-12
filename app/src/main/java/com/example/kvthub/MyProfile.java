package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class MyProfile extends AppCompatActivity {

    ImageView imageView;
    File file;
    Bitmap profIMG;
    Button changeImage;
    ToggleButton editable;
    TextView phone;
    EditText edt, edt1, edt2, edt3, edt4, edt5, edt6, edt7;
    CardView cardView;
    EditText[] summa;
    private String id, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        imageView = findViewById(R.id.imgprofile);
        editable = findViewById(R.id.togglebutton);
        phone = findViewById(R.id.TextView5);
        edt4 = findViewById(R.id.UserPhone);
        changeImage = findViewById(R.id.change);
        id = getIntent().getExtras().getString("KEY");
        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            edt4.setVisibility(View.VISIBLE);
            phone.setVisibility(View.VISIBLE);
            editable.setVisibility(View.VISIBLE);
            changeImage.setVisibility(View.VISIBLE);
        }
        edt = findViewById(R.id.userProfName);
        edt1 = findViewById(R.id.Usernick);
        edt2 = findViewById(R.id.UserDOB);
        edt3 = findViewById(R.id.UserEmail);
        edt4 = findViewById(R.id.UserPhone);
        edt5 = findViewById(R.id.UserHobbies);
        edt6 = findViewById(R.id.UserFav);
        edt7 = findViewById(R.id.UserWork);
        FirebaseDatabase.getInstance().getReference().child("MyUsers").orderByKey().equalTo(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    edt.setText((String) dataSnapshot.child("profile").getValue());
                    edt1.setText((String) dataSnapshot.child("nickName").getValue());
                    edt2.setText((String) dataSnapshot.child("dateOfBirth").getValue());
                    edt3.setText((String) dataSnapshot.child("email").getValue());
                    edt4.setText((String) dataSnapshot.child("mobileNumber").getValue());
                    edt5.setText((String) dataSnapshot.child("hobbies").getValue());
                    edt6.setText((String) dataSnapshot.child("fav").getValue());
                    edt7.setText((String) dataSnapshot.child("profession").getValue());
                    url = (String) dataSnapshot.child("profileLink").getValue();
                    Glide.with(MyProfile.this).load(url).placeholder(R.drawable.hold).centerCrop().into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        summa = new EditText[]{edt, edt1, edt2, edt3, edt4, edt5, edt6, edt7};
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1001);
            }
        });
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

                    if(!Objects.equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), edt.getText().toString())) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(edt.getText().toString())
                                .build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest);
                    }

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            try {
                Uri choosen = data.getData();
                String[] filepathcolumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(choosen, filepathcolumn, null, null, null);
                cursor.moveToFirst();
                int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
                String path = cursor.getString(columnindex);
                cursor.close();
                file = new File(path);
                profIMG = new Compressor.Builder(this)
                        .setMaxHeight(640)
                        .setMaxWidth(640)
                        .build()
                        .compressToBitmap(file);

                Glide.with(this).load(profIMG).centerCrop().into(imageView);

                final ProgressDialog progressDialog = new ProgressDialog(MyProfile.this);
                progressDialog.setMessage("Uploading profile image, Please wait");
                progressDialog.show();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                profIMG.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                try {
                    byteArrayOutputStream.close();
                    byteArrayOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UploadTask uploadTask = (UploadTask) FirebaseStorage.getInstance().getReference().child("MyImages").child("UserProfile").child(id).putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        FancyToast.makeText(MyProfile.this, e.getMessage(), FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
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
}