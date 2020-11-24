package com.king.player.kingplayer;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class DataSource {

    private String path;

    private Uri uri;

    private AssetFileDescriptor assetFileDescriptor;

    private Map<String, String> headers;

    private Set<String> options;

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

    public DataSource(AssetFileDescriptor assetFileDescriptor) {
        this.assetFileDescriptor = assetFileDescriptor;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public AssetFileDescriptor getAssetFileDescriptor() {
        return assetFileDescriptor;
    }

    public void setAssetFileDescriptor(AssetFileDescriptor assetFileDescriptor) {
        this.assetFileDescriptor = assetFileDescriptor;
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

    public Set<String> getOptions() {
        if(options == null){
            synchronized (DataSource.class){
                if(options == null){
                    options = new HashSet<>();
                }
            }
        }
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }
}
