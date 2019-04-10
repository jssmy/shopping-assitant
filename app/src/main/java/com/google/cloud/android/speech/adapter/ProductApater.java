package com.google.cloud.android.speech.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.cloud.android.speech.R;
import com.google.cloud.android.speech.models.Product;
import com.bumptech.glide.request.RequestOptions;
import com.google.cloud.android.speech.product_detail_activity;

import java.util.List;

public class ProductApater extends RecyclerView.Adapter<ProductApater.ProductViewHolder> {

    private  Context context;
    private List<Product> data;
    RequestOptions options;

    public ProductApater(Context context, List<Product> data) {
        this.context = context;
        this.data = data;
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher);
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
        productViewHolder._product_name.setText(data.get(i).getName());
        productViewHolder._product_code.setText("Producto ID: "+String.valueOf(data.get(i).getID()));
        productViewHolder._product_price.setText("S/. "+String.valueOf(data.get(i).getPrice()));
        ///productViewHolder._product_description.setText(data.get(i).getDescription());
        //productViewHolder._product_ranking.setText(String.valueOf(data.get(i).getRanking()));

        /**holder.tv_name.setText(mData.get(position).getName());
        holder.tv_rating.setText(mData.get(position).getRating());
        holder.tv_studio.setText(mData.get(position).getStudio());
        holder.tv_category.setText(mData.get(position).getCategorie());*/

        // Load Image from the internet and set it into Imageview using Glide

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
        TextView _product_code;
        //TextView _product_description;
        //TextView _product_ranking;
        TextView _product_price;

        public ProductViewHolder(View itemView) {
            super(itemView);


            _product_detail = (CardView)itemView.findViewById(R.id.product_preview_2);
            _product_image = (ImageView)itemView.findViewById(R.id.item_image);
            _product_name = (TextView)itemView.findViewById(R.id.item_title);
            _product_price = (TextView)itemView.findViewById(R.id.item_price);
            _product_code = (TextView)itemView.findViewById(R.id.item_code);



        }
    }
}
