package com.king.player.kingplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.king.player.core.R;
import com.king.player.kingplayer.AspectRatio;
import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.measure.IMeasureSurface;
import com.king.player.kingplayer.IPlayer;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class VideoView extends FrameLayout implements IMeasureSurface, IPlayer<KingPlayer> {

    private View surfaceFrame;
    private KingSurfaceView surfaceVideo;
    private KingSurfaceView surfaceSubtitles;
    private KingTextureView textureVideo;

    private IMeasureSurface mMeasureSurface;

    private KingPlayer mPlayer;

    private boolean isSurfaceView;

    private boolean isSubtitlesSurface;

    private Surface mSurface;

    private KingPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private KingPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;

    private KingPlayer.OnPlayerEventListener mOnPlayerEventListener;

    private KingPlayer.OnErrorListener mOnErrorListener;

    protected OnSurfaceListener mOnSurfaceListener;

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs,defStyleAttr,defStyleRes);
    }

    private void init(Context context,@Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.VideoView,defStyleAttr,defStyleRes);
        final int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.VideoView_vvUseSurfaceView){
                isSurfaceView = a.getBoolean(attr,false);
            }else if(attr == R.styleable.VideoView_vvUseSubtitlesSurface){
                isSubtitlesSurface = a.getBoolean(attr,false);
            }
        }
        a.recycle();

        inflate(context, R.layout.king_player_video_view, this);
        surfaceFrame = findViewById(R.id.surfaceFrame);
        if(isSurfaceView){
            ViewStub surfaceStub = surfaceFrame.findViewById(R.id.surfaceStub);
            surfaceVideo = surfaceStub != null ? (KingSurfaceView) surfaceStub.inflate() : (KingSurfaceView) surfaceFrame.findViewById(R.id.surfaceVideo);
            if(isSubtitlesSurface){
                ViewStub subtitlesSurfaceStub = surfaceFrame.findViewById(R.id.subtitlesSurfaceStub);
                surfaceSubtitles = subtitlesSurfaceStub != null ? (KingSurfaceView) subtitlesSurfaceStub.inflate() : (KingSurfaceView) surfaceFrame.findViewById(R.id.surfaceSubtitles);
                surfaceSubtitles.setZOrderMediaOverlay(true);
                surfaceSubtitles.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            }
            mMeasureSurface = surfaceVideo;
        }else{
            ViewStub textureStub = surfaceFrame.findViewById(R.id.textureStub);
            textureVideo = textureStub != null ? (KingTextureView) textureStub.inflate() : (KingTextureView) surfaceFrame.findViewById(R.id.textureVideo);
            mMeasureSurface = textureVideo;
        }

    }


    public final void setPlayer(@NonNull KingPlayer player){
        this.mPlayer = player;
        initSurface();
    }

    private void initSurface(){
        if(isSurfaceView){
            surfaceVideo.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    mSurface = holder.getSurface();
                    mPlayer.setSurface(holder);
                    mPlayer.updateSurface(surfaceVideo.getWidth(),surfaceVideo.getHeight());
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceCreated(mSurface,surfaceVideo.getWidth(),surfaceVideo.getHeight());
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    mPlayer.updateSurface(width,height);
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceSizeChanged(mSurface,width,height);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceDestroyed(mSurface);
                    }
                    mPlayer.surfaceDestroy();
                }
            });
        }else{
            textureVideo.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    mSurface = new Surface(surface);
                    mPlayer.setSurface(mSurface);
                    mPlayer.updateSurface(width,height);
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceCreated(mSurface,width,height);
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    mPlayer.updateSurface(width,height);
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceSizeChanged(mSurface,width,height);
                    }
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    if(mOnSurfaceListener != null){
                        mOnSurfaceListener.onSurfaceDestroyed(mSurface);
                    }
                    mPlayer.surfaceDestroy();
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }

        addListener();
    }

    public void setOnVideoSizeChangedListener(KingPlayer.OnVideoSizeChangedListener listener){
        mOnVideoSizeChangedListener = listener;
    }

    public void setOnBufferingUpdateListener(KingPlayer.OnBufferingUpdateListener listener){
        this.mOnBufferingUpdateListener = listener;
    }

    public void setOnPlayerEventListener(KingPlayer.OnPlayerEventListener listener){
        this.mOnPlayerEventListener = listener;
    }

    public void setOnErrorListener(KingPlayer.OnErrorListener listener){
        this.mOnErrorListener = listener;
    }

    /**
     * 添加监听事件
     */
    private void addListener(){
        mPlayer.setOnVideoSizeChangedListener(new KingPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int videoWidth, int videoHeight) {
                setVideoSize(videoWidth,videoHeight);
                if(mOnVideoSizeChangedListener!= null){
                    mOnVideoSizeChangedListener.onVideoSizeChanged(videoWidth,videoHeight);
                }
            }
        });
        mPlayer.setOnBufferingUpdateListener(new KingPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(int percent) {
                if(mOnBufferingUpdateListener != null){
                    mOnBufferingUpdateListener.onBufferingUpdate(percent);
                }
            }
        });

        mPlayer.setOnPlayerEventListener(new KingPlayer.OnPlayerEventListener() {
            @Override
            public void onEvent(int event, Bundle bundle) {
                if(mOnPlayerEventListener != null){
                    mOnPlayerEventListener.onEvent(event,bundle);
                }
            }
        });

        mPlayer.setOnErrorListener(new KingPlayer.OnErrorListener() {
            @Override
            public void onErrorEvent(int event, @Nullable Bundle bundle) {
                if(mOnErrorListener != null){
                    mOnErrorListener.onErrorEvent(event,bundle);
                }
            }
        });
    }

    public int getSurfaceWidth(){
        return mMeasureSurface.getMeasuredWidth();
    }

    public int getSurfaceHeight(){
        return mMeasureSurface.getMeasuredHeight();
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        mMeasureSurface.setVideoSize(videoWidth,videoHeight);
        if(surfaceSubtitles != null){
            surfaceSubtitles.setVideoSize(videoWidth,videoHeight);
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mMeasureSurface.setVideoSampleAspectRatio(videoSarNum,videoSarDen);
        if(surfaceSubtitles != null){
            surfaceSubtitles.setVideoSampleAspectRatio(videoSarNum,videoSarDen);
        }
    }

    @Override
    public void setVideoRotation(int videoRotationDegree) {
        mMeasureSurface.setVideoRotation(videoRotationDegree);
        if(surfaceSubtitles != null){
            surfaceSubtitles.setVideoRotation(videoRotationDegree);
        }
    }

    @Override
    public void setAspectRatio(@AspectRatio int aspectRatio) {
        mMeasureSurface.setAspectRatio(aspectRatio);
        if(surfaceSubtitles != null){
            surfaceSubtitles.setAspectRatio(aspectRatio);
        }
    }

    @Override
    public void updateVideoSurface() {
        mMeasureSurface.updateVideoSurface();
        if(surfaceSubtitles != null){
            surfaceSubtitles.updateVideoSurface();
        }
    }

    @Override
    public void setDataSource(@NonNull DataSource dataSource) {
        if(mPlayer != null){
            mPlayer.setDataSource(dataSource);
        }
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void release() {
        mPlayer.release();
    }

    @Override
    public void reset() {
        mPlayer.reset();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getPlayerState() {
        return mPlayer.getPlayerState();
    }

    @Override
    public void setVolume(float volume) {
        mPlayer.setVolume(volume);
    }


    @Override
    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void setSpeed(float speed) {
        mPlayer.setSpeed(speed);
    }

    @Override
    public float getSpeed() {
        return mPlayer.getSpeed();
    }

    @Override
    public void setLooping(boolean looping) {
        mPlayer.setLooping(looping);
    }

    @Override
    public boolean isLopping() {
        return mPlayer.isLopping();
    }

    @Override
    public float getBufferPercentage() {
        return mPlayer.getBufferPercentage();
    }

    @Override
    public KingPlayer getPlayer() {
        return mPlayer;
    }

    public int getVideoHeight(){
        return mPlayer.getVideoWidth();
    }

    public int getVideoWidth(){
        return mPlayer.getVideoHeight();
    }

    public void setOnSurfaceListener(OnSurfaceListener listener){
        mOnSurfaceListener = listener;
    }

    public interface OnSurfaceListener{
        void onSurfaceCreated(Surface surface,int width,int height);
        void onSurfaceSizeChanged(Surface surface,int width,int height);
        void onSurfaceDestroyed(Surface surface);
    }
}
