package com.myemcu.ttff_365.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

/*自定义Button，用于验证码发送时*/
public class TimerButton extends Button{

    private int currentCnt = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (currentCnt>0) {
                // 不断地更新倒计时
                currentCnt--;
                cntDown(currentCnt);
            }else {
                enStatus();
            }
        }
    };

    public TimerButton(Context context) {
        this(context,null);
    }

    public TimerButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    // Button使能状态
    public void enStatus() {
        setEnabled(true);
        setText("获取验证码");
        //setBackgroundColor(Color.GRAY);
    }

    /*状态(能够点击 不能点击的稍后 倒计时)*/

    // 稍后状态
    public void laterOnStatus(int color) {
        this.setEnabled(false);     // Button失效
        this.setText("请稍后...");   // 当前显示
        this.setTextColor(color);   // 设置颜色
    }

    // 倒计时状态
    public void cntDown(int cnt) {

        currentCnt=cnt;

        this.setEnabled(false);     // Button失效
        this.setText(cnt+"秒后重获");

        // 不断地取更新
        handler.sendEmptyMessageDelayed(0,1000);
    }
}
