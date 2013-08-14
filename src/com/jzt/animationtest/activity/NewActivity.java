package com.jzt.animationtest.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.jzt.animationtest.R;
import com.jzt.animationtest.async.AnimationUtils;

/**
 * @author Jon Tucker
 *
 */

public class NewActivity extends Activity {
  
  private ViewGroup mLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    AnimationUtils.transitionToContentView(this, R.layout.activity_new);
    
    if (savedInstanceState == null) {
      LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      mLayout = (ViewGroup) inflater.inflate(R.layout.activity_new, null, false);
      ViewTreeObserver observer = mLayout.getViewTreeObserver();
      observer.addOnPreDrawListener(new OnPreDrawListener() {
        
        @Override
        public boolean onPreDraw() {
          System.out.println("in onPreDraw().");
          mLayout.getViewTreeObserver().removeOnPreDrawListener(this);
          AnimationUtils.invisify(NewActivity.this, mLayout);
          return true;  // chet haase told me so
        }
      });
      
      setContentView(mLayout);
    }
  }
  
   // use a ViewTreeObserver to know when everything is laid out and get a bitmap.
  // if that doesn't work, try to use onWindowFocusChanged()
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, 0);
  }

}
