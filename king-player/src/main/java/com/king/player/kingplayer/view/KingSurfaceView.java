package com.king.player.kingplayer.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.king.player.kingplayer.AspectRatio;
import com.king.player.kingplayer.IMeasureSurface;
import com.king.player.kingplayer.MeasureHelper;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class KingSurfaceView extends SurfaceView implements IMeasureSurface {

    MeasureHelper mMeasureHelper;

    public KingSurfaceView(Context context) {
        this(context, null);
    }

    public KingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KingSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public KingSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mMeasureHelper = new MeasureHelper(this);
        init();
    }

    public void init(){
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
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
