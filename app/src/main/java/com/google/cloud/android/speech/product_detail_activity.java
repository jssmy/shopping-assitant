package com.google.cloud.android.speech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.cloud.android.speech.models.Product;
import com.google.cloud.android.speech.models.User;
import com.google.cloud.android.speech.utils.MySingleton;
import com.google.cloud.android.speech.utils.constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class product_detail_activity extends AppCompatActivity {

    private JsonObjectRequest requestProduct;
    private TextView  item_title;
    private TextView item_price;
    private TextView item_description;
    private ImageView item_image;
    RequestOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        String item_code   = String.valueOf(getIntent().getExtras().getInt("item_code"));
        item_title = (TextView)findViewById(R.id.item_title);
        item_price = (TextView)findViewById(R.id.item_price);
        item_description = (TextView)findViewById(R.id.item_description);
        item_image = (ImageView)findViewById(R.id.item_image);
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher);
        requestProducts(item_code);



    }

    public void requestProducts(String item_code){
        System.out.println("--[ PRODUCT REQUEST PRODUCT DETAIL ]--");
        requestProduct = new JsonObjectRequest(Request.Method.GET, constants.SERVICE_PRODUCT_DETAIL + item_code, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(obj);
                    Product product = new Product(obj.getString("name"), Float.parseFloat(obj.getString("price")), obj.getInt("ranking"), obj.getString("description"), obj.getString("url_img"));
                    item_title.setText(product.getName());
                    item_price.setText("S/. " + String.valueOf(product.getPrice())+" por " + product.getName());
                    item_description.setText(product.getDescription());
                    Glide.with(getApplicationContext()).load(product.getUrl_img()).apply(options).into(item_image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError eror) {
                eror.printStackTrace();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(requestProduct);
        System.out.println("[ PRODUCT REQUEST PRODUCT DETAIL END ]");


    }

}
