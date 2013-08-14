package com.jzt.animationtest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jzt.animationtest.R;

public class MainActivity extends Activity implements OnClickListener {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.main_container).setOnClickListener(this);
  }
  
  @Override
  public void onClick(View v) {
//    AnimationUtils.transition(v, NewActivity.class);
    Intent intent = new Intent(this, NewActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
  }
}
