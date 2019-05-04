package com.google.cloud.android.speech.utils;

import android.content.Context;
import android.icu.lang.UScript;

import com.android.volley.AuthFailureError;
import com.google.cloud.android.speech.utils.constants;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.cloud.android.speech.utils.MySingleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RequestService {
    private JSONObject data;
    private String URL;
    private JSONObject responseService= new JSONObject();
    private Map Header;
    private Context context;

    public JSONObject getResponseService() {
        return responseService;
    }

    public void setResponseService(JSONObject responseService) {
        this.responseService = responseService;
    }

    public RequestService(Context context) {
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public JSONObject  POST(String url, JSONObject object, final HashMap<String,String> header)
    {
        URL= url;
        data = object;
        Header =header;
        JsonObjectRequest    requestPOST = new JsonObjectRequest
        (
                Request.Method.POST,
                URL,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                        public void onResponse(JSONObject response) {
                                 responseService = response;
                                System.out.println("[ POST METHOD REQUEST ] + "+ responseService);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                responseService.put("message_error",error.getMessage());
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
        );

        System.out.println("[ POST METHOD REQUEST ] + "+ responseService);
        MySingleton.getInstance(context).addToRequestQueue(requestPOST);
        return  responseService;
    }

}
