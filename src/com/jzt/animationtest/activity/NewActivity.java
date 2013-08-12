package com.jzt.animationtest.activity;

import android.app.Activity;
import android.os.Bundle;

import com.jzt.animationtest.R;
import com.jzt.animationtest.async.AnimationUtils;

/**
 * @author Jon Tucker
 *
 */

public class NewActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AnimationUtils.transitionToContentView(this, R.layout.activity_new);
  }

}
