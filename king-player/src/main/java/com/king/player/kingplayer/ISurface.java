package com.king.player.kingplayer;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import androidx.annotation.NonNull;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface ISurface {

    void setSurface(@NonNull SurfaceHolder surfaceHolder);

    void setSurface(@NonNull Surface surface);

    void setSurface(@NonNull SurfaceTexture surfaceTexture);

    void updateSurface(int width,int height);

    void surfaceDestroy();

    int getVideoWidth();

    int getVideoHeight();


}
