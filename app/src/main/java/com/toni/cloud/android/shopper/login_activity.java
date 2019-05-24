package com.toni.cloud.android.shopper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.cloud.android.shopper.R;
import com.toni.cloud.android.shopper.data.SessionHandler;
import com.toni.cloud.android.shopper.entities.User;
import com.toni.cloud.android.shopper.utils.MySingleton;
import com.toni.cloud.android.shopper.utils.constants;

import org.json.JSONException;
import org.json.JSONObject;

public class login_activity extends AppCompatActivity {

    private EditText userText ;
    private EditText passwordText;
    private ProgressDialog pDialog;
    SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        userText = (EditText)findViewById(R.id.etLoginUsername);
        passwordText =(EditText)findViewById(R.id.etLoginPassword);
        session = new SessionHandler(this);

    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dissmisLoader(){
        pDialog.dismiss();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    public  void sing_in(View view){
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put(constants.KEY_EMAIL, userText.getText().toString());
            request.put(constants.KEY_PASSWORD, passwordText.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }




        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, constants.SERVICE_AUTHENTICATION, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //pDialog.dismiss();
                        try {
                            //Check if user got logged in successfully
                            System.out.println("[RESPONSE]: " +  response);
                            if (!response.has("message")) {
                                User user = new User(response.getInt("id"),response.getString("name"), response.getString("email"), response.getString("token"));
                                session.loginUser(user);
                                Intent launcher = new Intent(getApplicationContext(), menu_main_activity.class);
                                startActivity(launcher);
                                finish();

                            } else {


                                Toast.makeText(getApplicationContext(),
                                        response.getString(constants.KEY_MESSAGE), Toast.LENGTH_SHORT).show();

                            }
                            System.out.println("[ POST LOGIN RESPONSE]: "+response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        //Display error message whenever an error occurs
                        error.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);


    }
}
