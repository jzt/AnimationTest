package com.jzt.animationtest.activity;

import android.app.Activity;
import android.os.Bundle;

import com.jzt.animationtest.R;
import com.jzt.animationtest.async.BitmapUtils;

/**
 * @author Jon Tucker
 *
 */

public class NewActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BitmapUtils.transitionToContentView(this, R.layout.activity_new);
  }

}
