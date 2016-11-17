package com.myemcu.ttff_365;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {

    private ImageView iv_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        iv_welcome = (ImageView) findViewById(R.id.iv_welcome);

        // setContentView加载欢迎页后需要停顿几秒到主界面：1 sleep() 2 Handler 3 Timer类 4 使用动画过度

        // 主线程禁止耗时操作，本方法禁用
        /*try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        // 新开一个子线程，本方法可以，单我们用动画，欢迎页的过度一般都用动画
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

        // 属性动画(不能够直接new)(需要动画的图片id，动画效果为透明度，透明度的过度)
        ObjectAnimator animator = ObjectAnimator.ofFloat(iv_welcome,"alpha",0.7f,1.0f);
        animator.setDuration(3000); // 动画时长
        animator.start();           // 开启动画
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) { // 动画完成
                // 动画执行完成，进入主页面
                LoadMain();
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    // 跳转到主页面
    private void LoadMain() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
