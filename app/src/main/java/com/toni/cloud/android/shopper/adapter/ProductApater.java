package com.toni.cloud.android.shopper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.cloud.android.shopper.R;
import com.toni.cloud.android.shopper.entities.Product;
import com.bumptech.glide.request.RequestOptions;
import com.toni.cloud.android.shopper.product_detail_activity;

import java.util.List;

public class ProductApater extends RecyclerView.Adapter<ProductApater.ProductViewHolder> {

    private  Context context;
    private List<Product> data;
    RequestOptions options;

    public ProductApater(Context context, List<Product> data) {
        this.context = context;
        this.data = data;
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher_logo_icon).error(R.mipmap.clothes);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater  = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.product_preview_2,viewGroup,false) ;
        final ProductViewHolder viewHolder = new ProductViewHolder(view) ;
        viewHolder._product_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* ejnviar a ventana diferente */
                Intent i = new Intent(context, product_detail_activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("item_code",data.get(viewHolder.getAdapterPosition()).getID());
                context.startActivity(i);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {
        //float current_price = data.get(i).getPrice() - data.get(i).getPrice()*((float)(data.get(i).getDiscount()/100.0));
        ///System.out.println(String.valueOf(current_price));
        productViewHolder._product_name.setText(data.get(i).getName());
        productViewHolder._product_price.setText("S/. "+String.valueOf(data.get(i).currentPrice()));
        productViewHolder._product_description.setText(data.get(i).getDescription());
        productViewHolder._product_discount.setText("-" +String.valueOf(data.get(i).getDiscount())+"%");
        productViewHolder._product_before_price.setText("Antes S/."+String.valueOf(data.get(i).getPrice()));
        productViewHolder._product_before_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        productViewHolder._product_before_price.setVisibility(((data.get(i).getStock()==0 || data.get(i).getDiscount()==0)?View.INVISIBLE:View.VISIBLE));
        productViewHolder._product_discount.setVisibility(data.get(i).getDiscount()==0?View.INVISIBLE:View.VISIBLE);
        productViewHolder._product_status.setVisibility(data.get(i).getStock()==0?View.VISIBLE:View.INVISIBLE);
        Glide.with(context).load(data.get(i).getUrl_img()).apply(options).into(productViewHolder._product_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        CardView _product_detail;
        ImageView _product_image;
        TextView _product_name;
        //TextView _product_code;
        TextView _product_description;
        //TextView _product_ranking;
        TextView _product_price;

        TextView _product_status;
        TextView _product_discount;
        TextView _product_before_price;
        public ProductViewHolder(View itemView) {
            super(itemView);


            _product_detail = (CardView)itemView.findViewById(R.id.product_preview_2);
            _product_image = (ImageView)itemView.findViewById(R.id.item_image);
            _product_name = (TextView)itemView.findViewById(R.id.item_title);
            _product_price = (TextView)itemView.findViewById(R.id.item_price);
            //_product_code = (TextView)itemView.findViewById(R.id.item_code);
            _product_description = (TextView)itemView.findViewById(R.id.item_description);
            _product_status =  (TextView)itemView.findViewById(R.id.item_status);
            _product_discount = (TextView)itemView.findViewById(R.id.item_discount);
            _product_before_price = (TextView)itemView.findViewById(R.id.item_before_price);
        }
    }
}
