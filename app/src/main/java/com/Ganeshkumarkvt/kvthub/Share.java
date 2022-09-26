package com.Ganeshkumarkvt.kvthub;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.Ganeshkumarkvt.kvthub.Publicpost.mynam;

import id.zelory.compressor.Compressor;

public class Share extends Fragment implements View.OnClickListener{

    private ImageView postpic;
    private EditText picdes;
    private TextView myname, timenow;
    private Button uploadbtn;
    private Bitmap bitmap;
    private File file;
    private SimpleDateFormat dateFormat, dayformat, timeis;
    private ProgressDialog progressDialog;
    private String link;
    private String description;
    private String imagename, clock;
    private ToggleButton toggleButton;
    private AlertDialog.Builder alert;
    private ImageButton Info;
    private DatabaseReference reff;
    private PostData postData;

    public Share() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_share, container, false);

        postpic =  v.findViewById(R.id.myimg);
        myname = v.findViewById(R.id.myname);
        myname.setText(mynam);
        timenow = v.findViewById(R.id.timenow);
        timeis = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        picdes = v.findViewById(R.id.mydescrip);
        uploadbtn = v.findViewById(R.id.uploadbtn);
        postpic.setOnClickListener(this);
        uploadbtn.setOnClickListener(this);
        toggleButton = v.findViewById(R.id.mode);
        reff = FirebaseDatabase.getInstance().getReference().child("Public");
        postData = new PostData();
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    toggleButton.setBackgroundResource(R.drawable.days_btn);
                    toggleButton.setTextColor(0xFFE91E63);
                }else {
                    toggleButton.setBackgroundResource(R.drawable.button_bacckground);
                    toggleButton.setTextColor(0xFFFFFFFF);
                }
            }
        });
        Info = v.findViewById(R.id.info);
        Info.setOnClickListener(this);
        dateFormat = new SimpleDateFormat("ddMMyyyyh:mm:ssa");
        timenow.setText(dateFormat.format(new Date()));
        dayformat = new SimpleDateFormat("EEEE");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        clock = timeis.format(new Date());
        timenow.setText(clock);
    }

    private void infomode(){
        alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Instruction about Modes")
                .setCancelable(false)
                .setMessage("MODE: PUBLIC\nIn this mode, All users can view your post.\n\n" +
                        "MODE: PRIVATE\nIn this mode, you can sent the post individually to specified users.  Validity of private post is 6 days")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.myimg:
                if(uploadbtn.getVisibility() == View.VISIBLE) uploadbtn.setVisibility(View.GONE);
                if(Build.VERSION.SDK_INT >= 23){
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                    }else {
                        selectImage();
                    }
                }else{
                    selectImage();
                }
                break;
            case R.id.uploadbtn:
                uploadImg();
                break;
            case R.id.info:
                infomode();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1000);


    }

    private void uploadImg(){
        if(bitmap == null){
            FancyToast.makeText(getContext(),"Please pick a photo", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
        }else {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Uploading, Please wait...");
            progressDialog.show();
            description = picdes.getText().toString();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            imagename = "IMG"+dateFormat.format(new Date()) + ".jpg";
            if(toggleButton.isChecked()){
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("MyImages").child("Private")
                    .child(dayformat.format(new Date())).child(imagename).putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(getContext(), e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Objects.requireNonNull(taskSnapshot.getMetadata().getReference()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                progressDialog.dismiss();
                                link = Objects.requireNonNull(task.getResult()).toString();
                                if (uploadbtn.getVisibility() == View.VISIBLE)
                                    uploadbtn.setVisibility(View.GONE);
                                FancyToast.makeText(getContext(), "Uploading process was successful", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                transitiontosentprivate(description, imagename, link, clock);
                                picdes.setText(null);
                                postpic.setImageResource(R.drawable.hold);
                                bitmap = null;
                            }
                        }
                    });
                }

            });

        }else if(!toggleButton.isChecked()){
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("MyImages")
                        .child("Public").child(imagename).putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FancyToast.makeText(getContext(), e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                link =  task.getResult().toString();
                                if (uploadbtn.getVisibility() == View.VISIBLE)
                                    uploadbtn.setVisibility(View.GONE);
                                posttopublic(description, imagename, link, clock);
                                bitmap = null;
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK && data != null ){
            try {
                Uri choosen = data.getData();
                String[] filepathcolumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(choosen, filepathcolumn, null, null, null);
                cursor.moveToFirst();
                int columnindex = cursor.getColumnIndex(filepathcolumn[0]);
                String path = cursor.getString(columnindex);
                cursor.close();
                file = new File(path);
                bitmap = new Compressor(getContext())
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(80)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP).compressToBitmap(file);
                Glide.with(this).load(bitmap).placeholder(R.drawable.hold).into(postpic);
                if(uploadbtn.getVisibility() == View.GONE) uploadbtn.setVisibility(View.VISIBLE);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void transitiontosentprivate(String des,String imgname, String ink, String time){
                Intent intent = new Intent(getActivity(), Send_privateActivity.class);
                intent.putExtra("Descrip", des);
                intent.putExtra("Imagename", imgname);
                intent.putExtra("ImageLink", ink);
                intent.putExtra("Time",time);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
    }

    private void posttopublic(String des, String imgname, String ink, String time){
        long temp = Social.getMaxid();
        postData.setDescription(des);
        postData.setImageLink(ink);
        postData.setImageName(imgname);
        postData.setTime(time);
        postData.setFromWhom(mynam);
        reff.child(String.valueOf(temp-1)).setValue(postData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FancyToast.makeText(getContext(), "Post update successful", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    progressDialog.dismiss();
                    picdes.setText(null);
                    postpic.setImageResource(R.drawable.hold);
                }
            }
        });
    }

}