package com.king.player.kingplayer.source;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DataSource {

    private String path;

    private Uri uri;

    private String assetFilePath;

    private AssetFileDescriptor assetFileDescriptor;

    private Map<String, String> headers;

    public DataSource() {
    }

    public DataSource(String path) {
        this.path = path;
    }

    public DataSource(Uri uri) {
        this.uri = uri;
    }

    public DataSource(Uri uri, Map<String, String> headers) {
        this.uri = uri;
        this.headers = headers;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        assetFileDescriptor = null;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
        assetFileDescriptor = null;
    }

    public String getAssetFilePath() {
        return assetFilePath;
    }

    public void setAssetFilePath(String assetFilePath) {
        this.assetFilePath = assetFilePath;
    }

    public AssetFileDescriptor getAssetFileDescriptor(Context context) {
        if(assetFilePath != null){
            if(assetFileDescriptor == null){
                assetFileDescriptor = getAssetsFileDescriptor(context,assetFilePath);
            }
            return assetFileDescriptor;
        }
        return null;
    }

    public Map<String, String> getHeaders() {
        if(headers == null){
            synchronized (DataSource.class){
                if(headers == null){
                    headers = new HashMap<>();
                }
            }
        }
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static Uri buildAssetsUri(String assetsPath){
        return Uri.parse("file:///android_asset/" + assetsPath);
    }

    public static AssetFileDescriptor getAssetsFileDescriptor(Context context, String assetFilePath){
        try {
            if(TextUtils.isEmpty(assetFilePath))
                return null;
            return context.getAssets().openFd(assetFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public String toString() {
        return "DataSource{" +
                "path='" + path + '\'' +
                ", uri=" + uri +
                ", assetFilePath='" + assetFilePath + '\'' +
                ", headers=" + headers +
                '}';
    }
}
