package com.example.kvthub;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    String T;
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
        now =  new SimpleDateFormat("dd/MM/yyyy  h:mm a");
        neram = v.findViewById(R.id.neram);
        peyar.setText(mynam);
        floatingActionButton = v.findViewById(R.id.transbtn);
        writpost = v.findViewById(R.id.note);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Query query = FirebaseDatabase.getInstance().getReference().child("MyUsers").child("Public");
        FirebaseRecyclerOptions<PostData> options = new FirebaseRecyclerOptions.Builder<PostData>()
                .setQuery(query, PostData.class)
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

}