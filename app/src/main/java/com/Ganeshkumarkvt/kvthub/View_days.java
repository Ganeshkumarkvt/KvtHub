package com.Ganeshkumarkvt.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class View_days extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> days;
    private Calendar calendar;
    private java.text.SimpleDateFormat day;
    public static String today, yesterday;
    private Button day0, day1, day2, day3, day4, day5;
    String tommorow;
    private final String DEL_KEY = "DELETE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        setContentView(R.layout.activity_view_days);
        day0 = findViewById(R.id.today);
        day1 = findViewById(R.id.yesterday);
        day2 = findViewById(R.id.day3);
        day3 = findViewById(R.id.day4);
        day4 = findViewById(R.id.day5);
        day5 = findViewById(R.id.day6);
        day = new SimpleDateFormat("EEEE");
        today =  day.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        yesterday = day.format(calendar.getTime());
        days = new ArrayList<>();
        listcreator();
        day0.setOnClickListener(this);
        day1.setOnClickListener(this);
        day2.setOnClickListener(this);
        day3.setOnClickListener(this);
        day4.setOnClickListener(this);
        day5.setOnClickListener(this);
        SharedPreferences sharedPreferences =  getPreferences(MODE_PRIVATE);
        String T = sharedPreferences.getString(DEL_KEY,"");
        if(!Objects.equals(T, "")){
            if (!Objects.equals(T, today)) deletetommorow();
        }else {
            deletetommorow();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        day0.setText(days.get(0));
        day1.setText(days.get(1));
        day2.setText(days.get(2));
        day3.setText(days.get(3));
        day4.setText(days.get(4));
        day5.setText(days.get(5));
    }
    private void deletetommorow(){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_WEEK, 1);
        tommorow = day.format(now.getTime());
        try {
            FirebaseDatabase.getInstance().getReference().child("MyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("ReceivedPosts").child(tommorow).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        try {
                            FirebaseStorage.getInstance().getReference().child("MyImages").child("Private").child(tommorow).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(View_days.this, "Refreshed", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(DEL_KEY,today);
                                    editor.apply();
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void listcreator(){
        for (int i=0; i<=5; i++)
        {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_WEEK, -i);
            String temp = day.format(calendar.getTime());
            if(temp.equals(today)) days.add("Today");
            else if(temp.equals(yesterday)) days.add("Yesterday");
            else days.add("Last " + temp);
        }
    }

    @Override
    public void onClick(View v) {

        calendar = Calendar.getInstance();

        switch (v.getId()){
            case  R.id.today:
                calendar.add(Calendar.DAY_OF_WEEK, -0);
                break;
            case R.id.yesterday:
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                break;
            case R.id.day3:
                calendar.add(Calendar.DAY_OF_WEEK, -2);
                break;
            case R.id.day4:
                calendar.add(Calendar.DAY_OF_WEEK, -3);
                break;
            case R.id.day5:
                calendar.add(Calendar.DAY_OF_WEEK, -4);
                break;
            case R.id.day6:
                calendar.add(Calendar.DAY_OF_WEEK, -5);
                break;
        }

        String thisday = day.format(calendar.getTime());
        Intent intent = new Intent(View_days.this, View_Private_Post.class);
        intent.putExtra("thisday", thisday);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}