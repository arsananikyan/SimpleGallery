package com.picsarttraining.simplegallery;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Uri> imageUris;
    private RecyclerView recyclerView;
    private ProgressDialog progress;
    private RecyclerViewAdapter adapter;
    private LinearLayout animationOverlay;
    private ImageView bigOverlayImage;
    private ImageView bigImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationOverlay = (LinearLayout) findViewById(R.id.animation_overlay);
        bigOverlayImage = (ImageView) findViewById(R.id.big_overlay_image);
        bigImage = (ImageView) findViewById(R.id.big_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new RecyclerViewAdapter(this);
        adapter.setItemClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(View v, Bitmap bitmap, int position) {

                int x = bigImage.getMeasuredWidth();
                int y = bigImage.getMeasuredHeight();
                try {
                    Bitmap imageBitmap = Utils.decodeUri(MainActivity.this, imageUris.get(position), Math.min(x, y));
                    bigImage.setImageBitmap(imageBitmap);
                    bigOverlayImage.setImageBitmap(imageBitmap);
                    startImageAnimation(v);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        imageUris = Utils.getStorageImages(this);
        progress = ProgressDialog.show(this, "Loading",
                "", true);
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
        if (!viewTreeObserver.isAlive())
            return;
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Utils.loadImagesFromUris(MainActivity.this, imageUris, recyclerView.getMeasuredHeight(), new Utils.BitmapsLoadCallback() {
                    @Override
                    public void onBitmapLoad(ArrayList<Bitmap> bitmaps) {
                        progress.dismiss();
                        adapter.setBitmaps(bitmaps);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void startImageAnimation(View v) {
        animationOverlay.setVisibility(View.VISIBLE);
        bigImage.setVisibility(View.INVISIBLE);

        int centerX = v.getLeft() + v.getMeasuredWidth() / 2;
        int centerY = recyclerView.getTop() + v.getMeasuredHeight() / 2;
        int destCenterX = bigOverlayImage.getLeft() + bigOverlayImage.getMeasuredWidth() / 2;
        int destCenterY = bigOverlayImage.getTop() + bigOverlayImage.getMeasuredHeight() / 2;
        float scaleX = (float) v.getMeasuredWidth() / bigOverlayImage.getMeasuredWidth();
        float scaleY = (float) v.getMeasuredHeight() / bigOverlayImage.getMeasuredHeight();

        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX, 1, scaleY, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        TranslateAnimation translateAnimation = new TranslateAnimation(centerX - destCenterX, 0, centerY - destCenterY, 0);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(300);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bigImage.setVisibility(View.VISIBLE);
                animationOverlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        bigOverlayImage.startAnimation(animationSet);
    }
}
