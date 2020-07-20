package com.Ganeshkumarkvt.kvthub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class UserAdapter  extends BaseAdapter {
    ArrayList<String> usernamesall;

    public UserAdapter(ArrayList<String> usernamesall) {
        this.usernamesall = usernamesall;
    }

    @Override
    public int getCount() {
        return usernamesall.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.usertab,parent, false);
            TextView name;
            name = convertView.findViewById(R.id.username);
            name.setText(usernamesall.get(position));}

        return convertView;
    }
}

