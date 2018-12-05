package com.library.webservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.webkit.ValueCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * 功能：
 *
 * @author：zhangerpeng
 * @create：2018\12\3 0003 13:47
 * @version：2018 1.0
 * Created with IntelliJ IDEA
 */

public class DialogUtils {
    private static DialogUtils wevdialogUtils;
    public ValueCallback valueCallback;
    public static final int REQUEST_CODE_CAMERA = 110;//相机选择结果码
    public static final int REQUEST_CODE_PHOTOS = 111;//相册选择结果码
    public static final int REQUEST_CODE_FILE = 112;//文件选择结果码

    private String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";

    private Uri cameraUri;//相机返回的绝对路径
    private File file;    //相机返回的文件

    public static DialogUtils getInstance() {
        if (wevdialogUtils == null) {
            wevdialogUtils = new DialogUtils();
        }
        return wevdialogUtils;
    }

    /**
     * 弹出对话框
     */
    public void showCameraDialog(final Activity activity, ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("请选择");
        builder.setItems(new String[]{"相机", "相册", "取消选择"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openCamera(activity);
                        break;
                    case 1:
                        openPhotoAlbum(activity);
                        break;
                    case 2:
                        cancelFilePathCallback();
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 调用相机
     */
    public void openCamera(final Activity activity) {
        file = new File(filePath + File.separator + getPicName("IMG_"));
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath()); //保存到默认相机目录
        cameraUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues); // 其次把文件插入到系统图库
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri); //添加到文件里
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);

    }

    /**
     * 调用相册
     */
    public void openPhotoAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, REQUEST_CODE_PHOTOS);
    }

    /**
     * Name  IMG_+时间==图片地址
     */
    private String getPicName(String Name) {
        return Name + String.valueOf(System.currentTimeMillis()) + ".jpg";
    }

    public File getCameraFile() {
        return file;
    }

    /**
     * 处理返回数据 webview上传文件
     *
     * @param activity
     * @param resultCode
     * @param data
     */
    public void takeFileResult(Activity activity, int resultCode, Intent data) {
        if (valueCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = getPath(activity, result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    valueCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    valueCallback.onReceiveValue(uri);
                }
            } else {
                valueCallback.onReceiveValue(null);
                valueCallback = null;
            }
        }
    }

    /**
     * 处理返回数据 webview上传相册
     *
     * @param activity
     * @param resultCode
     * @param data
     */
    public void takePhotoResult(Activity activity, int resultCode, Intent data) {
        if (valueCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = getPath(activity, result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    valueCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    valueCallback.onReceiveValue(uri);
                }
            } else {
                valueCallback.onReceiveValue(null);
                valueCallback = null;
            }
        }
    }

    /**
     * 处理返回数据 webview上传相机
     *
     * @param resultCode
     * @param file
     */
    public void takePictureResult(int resultCode, File file) {
        if (valueCallback != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    valueCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    valueCallback.onReceiveValue(uri);
                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                valueCallback.onReceiveValue(null);
                valueCallback = null;
            }
        }
    }


    /**
     * 置空FilePathCallback 对象
     */
    public void cancelFilePathCallback() {
        if (valueCallback != null) {
            valueCallback.onReceiveValue(null);
            valueCallback = null;
        }
    }

    //****************************************专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使***************************************************

    @SuppressLint("NewApi")
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
