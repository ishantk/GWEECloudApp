package com.auribises.gweecloudapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllUsersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    @InjectView(R.id.listView)
    ListView listView;

    StringRequest request;
    RequestQueue requestQueue;

    String RETRIEVE_URL = "http://auribises.com/gwee/retrieve.php";
    String DELETE_URL = "http://auribises.com/gwee/delete.php";

    ProgressDialog dialog;

    ArrayList<GWEEUser> userList;
    UserAdapter userAdapter;


    ArrayList<String> userNameList;
    ArrayAdapter<String> adapter;

    GWEEUser user;
    int pos;

    //RecyclerView and RecyclerView.Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        ButterKnife.inject(this);

        requestQueue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait..");

        retrieveAllUsers();

    }

    void retrieveAllUsers(){

        request = new StringRequest(
                Request.Method.GET,
                RETRIEVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            JSONArray jsonArray = jsonObject.getJSONArray("users");

                            userList = new ArrayList<>();
                            userNameList = new ArrayList<>();

                            int id=0;
                            String n="",e="",p="",g="",c="";

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jObj = jsonArray.getJSONObject(i);

                                id = jObj.getInt("id");
                                n = jObj.getString("name");
                                e = jObj.getString("email");
                                p = jObj.getString("password");
                                g = jObj.getString("gender");
                                c = jObj.getString("city");

                                GWEEUser user = new GWEEUser(id,n,e,p,g,c);
                                userList.add(user);

                                userNameList.add(n);
                            }

                            //adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,userNameList);
                            //listView.setAdapter(adapter);

                            userAdapter = new UserAdapter(getApplicationContext(),R.layout.list_item,userList);
                            listView.setAdapter(userAdapter);
                            listView.setOnItemClickListener(AllUsersActivity.this);
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Some Exception: "+e,Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                        dialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Some Volley Error: "+error,Toast.LENGTH_LONG).show();
                    }
                }
        );

        dialog.show();
        requestQueue.add(request); // Process the Request

    }

    void showUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Details of "+user.getName());
        builder.setMessage(user.toString());
        builder.setPositiveButton("Done",null);
        builder.create().show();
    }

    void askForDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+user.getName());
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser();
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.create().show();
    }

    void deleteUser(){
        request = new StringRequest(Request.Method.POST,
                        DELETE_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    int success = jsonObject.getInt("success");
                                    String message = jsonObject.getString("message");

                                    if(success == 1){
                                        userList.remove(pos);
                                        userAdapter.notifyDataSetChanged(); // refresh listview
                                    }

                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),"Some Exception: "+e,Toast.LENGTH_LONG).show();
                                }

                                dialog.dismiss();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Some Error: "+error,Toast.LENGTH_LONG).show();
                            }
                        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map = new HashMap<>();
                map.put("id",String.valueOf(user.getId()));

                return map;
            }
        }
        ;

        dialog.show();
        requestQueue.add(request); // Request shall be processed now
    }

    void showOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"View User","Delete User","Update User","Call User"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i){
                    case 0:
                        showUser();
                        break;
                    case 1:
                        askForDeletion();
                        break;
                    case 2:
                        Intent intent = new Intent(AllUsersActivity.this,RegisterUserActivity.class);
                        intent.putExtra("keyUser",user);
                        startActivity(intent);
                        break;
                    case 3:

                        break;

                }

            }
        });
        builder.create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        pos = i;
        user = userList.get(i);
        showOptions();
    }
}
