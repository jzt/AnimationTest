package com.jzt.animationtest.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * @author Jon Tucker
 * 
 */

public class BitmapUtils {

  public static void transition(final View view, final Class<?> activity) {

    new AsyncTask<View, Void, Bitmap>() {
      @Override
      protected Bitmap doInBackground(View... params) {
        View v = params[0];
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return bitmap;
      }
      
      protected void onPostExecute(Bitmap result) {
        cacheBitmapAndStartActivity(view.getContext(), result, activity);
      }
    }.execute(view);

  }

  public static void cacheBitmapAndStartActivity(final Context context, Bitmap bitmap,
      final Class<?> activity) {
    System.out.println("in cacheBitmap()");
    new AsyncTask<Bitmap, Void, Void>() {
      @Override
      protected Void doInBackground(Bitmap... params) {
        Bitmap bitmap = params[0];
        File cacheDir = context.getCacheDir();
        File f = new File(cacheDir, "bmp");
        try {
          FileOutputStream out = new FileOutputStream(f);
          bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
          out.flush();
          out.close();
          System.out.println("Cached bitmap.");
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        bitmap.recycle();
        return null;
      }

      @Override
      protected void onPostExecute(Void result) {
        System.out.println("Launching activity");
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
      }
    }.execute(bitmap);
  }

  public static void transitionToContentView(final Activity activity, final int id) {

    System.out.println("in getCachedBitmap()");

    new AsyncTask<Void, Void, Bitmap>() {

      @Override
      protected Bitmap doInBackground(Void... params) {
        Bitmap result = null;
        File mCacheDir = activity.getCacheDir();
        File mBmp = new File(mCacheDir, "bmp");
        try {
          FileInputStream mInput = new FileInputStream(mBmp);
          result = BitmapFactory.decodeStream(mInput);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        return result;
      }

      @Override
      protected void onPostExecute(Bitmap result) {
        System.out.println("in onPostExecute()");
        LayoutInflater inflater =
            (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        ViewGroup layout = (ViewGroup) inflater.inflate(id, null, false);
        ImageView mOverlay = new ImageView(activity);
        mOverlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        mOverlay.setImageBitmap(result);
        layout.addView(mOverlay);
        activity.setContentView(layout);
      }
    }.execute();
  }
}

