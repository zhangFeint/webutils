package com.library.webservice;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;


/**
 * 作用：辅助 WebView 处理 Javascript 的对话框,网站图标,网站标题等等。
 */
public class MyWebChromeClient extends WebChromeClient {
    private Activity activity;
    private ProgressBar progressBar;

    private String errorPath = "file:/android_asset/Networkoutage/webview404.html";


    public MyWebChromeClient(Activity activity) {
        this.activity = activity;
    }

    public MyWebChromeClient(Activity activity, ProgressBar progressBar) {
        this.activity = activity;
        this.progressBar = progressBar;
    }

    public void setErrorPath(String errorPath) {
        this.errorPath = errorPath;
    }

    /**
     * 作用：获取Web页中的标题
     * android 6.0 以下通过title获取
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                view.loadUrl("about:blank");    // 避免出现默认的错误界面
                view.loadUrl(errorPath);
            }
        }
    }

    /**
     * 作用：获得网页的加载进度并显示
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (progressBar != null) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                progressBar.setProgress(newProgress);//设置进度值
            }
        }
        super.onProgressChanged(view, newProgress);
    }

    //配置权限（同样在WebChromeClient中实现）
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    /**
     * 上传文件问题，需要下方方法
     */

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        isFile(valueCallback);
    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        isFile(filePathCallback);
        return true;
    }

    private void isFile(ValueCallback valueCallback) {
//        DialogUtils.getInstance().showWebCameraDialog(activity, valueCallback);
    }

    /**
     * web弹出框不出问题  分为4种弹框
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        showDialog(view, url, message, result);
        return true;
    }

    /**
     * 设置响应js 的Confirm()函数
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        showDialog(view, url, message, result);
        return true;
    }

    /**
     * 设置响应js 的Prompt()函数
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        showDialog(view, url, message, result);
        return true;
    }

    public void showDialog(WebView view, String url, String message, final JsResult result) {
        AlertDialog.Builder b = new AlertDialog.Builder(activity);
        b.setMessage(message);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        b.setCancelable(false);
        b.create().show();
    }


}