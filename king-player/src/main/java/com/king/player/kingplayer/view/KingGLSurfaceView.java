package com.king.player.kingplayer.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.king.player.kingplayer.AspectRatio;
import com.king.player.kingplayer.IMeasureSurface;
import com.king.player.kingplayer.MeasureHelper;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class KingGLSurfaceView extends GLSurfaceView implements IMeasureSurface {

    MeasureHelper mMeasureHelper;

    public KingGLSurfaceView(Context context) {
        this(context, null);
    }

    public KingGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMeasureHelper = new MeasureHelper(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureHelper.doMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        mMeasureHelper.setVideoSize(videoWidth,videoHeight);
        if(videoWidth * videoHeight > 0){
            getHolder().setFixedSize(videoWidth,videoHeight);
        }
        requestLayout();
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mMeasureHelper.setVideoSampleAspectRatio(videoSarNum,videoSarDen);
        requestLayout();
    }

    @Override
    public void setVideoRotation(int videoRotationDegree) {
        mMeasureHelper.setVideoRotation(videoRotationDegree);
        requestLayout();
    }

    @Override
    public void setAspectRatio(@AspectRatio int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void updateVideoSurface() {
        requestLayout();
    }
}
