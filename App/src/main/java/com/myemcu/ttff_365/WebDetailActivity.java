package com.myemcu.ttff_365;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;


// 显示网页内容
public class WebDetailActivity extends AppCompatActivity {

    public static final String URL_KEY = "url_key";
    private String url;

    private WebView web_view;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);

        web_view = (WebView) findViewById(R.id.web_view);

        webSettings=web_view.getSettings();             // 获取WebView参数设置
        webSettings.setUseWideViewPort(false);          // 将图片调整到适合WebView的大小
        webSettings.setJavaScriptEnabled(true);         // 支持JS
        webSettings.setLoadsImagesAutomatically(true);  // 支持自动加载图片

        url=getIntent().getStringExtra(URL_KEY);
        web_view.loadUrl(url);// 此链接为json解析到的data中的第一个ad_list中的link
    }
}
