package com.myemcu.gradeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GradeView gv_star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gv_star = (GradeView) findViewById(R.id.gv_star);
        Toast.makeText(MainActivity.this,"当前有："+gv_star.getScorces()+"颗星",Toast.LENGTH_SHORT).show();
    }

    /*@Override
    public void onStarClick(int value) {
        value= gv_star.getScorces();
        Toast.makeText(MainActivity.this,"当前有："+value+"颗星",Toast.LENGTH_SHORT).show();
    }*/
}
