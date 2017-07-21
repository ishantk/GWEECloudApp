package com.auribises.gweecloudapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    GWEEUser user;

    StringRequest request;
    RequestQueue requestQueue;

    String REGISTER_URL = "http://auribises.com/gwee/insert.php";

    ProgressDialog dialog;

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
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id){
            case R.id.buttonRegister:

                user.setName(eTxtName.getText().toString().trim());
                user.setEmail(eTxtEmail.getText().toString().trim());
                user.setPassword(eTxtPassword.getText().toString().trim());

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

        dialog.show();

        request = new StringRequest(Request.Method.POST, REGISTER_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),message+" - "+success,Toast.LENGTH_LONG).show();

                        clearFields();
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

                map.put("name",user.getName());
                map.put("email",user.getEmail());
                map.put("password",user.getPassword());
                map.put("gender",user.getGender());
                map.put("city",user.getCity());

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
}
