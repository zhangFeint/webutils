package com.library.webservice;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.webkit.DownloadListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017\12\28 0028.
 */

public class MyWebViewDownLoadListener implements DownloadListener {

    private Activity activity;
    private boolean isSourceRaw = false;
    public MyWebViewDownLoadListener(Activity activity) {
        this.activity = activity;
    }

    public MyWebViewDownLoadListener(Activity activity, boolean isSourceRaw) {
        this.activity = activity;
        this.isSourceRaw = isSourceRaw;
    }
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (isSourceRaw) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        } else {
            new Thread(new DownLoadThread(url)).start();
        }
    }

    public class DownLoadThread implements Runnable {

        private String dlUrl;

        public DownLoadThread(String dlUrl) {
            this.dlUrl = dlUrl;
        }

        @Override
        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(dlUrl).openConnection();
                // /data/data/packagename/files
                File sdFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Download", getNameFromUrl(dlUrl));
                downloadFile(sdFile, conn.getInputStream(), conn.getContentLength(), new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Toast.makeText(activity, "下载完毕~~~~", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDownloading(int progress) {
                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        /**
         * 下载文件
         *
         * @param file
         * @param is
         * @param total
         * @param listener
         */
        public void downloadFile(final File file, InputStream is, long total, final OnDownloadListener listener) {
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);
                    listener.onDownloading(progress);// 下载中
                }
                fos.flush();
                listener.onDownloadSuccess(file); // 下载完成
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (fos != null)
                        fos.close();
                } catch (IOException e) {
                }
            }
        }

        /**
         * @return 从下载连接中解析出文件名
         */
        @NonNull
        private String getNameFromUrl(String url) {
            return url.substring(url.lastIndexOf("/") + 1);
        }


    }

    public interface OnDownloadListener {
        //················下载成功·················
        void onDownloadSuccess(File file);

        //················下载进度·················
        void onDownloading(int progress);

        //················下载失败·················
        void onDownloadFailed();
    }
}