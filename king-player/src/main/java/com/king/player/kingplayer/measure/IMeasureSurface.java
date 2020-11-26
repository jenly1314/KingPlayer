package com.king.player.kingplayer.measure;

import com.king.player.kingplayer.AspectRatio;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface IMeasureSurface {

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    void setVideoRotation(int videoRotationDegree);

    int getMeasuredWidth();

    int getMeasuredHeight();

    void setAspectRatio(@AspectRatio int aspectRatio);

    void updateVideoSurface();
}
