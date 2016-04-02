package com.picsarttraining.simplegallery;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Arsen on 01.04.2016.
 */
public class Utils {

    public static Bitmap decodeUri(Context context, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, o2);
    }

    public static void loadImagesFromUris(final Context context, ArrayList<Uri>  imageUris, final int requiredSize, final BitmapsLoadCallback bitmapsLoadCallback) {
        Uri[] uris = new Uri[imageUris.size()];
        uris = imageUris.toArray(uris);
        final Uri[] finalUris = uris;
        new AsyncTask<Void, Bitmap, ArrayList<Bitmap>>() {

            @Override
            protected ArrayList<Bitmap> doInBackground(Void... params) {
                ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                for(Uri uri : finalUris)
                {
                    try {
                        bitmaps.add(decodeUri(context, uri, requiredSize));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return bitmaps;
            }

            @Override
            protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
                bitmapsLoadCallback.onBitmapLoad(bitmaps);
            }
        }.execute();
    }

    public static ArrayList<Uri> getStorageImages(Context context) {
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA};
        Cursor c = null;
        ArrayList<Uri> imageUris = new ArrayList<>();
        SortedSet<String> dirList = new TreeSet<String>();

        String[] directories = null;
        if (u != null) {
            c = context.getContentResolver().query(u, projection, null, null, null);
        }

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();
                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                            ) {


                        String path = imagePath.getAbsolutePath();
                        imageUris.add(Uri.fromFile(new File(path)));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return imageUris;
    }

    public interface BitmapsLoadCallback{
        void onBitmapLoad(ArrayList<Bitmap> bitmaps);
    }
}
