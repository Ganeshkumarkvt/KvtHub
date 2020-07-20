package com.Ganeshkumarkvt.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppInfo extends AppCompatActivity {
    private ImageView App;
    private ListView simplelist;
    private String[] Mainitem, subitem;
    private SimpleAdapter simpleAdapter;
    private File file;
    private String UL;
    private float VN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        App = findViewById(R.id.applogo);
        simplelist = findViewById(R.id.simplelist);
        Mainitem = new String[]{"APP VERSION", "CHECK FOR UPDATES", "WEBSITE"};
        subitem = new String[]{BuildConfig.VERSION_NAME, "", "KONDALVATTAMTHIDAL G-sites"};
            List<Map<String, String>> data = new ArrayList<>();
        for (int i= 0; i<Mainitem.length; i++){
            Map<String, String> datum = new HashMap<>(2);
            datum.put("title", Mainitem[i]);
            datum.put("value", subitem[i]);
            data.add(datum);
        }
       simpleAdapter = new SimpleAdapter(this, data,
               android.R.layout.simple_list_item_2,
               new String[]{"title", "value"},
               new int[]{android.R.id.text1, android.R.id.text2});
        simplelist.setAdapter(simpleAdapter);
        file = new File("/KvtHub/App/");
        simplelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(AppInfo.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                            } else {
                                checkforUpdates();
                            }
                        } else {
                            checkforUpdates();
                        }


                break;
                case 2:
                Intent intent = new Intent(AppInfo.this, KvtWebView.class);
                startActivity(intent);
                break;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults[0] == RESULT_OK){
            checkforUpdates();
        }
    }

    private void download(Context context, String fileName, String url){
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName+".apk");
        request.setDescription("Downloading APK...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ".apk");

        downloadManager.enqueue(request);
    }
    private void checkforUpdates(){
        FirebaseDatabase.getInstance().getReference().child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VN = Float.parseFloat((String) Objects.requireNonNull(snapshot.child("appVersion").getValue()));
                if(VN > Float.parseFloat(BuildConfig.VERSION_NAME)){
                    UL = (String) snapshot.child("applink").getValue();
                    AlertDialog.Builder alert = new AlertDialog.Builder(AppInfo.this);
                    alert.setTitle("Update available")
                            .setCancelable(false)
                            .setMessage("KvtHubv"+VN+"\nNew version available, Download now")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    download(AppInfo.this,"KvtHubv"+VN,UL);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }else{
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AppInfo.this);
                    alertDialog.setTitle("Check for update")
                            .setMessage("No update available")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}