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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
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
        results[0] = Bitmap.createBitmap(bitmap, (int) leftX, 0, (int) rightX, maxY);
        results[1] =
            Bitmap.createBitmap(bitmap, (int) leftX, minY, (int) rightX, bitmap.getHeight() - minY);
//        bitmap.recycle();
        
        System.out.println("minY: " + minY);
        System.out.println("maxY: " + maxY);
        System.out.println("topHeight: " + results[0].getHeight());
        System.out.println("bottomHeight: " + results[1].getHeight());
        System.out.println("bitmap.getHeight() - minY: " + (bitmap.getHeight() - minY));
        
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
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
//        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Canvas topCanvas = new Canvas(results[0]);
        Canvas bottomCanvas = new Canvas(results[1]);
        topCanvas.clipPath(topPath, Region.Op.UNION);
        bottomCanvas.clipPath(bottomPath, Region.Op.DIFFERENCE);
        topCanvas.drawPath(topPath, paint);
        bottomCanvas.drawPath(bottomPath, paint);

//        paint.setColor(Color.GREEN);
//        paint.setStrokeWidth(10f);
//        topCanvas.drawLine(leftX, leftY, rightX, rightY, paint);
//        topCanvas.drawCircle(leftX, maxY, 30, paint);
//        topCanvas.drawCircle(rightX, maxY, 30, paint);
//        paint.setColor(Color.MAGENTA);
//        bottomCanvas.drawLine(leftX, leftY, rightX, rightY, paint);
//        bottomCanvas.drawCircle(leftX, minY, 30, paint);
//        bottomCanvas.drawCircle(rightX, minY, 30, paint);
        

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
    topAnimation.setDuration(10000);
    topAnimation.setInterpolator(new AccelerateInterpolator());

    bottomAnimation.setAnimationListener(listener);
    bottomAnimation.setDuration(10000);
    bottomAnimation.setInterpolator(new AccelerateInterpolator());

    top.startAnimation(topAnimation);
    bottom.startAnimation(bottomAnimation);

  }
}
