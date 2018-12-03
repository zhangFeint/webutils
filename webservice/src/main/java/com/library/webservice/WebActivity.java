package com.library.webservice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;



public class WebActivity extends AppCompatActivity {

    private String murl;
    private WebView webview;
    private ProgressBar progressbar;

    public static void show(Context act, String url) {
        Intent intent = new Intent(act, WebActivity.class);
        intent.putExtra("url", url);
        act.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        init();//实例化控件

    }

    public void setUrl(String murl) {
        this.murl = murl;
    }

    /**
     * 实例化控件
     */
    private void init() {
        webview = findViewById(R.id.webview);
        progressbar = findViewById(R.id.progressbar);
        Intent intent1 = getIntent();
        murl = intent1.getStringExtra("url");
        WebviewUtil webviewUtils = new WebviewUtil(WebActivity.this, webview);
        webviewUtils.setConfig();
        webviewUtils.WebChromeClient(new MyWebChromeClient(WebActivity.this, progressbar));
        webviewUtils.setWebViewClient(new MyWebViewClient(WebActivity.this, murl));
        webviewUtils.setDownloadListener(new MyWebViewDownLoadListener(WebActivity.this,true));
//        webviewUtils.addJavascriptInterface(new JavaScriptinterface(this));
        webviewUtils.startloadUrl(webview, murl);

    }

    /**
     * webview返回数据处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DialogUtils.REQUEST_CODE_CAMERA:
                DialogUtils.getInstance().takePictureResult(resultCode, DialogUtils.getInstance().getCameraFile());
                break;
            case DialogUtils.REQUEST_CODE_PHOTOS:
                DialogUtils.getInstance().takePhotoResult(this, resultCode, data);
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /***
     * 防止WebView加载内存泄漏
     */
    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.setVisibility(View.GONE);
            webview.removeAllViews();
            webview.destroy();
        }
        super.onDestroy();
    }

    /**
     * 实现按下源生返回键，返回到上一个网页的方法，直接复制即可，
     * 此方法为监听返回按键时的处理
     *
     * @content loadUrl进的网址
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && webview.canGoBack()) { //监听到返回键被按下，并且当前网页可被返回
            if (!webview.canGoBack()) { //获取当前的网址，与初始网址界面是否相同
                finish(); //相同表示为第一次进入的网址，上一级为源生
            } else {
                webview.goBack();                   //返回到网页的上一级
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}