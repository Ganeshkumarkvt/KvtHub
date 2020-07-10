package com.example.kvthub;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.kvthub.Publicpost.mynam;


public class Users extends Fragment implements UserAdapter.OnUserClickListener {
    private RecyclerView listusers;
    public static ArrayList<String> usernames;
    public static ArrayList<String> UIDs;
    String me;


    public Users() {
        // Required empty public constructor
    }


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        listusers = v.findViewById(R.id.listusers);
        me = mynam;
        usernames = new ArrayList<>();
        UIDs = new ArrayList<>();
        listusers.setLayoutManager(new LinearLayoutManager(getContext()));
        final UserAdapter userAdapter = new UserAdapter(usernames, this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait...");
            dialog.show();


        FirebaseDatabase.getInstance().getReference().child("MyUsers").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    UIDs.add(snapshot.getKey());
                }
                String username =(String)snapshot.child("Name").getValue();
                try {
                    if(!Objects.requireNonNull(username).equals(me)){
                        usernames.add(username);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                listusers.setAdapter(userAdapter);
                dialog.dismiss();
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
        return v;
    }

    @Override
    public void OnUserClick(int position) {
        Toast.makeText(getContext(), UIDs.get(position), Toast.LENGTH_SHORT).show();
        Log.i("My", position + "");
    }
}
