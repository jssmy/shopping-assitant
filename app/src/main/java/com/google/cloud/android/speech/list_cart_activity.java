package com.google.cloud.android.speech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.cloud.android.speech.adapter.ItemCartAdapter;
import com.google.cloud.android.speech.adapter.ProductApater;
import com.google.cloud.android.speech.data.SessionHandler;
import com.google.cloud.android.speech.models.ItemCart;
import com.google.cloud.android.speech.models.Product;
import com.google.cloud.android.speech.models.User;
import com.google.cloud.android.speech.utils.MySingleton;
import com.google.cloud.android.speech.utils.constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class list_cart_activity extends AppCompatActivity {


    private RecyclerView recyclerViewItemCart;
    private List<ItemCart> items;
    private SessionHandler session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_cart_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerViewItemCart = (RecyclerView)findViewById(R.id.item_cart_list);
        items = new ArrayList<>();
        requestItems();
    }

    public void requestItems(){

        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        JsonArrayRequest request = new JsonArrayRequest(
                constants.SERVICE_LIST_ITEM_CART+user.getId(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSONObject obj;

                        try{
                            for (int i=0; i<response.length(); i++){

                                obj = response.getJSONObject(i);
                                ItemCart item = new ItemCart(obj.getInt("id"),obj.getString("product_name"), obj.getInt("quantity"),Float.parseFloat(obj.getString("price")),obj.getInt("discount"),true, obj.getString("url_img"));
                                items.add(item);
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                        itemAdapterSettings(items);
                        items = null;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void itemAdapterSettings(List<ItemCart> itemCarts){
        ItemCartAdapter itemCartAdapter = new ItemCartAdapter(getApplicationContext(),itemCarts);
        recyclerViewItemCart.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewItemCart.setAdapter(itemCartAdapter);

    }

}
