package com.google.cloud.android.speech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
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
import com.google.cloud.android.speech.data.SessionHandler;
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
    private EditText itemQuantity;
    private int itemQuantityValue;
    private  Product product;
    private  TextView itemTotal;
    private SessionHandler session;
    RequestOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        String item_code   = String.valueOf(getIntent().getExtras().getInt("item_code"));
        item_title = (TextView)findViewById(R.id.item_title);
        item_price = (TextView)findViewById(R.id.item_price);
        item_description = (TextView)findViewById(R.id.item_description);
        item_image = (ImageView)findViewById(R.id.item_image);
        itemQuantity = (EditText)findViewById(R.id.itemQuantity);
        itemTotal = (TextView)findViewById(R.id.total);
        itemQuantityValue=0;
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_logo_icon).error(R.mipmap.ic_launcher_logo_icon);
        requestProducts(item_code);


    }

    public void requestProducts(String item_code){
        System.out.println("--[ PRODUCT REQUEST PRODUCT DETAIL ]--");
        requestProduct = new JsonObjectRequest(Request.Method.GET, constants.SERVICE_PRODUCT_DETAIL + item_code, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(obj);
                    product = new Product(
                            obj.getInt("id"),
                            obj.getString("name"),
                            Float.parseFloat(obj.getString("price")),
                            obj.getInt("ranking"),
                            obj.getString("description"),
                            obj.getString("url_img"),
                            "",
                            obj.getInt("discount"),
                            obj.getInt("stock")
                    );
                    item_title.setText(product.getName());
                    item_price.setText("S/. " + String.valueOf(product.currentPrice())+" por " + product.getName());
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

    private  float round(float val)
    {
        return (float)(Math.round(val*100.0)/100.0);
    }



    public void increaseQuantity(View view)
    {
        itemQuantityValue = itemQuantityValue +1;
        itemQuantityValue = itemQuantityValue>=product.getStock()?product.getStock():itemQuantityValue;
        itemQuantity.setText(String.valueOf(itemQuantityValue));
        itemTotal.setText("Total: "+String.valueOf(round(itemQuantityValue*product.currentPrice())));

    }


    public  void reduceQuantity(View view)
    {
        itemQuantityValue = itemQuantityValue -1;
        itemQuantityValue = itemQuantityValue<=0?0:itemQuantityValue;
        itemQuantity.setText(String.valueOf(itemQuantityValue));
        itemTotal.setText("Total: "+String.valueOf(round(itemQuantityValue*product.currentPrice())));

    }

    public   void addToCart(View view)
    {
        if(itemQuantityValue==0){
            Toast.makeText(getApplicationContext(),"La cantidad mínica es uno",Toast.LENGTH_LONG).show();
            return;
        }

        session = new SessionHandler(getApplicationContext());
        User user = session.getUserDetails();
        System.out.println("--[ PRODUCT REQUEST PRODUCT ADD CART ]--");
        JSONObject request = new JSONObject();
        try {
            //Populate the request parameters
            request.put("user_id",user.getId());
            request.put("product_id",product.getID());
            request.put("quantity",itemQuantityValue);
            request.put("price",product.getPrice());
            request.put("discount",product.getDiscount());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestProduct = new JsonObjectRequest(Request.Method.POST, constants.SERVICE_ADD_ITEM_CART, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject obj) {
                try {
                    System.out.println(obj);
                    if(obj.getInt("message")==200) Toast.makeText(getApplicationContext(),"Se ha agregado el producto al carrito!",Toast.LENGTH_LONG).show();
                    if(obj.getInt("message")==409)Toast.makeText(getApplicationContext(),"No se ha podido agregar el producto. Inténtelo luego!",Toast.LENGTH_LONG).show();
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
        System.out.println("[ PRODUCT REQUEST PRODUCT ADD CART END ]");
    }

}
