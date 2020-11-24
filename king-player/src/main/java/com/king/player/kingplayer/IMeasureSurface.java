package com.king.player.kingplayer;

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
