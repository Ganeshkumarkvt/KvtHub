package com.Ganeshkumarkvt.kvthub;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import static com.Ganeshkumarkvt.kvthub.Publicpost.mynam;


public class Users extends Fragment {
    private ListView listusers;
    public static ArrayList<String> usernames;
    public static ArrayList<String> UIDs;

    String me;
    UserAdapter userAdapter;
    private DatabaseReference users;


    public Users() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        listusers = v.findViewById(R.id.listusers);
        me = mynam;
        usernames = new ArrayList<>();
        UIDs = new ArrayList<>();
        users = FirebaseDatabase.getInstance().getReference().child("MyUsers");
        userAdapter = new UserAdapter(usernames);
        listusers.setAdapter(userAdapter);
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait...");
            dialog.setCancelable(false);
            dialog.show();


        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!Objects.equals(snapshot.getKey(), FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    UIDs.add(snapshot.getKey());
                }
                String username = (String) snapshot.child("profile").getValue();
                try {
                    assert username != null;
                    if(!Objects.requireNonNull(username).equals(me)){
                        usernames.add(username);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                userAdapter.notifyDataSetChanged();
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
        listusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), UserProfile.class);
                intent.putExtra("KEY", UIDs.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return v;
    }
}
