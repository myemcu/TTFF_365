package com.myemcu.ttff_365;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 主页解决方案：ViewPager+Fragment+RadioButton(栗如：BJNews)
        // TabHost+Fragment(过时)
        // ViewGroup+Fragment+动态切换(Button加载Fragment)(不建议，因：不断地销毁和创建)
    }
}
