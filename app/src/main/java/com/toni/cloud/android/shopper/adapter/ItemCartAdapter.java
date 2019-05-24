package com.toni.cloud.android.shopper.adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.cloud.android.shopper.R;
import com.toni.cloud.android.shopper.entities.ItemCart;
import com.bumptech.glide.request.RequestOptions;
import com.toni.cloud.android.shopper.utils.MySingleton;
import com.toni.cloud.android.shopper.utils.constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ItemCartAdapter extends  RecyclerView.Adapter<ItemCartAdapter.ItemCartViewHolder>{
    private  Context context;
    private List<ItemCart> data;
    RequestOptions options;
    public ItemCartAdapter(Context context, List<ItemCart> data) {
        this.context = context;
        this.data=data;
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_logo_icon).error(R.mipmap.clothes);
    }

    @NonNull
    @Override
    public ItemCartAdapter.ItemCartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater  = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_cart,viewGroup,false) ;
        final ItemCartAdapter.ItemCartViewHolder viewHolder = new ItemCartAdapter.ItemCartViewHolder(view) ;
        //remove_item
        viewHolder._item_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /* remove */
                requestItems(data.get(viewHolder.getAdapterPosition()).getId(),viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    public void requestItems(final int item_id, final int pos){

        String item = String.valueOf(item_id);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, constants.SERVICE_DELETE_ITEM_CART + item, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    if(object.getInt("message")==200) {
                        data.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, data.size());
                        Toast.makeText(context,"Se ha eliminado el elemento",Toast.LENGTH_LONG).show();
                    }
                    if(object.getInt("message")==400) Toast.makeText(context,"Ha ocurrido un problema. Inténtalo de nuevo",Toast.LENGTH_LONG).show();
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(context,"Ha ocurrido un problema. Inténtalo de nuevo",Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(context,"Ha ocurrido un problema. Inténtalo de nuevo",Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemCartAdapter.ItemCartViewHolder itemCartViewHolder, int i) {
        itemCartViewHolder._item_title.setText(data.get(i).getProduct_name());
        itemCartViewHolder._item_quantity.setText(String.valueOf(data.get(i).getQuantity()));
        itemCartViewHolder._item_current_price.setText(String.valueOf(data.get(i).currentPrice()));
        itemCartViewHolder._item_sub_total.setText(String.valueOf(data.get(i).currentPrice()*data.get(i).getQuantity()));
        Glide.with(context).load(data.get(i).getUrl_img()).apply(options).into(itemCartViewHolder._item_img);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ItemCartViewHolder extends RecyclerView.ViewHolder {

        TextView _item_title;
        TextView _item_current_price;
        TextView _item_quantity;
        TextView _item_sub_total;
        ImageView _item_img;
        ImageView _item_remove;
        public ItemCartViewHolder(View itemView) {
            super(itemView);
            _item_title = (TextView)itemView.findViewById(R.id.item_title);
            _item_current_price = (TextView)itemView.findViewById(R.id.item_current_price);
            _item_quantity =(TextView)itemView.findViewById(R.id.item_quantity);
            _item_sub_total = (TextView)itemView.findViewById(R.id.item_sub_total);
            _item_img =         (ImageView)itemView.findViewById(R.id.item_img);
            _item_remove  = (ImageView)itemView.findViewById(R.id.remove_item);
        }
    }
}


