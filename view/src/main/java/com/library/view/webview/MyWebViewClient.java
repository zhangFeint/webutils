package com.library.view.webview;

/**
 * Created by Administrator on 2017\11\23 0023.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.util.HashMap;
import java.util.Map;


/**
 * webviewb必须实现的方法 ，解决
 */
public class MyWebViewClient extends WebViewClient {
    private Activity activity;
    private String murl;

    private String qq = "mqqwpa:";
    private String wenxin = "weixin://wap/pay?";
    private String phone = "tel:";
    private String errorPath = "file:/android_asset/Networkoutage/webview404.html";
    private boolean isSourceRaw = false;


    public MyWebViewClient(Activity activity, String murl) {
        this.activity = activity;
        this.murl = murl;
    }
    public MyWebViewClient(Activity activity, String murl,boolean sourceRaw) {
        this.activity = activity;
        this.murl = murl;
        isSourceRaw = sourceRaw;
    }


    /**
     * 加载页面的服务器出现错误时（如404）调用。
     */

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view,request,error);
        view.loadUrl("about:blank"); // 避免出现默认的错误界面
        view.loadUrl(errorPath);
    }

    /**
     * 接受信任所有网站的证书
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    /**
     * 作用：打开网页时不调用系统浏览器， 而是在本WebView中显示；在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
     */
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        Log.i("MyWebViewClient", "shouldOverrideUrlLoading" + url);
        final PayTask task = new PayTask(activity);//跳转到支付宝,推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
            @Override
            public void onPayResult(final H5PayResultModel result) {
                final String url = result.getReturnUrl();
                if (!TextUtils.isEmpty(url)) {
                    view.loadUrl(url);
                }
            }
        });
        if (!isIntercepted)//判断是否成功拦截,若成功拦截，则无需继续加载该URL；否则继续加载 https://wappaygw.alipay.com
            if (!url.equalsIgnoreCase(murl)) //判断是否与当前url相同，
                if (url.startsWith(qq) || url.startsWith(wenxin) || url.startsWith(phone)) {
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(activity, "启动异常！\n请检查是否安装了该应用.", Toast.LENGTH_SHORT).show();
                    }
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    if (isSourceRaw) {
                        WebNavigationActivity.show(activity, url);
                    } else {
                        view.loadData("", "text/html", "UTF-8");  //解决部分手机调用不到js的
                        Map extraHeaders = new HashMap(); //解决微信支付少参数问题
                        extraHeaders.put("Referer", "");
                        view.loadUrl(url, extraHeaders);//""加载页面
                    }
                    return true;//返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                }
        return super.shouldOverrideUrlLoading(view,url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    /**
     * 开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

    }

    /**
     * 在页面加载结束时调用。我们可以关闭loading 条，切换程序动作。
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }
}
