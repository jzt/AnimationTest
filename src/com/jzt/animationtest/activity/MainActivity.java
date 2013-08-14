package com.jzt.animationtest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jzt.animationtest.R;
import com.jzt.animationtest.async.BitmapUtils;

public class MainActivity extends Activity implements OnClickListener {
  
  private View mView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.main_container).setOnClickListener(this);
  }
  
  @Override
  public void onClick(View v) {
    BitmapUtils.transition(v, NewActivity.class);
  }
}
