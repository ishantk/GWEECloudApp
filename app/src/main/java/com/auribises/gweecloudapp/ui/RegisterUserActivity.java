package com.auribises.gweecloudapp.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auribises.gweecloudapp.model.GWEEUser;
import com.auribises.gweecloudapp.R;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.editTextPassword)
    EditText eTxtPassword;

    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    @InjectView(R.id.spinnerCity)
    Spinner spCity;

    @InjectView(R.id.buttonRegister)
    Button btnRegister;

    ArrayAdapter<String> adapter;

    GWEEUser user,rcvUser;

    StringRequest request;
    RequestQueue requestQueue;

    String REGISTER_URL = "http://auribises.com/gwee/insert.php";
    String UPDATE_URL = "http://auribises.com/gwee/update.php";

    ProgressDialog dialog;

    boolean updateMode;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        ButterKnife.inject(this);

        // Initialize Volley's Request Queue
        requestQueue = Volley.newRequestQueue(this);

        user = new GWEEUser();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select City--"); //0
        adapter.add("Ludhiana"); //1
        adapter.add("Chandigarh"); //2
        adapter.add("Delhi"); //3
        adapter.add("Bengaluru"); //4
        adapter.add("Pune"); //5

        spCity.setAdapter(adapter);

        spCity.setOnItemSelectedListener(this);

        btnRegister.setOnClickListener(this);
        rbMale.setOnClickListener(this);
        rbFemale.setOnClickListener(this);


        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyUser");

        if(updateMode){
            rcvUser = (GWEEUser) rcv.getSerializableExtra("keyUser");

            // set some default values in the user object
            user.setId(rcvUser.getId());
            user.setCity(rcvUser.getCity());
            user.setGender(rcvUser.getGender());

            eTxtName.setText(rcvUser.getName());
            eTxtEmail.setText(rcvUser.getEmail());
            eTxtPassword.setText(rcvUser.getPassword());

            if(rcvUser.getGender().equals("Male")){
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
            }else{
                rbMale.setChecked(false);
                rbFemale.setChecked(true);
            }

            for(int i=0;i<adapter.getCount();i++){
                if(adapter.getItem(i).equals(rcvUser.getCity())){
                    spCity.setSelection(i);
                    break;
                }
            }

            btnRegister.setText("Update User");
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.buttonRegister:

                token = FirebaseInstanceId.getInstance().getToken();

                user.setName(eTxtName.getText().toString().trim());
                user.setEmail(eTxtEmail.getText().toString().trim());
                user.setPassword(eTxtPassword.getText().toString().trim());

                if(validateFields())
                    registerUser();

                break;

            case R.id.radioButtonMale:

                user.setGender("Male");

                break;

            case R.id.radioButtonFemale:

                user.setGender("Female");

                break;
        }

    }

    void registerUser(){

        String url = "";
        if(updateMode)
            url = UPDATE_URL;
        else
            url = REGISTER_URL;

        dialog.show();



        request = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message+" - "+success,Toast.LENGTH_LONG).show();

                        if(updateMode) {

                            Intent data = new Intent();
                            data.putExtra("updatedUser",user);
                            setResult(201,data);
                            finish();

                        }else {
                            clearFields();
                        }

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Some Exception: "+e,Toast.LENGTH_LONG).show();
                    }

                    dialog.dismiss();

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Some Error: "+error,Toast.LENGTH_LONG).show();
                    dialog.dismiss();

                }
            }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();

                if(updateMode){
                    map.put("id",String.valueOf(rcvUser.getId()));
                }

                map.put("name",user.getName());
                map.put("email",user.getEmail());
                map.put("password",user.getPassword());
                map.put("gender",user.getGender());
                map.put("city",user.getCity());
                map.put("token",token);

                return map;
            }
        }
        ;

        requestQueue.add(request); // Send the Request to Server
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //String city = adapter.getItem(i);
        user.setCity(adapter.getItem(i));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void clearFields(){
        eTxtName.setText("");
        eTxtEmail.setText("");
        eTxtPassword.setText("");

        rbFemale.setChecked(false);
        rbMale.setChecked(false);

        spCity.setSelection(0);
    }

    boolean validateFields(){

        boolean flag = true;

        if(user.getName().isEmpty()){
            eTxtName.setError("Please Enter Name");
            flag = false;
        }

        if(user.getEmail().isEmpty()){
            eTxtEmail.setError("Please Enter Email");
            flag = false;
        }else{
            if(!user.getEmail().contains("@") && !user.getEmail().contains(".")){
                eTxtEmail.setError("Please Enter Valid Email");
                flag = false;
            }
        }

        if(user.getPassword().isEmpty()){
            eTxtPassword.setError("Please Enter Password");
            flag = false;
        }else{
            if((user.getPassword().length()<6)) {
                eTxtPassword.setError("Please Enter Password with 6 chars");
                flag = false;
            }
        }

        if(user.getGender().isEmpty()){
            Toast.makeText(this,"Please select your gender",Toast.LENGTH_LONG).show();
            flag = false;
        }

        if(user.getCity().isEmpty()){
            Toast.makeText(this,"Please select your city",Toast.LENGTH_LONG).show();
            flag = false;
        }


        return flag;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_register,menu); // IOC

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.allUsers){
            Intent intent = new Intent(RegisterUserActivity.this, AllUsersActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
