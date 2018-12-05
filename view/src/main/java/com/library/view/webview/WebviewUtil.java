package com.library.view.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017\11\23 0023.
 */

public class WebviewUtil {
    private WebView webview;
    private Activity activity;

    public WebviewUtil(Activity activity, WebView webview) {
        this.webview = webview;
        this.activity = activity;
    }


    public void setConfig() {
        WebSettings webSettings = webview.getSettings();
        webSettings.setDatabaseEnabled(true);//进度条加载完毕时
        webSettings.setJavaScriptEnabled(true); // 设置与Js交互的权限
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setAppCacheEnabled(true);
        String dir = activity.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setGeolocationDatabasePath(dir);//设置定位的数据库路径
        webSettings.setDomStorageEnabled(true);//开启DomStorage缓存
        webSettings.setGeolocationEnabled(true);   //启用地理定位
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片

        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setGeolocationDatabasePath("http://www.cvbaoli.com/webak/public/showAgreement");
        webSettings.setDomStorageEnabled(true);
        /**
         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (isNetworkAvailable(activity)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //有网络连接，设置默认缓存模式
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//无网络连接，设置本地缓存模式
        }
    }


    public void setWebViewClient(WebViewClient webViewClient) {
        webview.setWebViewClient(webViewClient);//解决选择浏览器的问题,拦截url
    }

    public void WebChromeClient(WebChromeClient webChromeClient) {
        webview.setWebChromeClient(webChromeClient);  //网页对话框显示问题,上传文件问题解决
    }

    @SuppressLint("JavaScriptinterface")
    public void addJavascriptInterface(Object object) {
        webview.addJavascriptInterface(object, "appandroid"); //h5调用源生方法的类
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        webview.setDownloadListener(downloadListener);  // \(^o^)/~下载文件
    }

    /**
     * webview访问信息
     */
    public void startloadUrl(WebView webview, String murl) {
        webview.loadData("", "text/html", "UTF-8");  //解决部分手机调用不到js的
        Map extraHeaders = new HashMap(); //解决微信支付少参数问题
        extraHeaders.put("Referer", "http://apprzs.ngrok.xiaomiqiu.cn");
        webview.loadUrl(murl, extraHeaders);//加载页面
    }


    /**
     * @param context
     * @param url     synCookies(murl,"user_id");
     *                synCookies(activity,"http://www.htjjsc.com");
     */
    public void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, String.format("%s=%s", "user_id", "78"));
//        cookieManager.setCookie(url, String.format("%s=%s", "xx", "78"));
//        cookieManager.setCookie(url, String.format("%s=%s", "xxx", userEntry.getPortCode()));
        String cookie = cookieManager.getCookie(url);
        Log.e("cookie:——————", cookie);
        CookieSyncManager.getInstance().sync();
    }

    public static boolean synCookies(Context context, String url, String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        String oldcookie = cookieManager.getCookie(url);
        Log.e("oldcookie:——————", oldcookie);
        cookieManager.setCookie(url, String.format("%s=%s", "user_id", "74"));
        String newCookie = cookieManager.getCookie(url);
        Log.e("newcookie:——————", newCookie);
        CookieSyncManager.createInstance(context).sync();
        return TextUtils.isEmpty(newCookie) ? false : true;
    }

    /**
     * /**
     * 检测当前网络可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
