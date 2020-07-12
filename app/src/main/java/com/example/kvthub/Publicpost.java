package com.example.kvthub;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.kvthub.R.drawable.close;
import static com.example.kvthub.R.drawable.write;


public class Publicpost extends Fragment {
    private FloatingActionButton floatingActionButton;
    private TextView peyar, neram;
    private EditText writpost;
    private CardView cardView;
    private SimpleDateFormat now;
    public static String mynam;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private Context context;
    private Button postbtn;
    String T;
    private PostData data;
    public Publicpost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mynam = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicpost, container, false);
        cardView = v.findViewById(R.id.postcard);
        context = getContext();
        recyclerView = v.findViewById(R.id.publicrecycler);
        peyar = v.findViewById(R.id.peyar);
        postbtn = v.findViewById(R.id.writpost);
        now =  new SimpleDateFormat("dd/MM/yyyy  h:mm a");
        neram = v.findViewById(R.id.neram);
        peyar.setText(mynam);
        data = new PostData();
        floatingActionButton = v.findViewById(R.id.transbtn);
        writpost = v.findViewById(R.id.note);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<PostData> options = new FirebaseRecyclerOptions.Builder<PostData>()
                .setQuery( FirebaseDatabase.getInstance().getReference().child("Public"), PostData.class)
                .setLifecycleOwner(Publicpost.this)
                .build();
        adapter = new PostAdapter(options, context);
        recyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                    floatingActionButton.setImageResource(close);
                }else {
                    cardView.setVisibility(View.GONE);
                    floatingActionButton.setImageResource(write);
                }
                postbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(writpost.getText().toString().equals("")){
                            FancyToast.makeText(getContext(), "write something to Post", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                        }else {
                            writupload();
                        }
                    }
                });
            }

        });
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        T = now.format(new Date());
        neram.setText(T);
    }

    private void writupload(){
        Long temp = Social.getMaxid();
        data.setImageName("");
        data.setImageLink("");
        data.setDescription(writpost.getText().toString());
        data.setTime(T);
        data.setFromWhom(mynam);
        FirebaseDatabase.getInstance().getReference().child("Public").child(String.valueOf(temp-1))
                .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FancyToast.makeText(getContext(), "Post successful", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS, false).show();
                    cardView.setVisibility(View.GONE);
                    floatingActionButton.setImageResource(write);
                    writpost.setText("");
                }
            }
        });
    }

}