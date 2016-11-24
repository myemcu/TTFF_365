package com.myemcu.gradeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

public class GradeView extends View {

    private  int scorces = 5; // 总分

    private int singleBitmapWidth = 0;  // 单个Bitmap宽度

    private Bitmap bigger_grade_fcous,bigger_grade_normal;

    private int grade;  // 成绩
    


    // 套路：3个构造器
    public GradeView(Context context) {
        this(context,null);
    }

    public GradeView(Context context, AttributeSet attrs) {
        this(context, attrs ,0);
    }

    public GradeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        // 获取图片Bitmap（两张）
        bigger_grade_fcous=BitmapFactory.decodeResource(getResources(),R.drawable.bigger_grade_fcous);
        bigger_grade_normal=BitmapFactory.decodeResource(getResources(),R.drawable.bigger_grade_normal);

        singleBitmapWidth=bigger_grade_normal.getWidth();
    }

    @Override   // 测量方法
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        // 1 根据图片计算宽高
        int width = bigger_grade_fcous.getWidth()*scorces+getPaddingLeft()+getPaddingRight();
        int height = bigger_grade_fcous.getHeight()+getPaddingTop()+getPaddingBottom();

        // 2 获取测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if (widthMode==MeasureSpec.EXACTLY) {//AT_MOST:用户设置的是warp_content；EXACTLY:用户设置的是固定值 or match_parent
            int userWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (userWidth<width) {
                throw new IllegalArgumentException("亲，您给的宽度不够");
            }

            width=userWidth;// 显示的宽度为用户设置的宽度
        }

        // 3 设置宽高
        setMeasuredDimension(width,height);
    }

    @Override   // 绘制方法
    protected void onDraw(Canvas canvas) {  // 用来绘制界面
        super.onDraw(canvas);

        // canvas.drawBitmap(bigger_grade_normal,0,0,null);    // 待画图片，左边距，顶边距，画笔

        // int bitmapWidth = bigger_grade_normal.getWidth();       // 图片宽度

        // 画5颗星
        for (int i=0;i<scorces;i++) {
            if (i<grade) {
                canvas.drawBitmap(bigger_grade_fcous,i*singleBitmapWidth,0,null);
            }
            else {
                canvas.drawBitmap(bigger_grade_normal,i*singleBitmapWidth,0,null);
            }

        }
    }

    @Override   // 触摸方法
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                                        onTouchResult(event);
                                        break;

            case MotionEvent.ACTION_MOVE:
                                        onTouchResult(event);
                                        break;
            default:break;
        }

        //return super.onTouchEvent(event);
        return true; // 返回true，让onTouchEvent不断重新执行
    }

    // 触摸结果
    private void onTouchResult(MotionEvent event) {
        // 1 获取x轴方向触摸位置
        int move_x = (int) event.getX();
        // 2 根据位置算成绩
        grade = move_x/singleBitmapWidth+1;
        if (grade <=scorces) {
            Log.e("Grade", grade +"");
        }
        // 3 算分后，重绘界面
        invalidate();

        // 4 显示吐司
        Toast.makeText(getContext(),"当前有："+grade+"颗星",Toast.LENGTH_SHORT).show();
    }

    public int getScorces() {
        return grade;
    }

    /*public interface OnStarClickListener {
        void onStarClick(int value);
    }

    public OnStarClickListener listener;

    public void setOnStarClickListener(OnStarClickListener listener) {
        this.listener=listener;
    }*/
}
