package com.picsarttraining.simplegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Arsen on 02.04.2016.
 */
public class RecyclerViewAdapter extends
        RecyclerView.Adapter<RecyclerViewHolder> {// Recyclerview will extend to

    // recyclerview adapter
    private ArrayList<Bitmap> bitmaps;
    private LayoutInflater inflater;

    private ItemClickListener itemClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<Bitmap> bitmaps) {
        this.inflater = LayoutInflater.from(context);
        this.bitmaps = new ArrayList<>(bitmaps);
    }

    public RecyclerViewAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.bitmaps = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return (null != bitmaps ? bitmaps.size() : 0);

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        final Bitmap bitmap = bitmaps.get(position);
        holder.imageview.setImageBitmap(bitmap);
        holder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener==null)
                    return;
                itemClickListener.onClick(v, bitmap,position);
            }
        });
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View convertView = inflater.inflate(
                R.layout.item_image, viewGroup, false);
        RecyclerViewHolder listHolder = new RecyclerViewHolder(convertView);
        return listHolder;
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps.clear();
        this.bitmaps = new ArrayList<>(bitmaps);
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onClick(View v, Bitmap bitmap, int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}