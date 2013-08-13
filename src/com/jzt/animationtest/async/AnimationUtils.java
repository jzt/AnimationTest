package com.jzt.animationtest.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class AnimationUtils {

  public static void transition(final View view, final Class<?> activity) {

    new AsyncTask<View, Void, Bitmap>() {
      @Override
      protected Bitmap doInBackground(View... params) {
        View v = params[0];
        v = params[0];
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
          bitmap.compress(Bitmap.CompressFormat.PNG, 25, out);
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

    new AsyncTask<Void, Void, Bitmap[]>() {

      @Override
      protected Bitmap[] doInBackground(Void... params) {
        Bitmap bitmap = null;
        File mCacheDir = activity.getCacheDir();
        File mBmp = new File(mCacheDir, "bmp");
        try {
          FileInputStream mInput = new FileInputStream(mBmp);
          bitmap = BitmapFactory.decodeStream(mInput);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }

        Random random = new Random(System.currentTimeMillis());
        float leftX = 0;
        float rightX = bitmap.getWidth();
        float leftY = random.nextFloat() * bitmap.getHeight();
        float rightY = random.nextFloat() * bitmap.getHeight();

        int minY = (int) Math.min(leftY, rightY);
        int maxY = (int) Math.max(leftY, rightY);

        Bitmap[] results = new Bitmap[2];
        results[0] =
            Bitmap.createBitmap(bitmap, (int) leftX, 0, (int) rightX, maxY).copy(
                Bitmap.Config.ARGB_8888, true);
        results[1] =
            Bitmap.createBitmap(bitmap, (int) leftX, minY, (int) rightX, bitmap.getHeight() - minY)
                .copy(Bitmap.Config.ARGB_8888, true);
        bitmap.recycle();
        
        Path topPath = new Path();
        Path bottomPath = new Path();

        if (leftY < rightY) {
          topPath.moveTo(leftX, leftY);
          topPath.lineTo(leftX, rightY);
          topPath.lineTo(rightX, rightY);
          topPath.lineTo(leftX, leftY);

          bottomPath.moveTo(0, 0);
          bottomPath.lineTo(rightX, rightY-leftY);
          bottomPath.lineTo(rightX, 0);
          bottomPath.lineTo(0, 0);
        } else {
          topPath.moveTo(leftX, leftY);
          topPath.lineTo(rightX, rightY);
          topPath.lineTo(rightX, leftY);
          topPath.lineTo(leftX, leftY);

          bottomPath.moveTo(0,  0);
          bottomPath.lineTo(0, leftY - rightY);
          bottomPath.lineTo(rightX, 0);
          bottomPath.lineTo(0, 0);
        }
        
        topPath.close();
        bottomPath.close();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        paint.setShader(new BitmapShader(results[0], Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        Bitmap topDst = Bitmap.createBitmap(results[0].getWidth(), results[0].getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bottomDst = Bitmap.createBitmap(results[1].getWidth(), results[1].getHeight(), Bitmap.Config.ARGB_8888);

        Canvas topCanvas = new Canvas(topDst);
        Canvas bottomCanvas = new Canvas(bottomDst);
        topCanvas.drawBitmap(results[0], 0, 0, new Paint());
        bottomCanvas.drawBitmap(results[1], 0, 0, new Paint());
        topCanvas.drawPath(topPath, paint);
        bottomCanvas.drawPath(bottomPath, paint);
        results[0] = topDst;
        results[1] = bottomDst;

        return results;
      }

      @Override
      protected void onPostExecute(Bitmap[] results) {
        System.out.println("in onPostExecute()");
        
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout rl = (RelativeLayout) inflater.inflate(id, null, false);
        RelativeLayout.LayoutParams lp =
            new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ImageView top = new ImageView(activity);
        top.setImageBitmap(results[0]);
        top.setLayoutParams(lp);
        rl.addView(top);
        
        lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ImageView bottom = new ImageView(activity);
        bottom.setImageBitmap(results[1]);
        bottom.setLayoutParams(lp);
        rl.addView(bottom);
        activity.setContentView(rl);
        
        // kick off the animation
        animate(rl, top, bottom);
      }

    }.execute();
  }
  
  private static void animate(final ViewGroup container, final View top, final View bottom) {
    final TranslateAnimation topAnimation =
        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f);
    final TranslateAnimation bottomAnimation =
        new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
    
    AnimationListener listener = new AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {}
      @Override
      public void onAnimationRepeat(Animation animation) {}
      @Override
      public void onAnimationEnd(Animation animation) {
        if (animation.equals(topAnimation)) {
          System.out.println("Top animation complete.");
          top.setVisibility(View.GONE);
          container.removeView(top);
        }
        if (animation.equals(bottomAnimation)) {
          System.out.println("Bottom animation complete.");
          bottom.setVisibility(View.GONE);
          container.removeView(bottom);
        }
      }
    };
    
    topAnimation.setAnimationListener(listener);
    topAnimation.setDuration(400);
    topAnimation.setInterpolator(new AccelerateInterpolator());
    topAnimation.setFillAfter(true);

    bottomAnimation.setAnimationListener(listener);
    bottomAnimation.setDuration(400);
    bottomAnimation.setInterpolator(new AccelerateInterpolator());

    top.startAnimation(topAnimation);
    bottom.startAnimation(bottomAnimation);
  }
}
