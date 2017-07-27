package com.auribises.gweecloudapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.auribises.gweecloudapp.R;
import com.auribises.gweecloudapp.model.GWEEUser;

import java.util.ArrayList;

/**
 * Created by ishantkumar on 24/07/17.
 */

public class UserAdapter extends ArrayAdapter<GWEEUser> {

    Context context;
    int resource;
    ArrayList<GWEEUser> userList,tempList;

    public UserAdapter(Context context, int resource, ArrayList<GWEEUser> userList) {
        super(context, resource, userList);

        this.context = context;
        this.resource = resource;
        this.userList = userList;

        tempList = new ArrayList<>();
        tempList.addAll(userList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        view = LayoutInflater.from(context).inflate(resource,parent,false);
        TextView txtName = (TextView)view.findViewById(R.id.textViewName);
        TextView txtEmail = (TextView)view.findViewById(R.id.textViewEmail);

        GWEEUser user = userList.get(position);

        txtName.setText(user.getId()+" - "+user.getName());
        txtEmail.setText(user.getEmail());

        return view;

    }

    public void filter(String str){

        // Optimise Search Algo here

        userList.clear();

        if(str.length()==0){
            userList.addAll(tempList);
        }else{
            for(GWEEUser user : tempList){
                if(user.getName().toLowerCase().contains(str.toLowerCase())){
                    userList.add(user);
                }
            }
        }

        notifyDataSetChanged();
    }
}
