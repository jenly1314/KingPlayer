package com.king.player.kingplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

import com.king.player.kingplayer.AspectRatio;
import com.king.player.kingplayer.IMeasureSurface;
import com.king.player.kingplayer.MeasureHelper;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class KingTextureView extends TextureView implements IMeasureSurface {

    MeasureHelper mMeasureHelper;

    public KingTextureView(Context context) {
        this(context, null);
    }

    public KingTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KingTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public KingTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
