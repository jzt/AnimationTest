package com.jzt.animationtest.activity;

import com.jzt.animationtest.R;
import com.jzt.animationtest.R.id;
import com.jzt.animationtest.R.layout;
import com.jzt.animationtest.async.BitmapUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

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
