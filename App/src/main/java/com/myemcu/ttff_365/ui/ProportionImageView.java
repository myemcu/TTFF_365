package com.myemcu.ttff_365.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.myemcu.ttff_365.R;

/**
 * Created by Administrator on 2016/11/22 0022.
 */

// 比例ImageView(自定义View)，用于屏幕适配
public class ProportionImageView extends ImageView{

    private float widthProportion;      // 宽度比例
    private float heightProportion;     // 高度比例

    // 直接在java里面new的时候调用
    public ProportionImageView(Context context) {
        this(context,null); // 调下面的构造器
    }

    // 在xml里面声明的时候调用
    public ProportionImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0); // 调下面的构造器
    }

    // 在xml中的自定义Style的情况调用
    public ProportionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化属性
        initAttribute(context,attrs);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        // 属性数组
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ProportionImageView);
        // 获取单个属性
        widthProportion = array.getFloat(R.styleable.ProportionImageView_proportion_width,0);
        heightProportion = array.getFloat(R.styleable.ProportionImageView_proportion_height,0);
    }

    @Override   // 测量方法(用来测量和显示View的大小)
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 将HomeFragment中的ImagView(iv_top_photo)改变高度显示

        // 获取ImagView的实际宽
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // 比例计算ImagView的高(实际宽 * 比例高/比例宽)
        int height = (int) (width*heightProportion/widthProportion);

        // 设置View的宽高
        setMeasuredDimension(width,height);
    }
}
