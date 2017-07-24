package com.auribises.gweecloudapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AllUsersActivity extends AppCompatActivity {

    @InjectView(R.id.listView)
    ListView listView;

    StringRequest request;
    RequestQueue requestQueue;

    String RETRIEVE_URL = "http://auribises.com/gwee/retrieve.php";

    ProgressDialog dialog;

    ArrayList<GWEEUser> userList;
    UserAdapter userAdapter;


    ArrayList<String> userNameList;
    ArrayAdapter<String> adapter;

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
}
