package com.Ganeshkumarkvt.kvthub;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.Ganeshkumarkvt.kvthub.R.drawable.close;
import static com.Ganeshkumarkvt.kvthub.R.drawable.write;


public class Publicpost extends Fragment{
    private FloatingActionButton floatingActionButton;
    private TextView neram;
    private EditText writpost;
    private CardView cardView;
    private SimpleDateFormat now;
    public static String mynam;
    public static Context context;
    private Button postbtn;
    String T;
    LinearLayoutManager layoutManager;
    PublicAdapter adapter;
    private PostData data;

    public Publicpost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        mynam = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_publicpost, container, false);
        cardView = v.findViewById(R.id.postcard);
        RecyclerView recyclerView = v.findViewById(R.id.publicrecycler);
        TextView peyar = v.findViewById(R.id.peyar);
        postbtn = v.findViewById(R.id.writpost);
        now =  new SimpleDateFormat("dd/MM/yyyy  h:mm a");
        layoutManager = new LinearLayoutManager(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission_group.STORAGE}, 2000);
            }
        }
        neram = v.findViewById(R.id.neram);
        peyar.setText(mynam);
        data = new PostData();
        floatingActionButton = v.findViewById(R.id.transbtn);
        writpost = v.findViewById(R.id.note);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(writpost,0);
                    floatingActionButton.setImageResource(close);
                }else {
                    cardView.setVisibility(View.GONE);
                    floatingActionButton.setImageResource(write);
                    try{
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus()).getWindowToken(), 0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

        Query query = FirebaseDatabase.getInstance().getReference().child("Public");
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(8)
                .build();
        DatabasePagingOptions<PostData> options = new DatabasePagingOptions.Builder<PostData>()
                .setLifecycleOwner(this)
                .setQuery(query,config,PostData.class)
                .build();
        adapter = new PublicAdapter(options, getContext());
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        context = getContext();
    }

    @Override
    public void onResume() {
        super.onResume();
        T = now.format(new Date());
        neram.setText(T);
    }

    private void writupload(){
        long temp = Social.getMaxid();
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

                    try{
                        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(Objects.requireNonNull(getActivity()).getCurrentFocus()).getWindowToken(), 0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    cardView.setVisibility(View.GONE);
                    floatingActionButton.setImageResource(write);
                    writpost.setText("");
                    writpost.setFocusable(false);
                }
            }
        });
    }




}