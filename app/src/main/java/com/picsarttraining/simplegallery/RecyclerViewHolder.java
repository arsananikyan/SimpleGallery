package com.picsarttraining.simplegallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Arsen on 02.04.2016.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageview;


    public RecyclerViewHolder(View convertView) {
        super(convertView);
        this.imageview = (ImageView) convertView.findViewById(R.id.item_image_view);
    }


}