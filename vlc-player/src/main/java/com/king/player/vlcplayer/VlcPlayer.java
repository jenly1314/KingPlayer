package com.king.player.vlcplayer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.util.LogUtils;
import com.king.player.vlcplayer.source.VlcDataSource;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IMedia;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class VlcPlayer extends KingPlayer<MediaPlayer> implements IVLCVout.OnNewVideoLayoutListener {

    private MediaPlayer mMediaPlayer;

    private Context mContext;

    private DataSource mDataSource;

    private Media mMedia;

    private HandlerThread mHandlerThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    private static final int INIT_START = 1;
    private static final int INIT_PAUSE = 2;
    private static final int INIT_STOP = 3;
    private static final int INIT_RELEASE = 4;
    private static final int INIT_RESET = 5;
    private static final int INIT_DATA_SOURCE = 6;

    private Bundle mBundle = obtainBundle();

    public VlcPlayer(@NonNull Context context){
        mContext = context.getApplicationContext();
        LibVLC libVLC = new LibVLC(context);
        mMediaPlayer = new MediaPlayer(libVLC);
        initHandlerThread();
    }

    public VlcPlayer(@NonNull Context context, @Nullable ArrayList<String> options){
        mContext = context.getApplicationContext();
        LibVLC libVLC = new LibVLC(context,options);
        mMediaPlayer = new MediaPlayer(libVLC);
        initHandlerThread();
    }

    public VlcPlayer(@NonNull Context context,@NonNull MediaPlayer mediaPlayer){
        mContext = context.getApplicationContext();
        mMediaPlayer = mediaPlayer;
        initHandlerThread();
    }

    private void initHandlerThread(){
        mHandlerThread = new HandlerThread("VLC-Thread");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case INIT_START:
                        onStart();
                        break;
                    case INIT_PAUSE:
                        onPause();
                        break;
                    case INIT_STOP:
                        onStop();
                        break;
                    case INIT_RELEASE:
                        onRelease();
                        break;
                    case INIT_RESET:
                        onReset();
                        break;
                    case INIT_DATA_SOURCE:
                        onLoadMedia();
                        break;
                }
                mMainHandler.obtainMessage(msg.what).sendToTarget();
                return true;
            }
        });

        mMainHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case INIT_START:
                        sendPlayerEvent(Event.EVENT_ON_START);
                        break;
                    case INIT_PAUSE:
                        sendPlayerEvent(Event.EVENT_ON_PAUSE);
                        break;
                    case INIT_STOP:
                        sendPlayerEvent(Event.EVENT_ON_STOP);
                        break;
                    case INIT_RELEASE:
                        sendPlayerEvent(Event.EVENT_ON_RELEASE);
                        break;
                    case INIT_RESET:
                        sendPlayerEvent(Event.EVENT_ON_RESET);
                        break;
                    case INIT_DATA_SOURCE:
                        mCurrentState = STATE_PREPARED;
                        if (mTargetState == STATE_PLAYING) {
                            start();
                        }else if(mTargetState == STATE_PAUSED){
                            pause();
                        }else if(mTargetState == STATE_STOPPED
                                || mTargetState == STATE_IDLE){
                            reset();
                        }
                        sendPlayerEvent(Event.EVENT_ON_DATA_SOURCE_SET);
                        break;

                }
            }
        };

    }

    private void onLoadMedia(){
        if(mMedia != null){
            mMediaPlayer.setMedia(mMedia);
            mMedia.release();
        }

    }


    @Override
    public void setSurface(@NonNull SurfaceHolder surfaceHolder) {
        mMediaPlayer.getVLCVout().setVideoSurface(surfaceHolder.getSurface(),surfaceHolder);
        if(!mMediaPlayer.getVLCVout().areViewsAttached()){
            mMediaPlayer.getVLCVout().attachViews(this);
        }
        sendPlayerEvent(Event.EVENT_ON_SURFACE_HOLDER_UPDATE);
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        mMediaPlayer.getVLCVout().setVideoSurface(surface,null);
        if(!mMediaPlayer.getVLCVout().areViewsAttached()){
            mMediaPlayer.getVLCVout().attachViews(this);
        }
        sendPlayerEvent(Event.EVENT_ON_SURFACE_UPDATE);
    }

    @Override
    public void setSurface(@NonNull SurfaceTexture surfaceTexture) {
        mMediaPlayer.getVLCVout().setVideoSurface(surfaceTexture);
        if(!mMediaPlayer.getVLCVout().areViewsAttached()){
            mMediaPlayer.getVLCVout().attachViews(this);
        }
        sendPlayerEvent(Event.EVENT_ON_SURFACE_UPDATE);
    }

    @Override
    public void updateSurface(int width, int height) {
        mMediaPlayer.getVLCVout().setWindowSize(width,height);
    }

    @Override
    public void surfaceDestroy() {
        if (mMediaPlayer.getVLCVout().areViewsAttached()){
            mMediaPlayer.getVLCVout().detachViews();
        }
    }


    @Override
    public void setDataSource(@NonNull DataSource dataSource) {
        try {
            if(mDataSource != null){
                resetListener();
            }
            mDataSource = dataSource;
            addListener();
            Media media = null;
            if(!TextUtils.isEmpty(mDataSource.getPath())){
                if (mDataSource.getPath().contains("://")) {
                    media = new Media(mMediaPlayer.getLibVLC(),Uri.parse(mDataSource.getPath()));
                } else {
                    media = new Media(mMediaPlayer.getLibVLC(),mDataSource.getPath());
                }
            }else if(mDataSource.getUri() != null){
                media = new Media(mMediaPlayer.getLibVLC(),mDataSource.getUri());
            }else if(mDataSource.getAssetFileDescriptor(mContext) != null){
                media = new Media(mMediaPlayer.getLibVLC(),mDataSource.getAssetFileDescriptor(mContext));
            }else{
                LogUtils.d(mDataSource.toString());
            }
            if(media != null){
                if(mDataSource instanceof VlcDataSource){
                    initOptions(media,((VlcDataSource) mDataSource).getOptions());
                }
                media.setEventListener(new IMedia.EventListener() {
                    @Override
                    public void onEvent(IMedia.Event event) {
                        LogUtils.d("Media.onEvent: " + event.type);
                        switch (event.type){
                            case IMedia.Event.DurationChanged:
                                LogUtils.d("IMedia.Event.DurationChanged: " + event.type);
                                IMedia.VideoTrack track = mMediaPlayer.getCurrentVideoTrack();
                                if(track != null){
                                    LogUtils.d( String.format("track: %d*%d",track.width,track.height));
                                    sendVideoSizeChangeEvent(track.width,track.height);
                                }
                                break;
                            case IMedia.Event.MetaChanged:
                                LogUtils.d("IMedia.Event.MetaChanged: " + event.type);
                                break;
                            case IMedia.Event.ParsedChanged:
                                LogUtils.d("IMedia.Event.ParsedChanged: " + event.type);
                                break;
                            case IMedia.Event.StateChanged:
                                LogUtils.d("IMedia.Event.StateChanged: " + event.type);
                                break;
                            case IMedia.Event.SubItemAdded:
                                LogUtils.d("IMedia.Event.SubItemAdded: " + event.type);
                                break;
                        }

                    }
                });
                mMedia = media;
                mCurrentState = STATE_PREPARING;
                mTargetState = STATE_PREPARING;
                mWorkHandler.obtainMessage(INIT_DATA_SOURCE).sendToTarget();
            }
        } catch (Exception e) {
            handleException(e,false);
            mCurrentState = STATE_ERROR;
        }

    }

    public void initOptions(@NonNull IMedia media, @Nullable Collection<String> options){
        if(options != null){
            for(String option: options){
                media.addOption(option);
            }
        }
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        LogUtils.d(String.format("onNewVideoLayout: %d * %d | visible: %d * %d",width,height,visibleWidth,visibleHeight));
        if(width * height != 0){
            sendVideoSizeChangeEvent(width,height);
        }
    }


    private void addListener(){
        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                int what = Event.EVENT_ON_COMMON;
                switch (event.type){
                    case MediaPlayer.Event.Buffering:
                        LogUtils.d(String.format("Event.Buffering: 0x%s",Integer.toHexString(event.type)));
                        int buffering = (int)event.getBuffering();
                        if(buffering == 0){
                            what = Event.EVENT_ON_BUFFERING_START;
                        }
                        sendBufferingUpdateEvent(buffering);

                        if(buffering == 100){
                            what = Event.EVENT_ON_BUFFERING_END;
                        }
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        LogUtils.d(String.format("Event.EncounteredError: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.EndReached:
                        LogUtils.d(String.format("Event.EndReached: 0x%s",Integer.toHexString(event.type)));
                        handleLoopPlayer();
                        break;
                    case MediaPlayer.Event.ESAdded:
                        LogUtils.d(String.format("Event.ESAdded: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.ESDeleted:
                        LogUtils.d(String.format("Event.ESDeleted: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.LengthChanged:
                        LogUtils.d(String.format("Event.LengthChanged: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.MediaChanged:
                        LogUtils.d(String.format("Event.MediaChanged: 0x%s",Integer.toHexString(event.type)));
                        what = Event.EVENT_ON_METADATA_UPDATE;
                        break;
                    case MediaPlayer.Event.Opening:
                        LogUtils.d(String.format("Event.Opening: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.PausableChanged:
                        LogUtils.d(String.format("Event.PausableChanged: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.Paused:
                        LogUtils.d(String.format("Event.Paused: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.Playing:
                        LogUtils.d(String.format("Event.Playing: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.PositionChanged:
                        LogUtils.d(String.format("Event.PositionChanged: 0x%s",Integer.toHexString(event.type)));
                        what = Event.EVENT_ON_TIMER_UPDATE;
                        mBundle.putInt(EventBundleKey.KEY_POSITION,(int)event.getPositionChanged());
                        break;
                    case MediaPlayer.Event.RecordChanged:
                        LogUtils.d(String.format("Event.RecordChanged: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.SeekableChanged:
                        LogUtils.d(String.format("Event.SeekableChanged: 0x%s",Integer.toHexString(event.type)));
                        what = Event.EVENT_ON_SEEK_COMPLETE;
                        break;
                    case MediaPlayer.Event.Stopped:
                        LogUtils.d(String.format("Event.Stopped: 0x%s",Integer.toHexString(event.type)));
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        LogUtils.d(String.format("Event.TimeChanged: 0x%s",Integer.toHexString(event.type)));
                        what = Event.EVENT_ON_TIMER_UPDATE;
                        mBundle.putInt(EventBundleKey.KEY_TIME,(int)event.getTimeChanged());
                        break;
                    case MediaPlayer.Event.Vout:
                        LogUtils.d(String.format("Event.Vout: 0x%s",Integer.toHexString(event.type)));
                        break;
                }

                mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EVENT,event.type);
                sendPlayerEvent(what,mBundle);
                recycleBundle();
            }
        });
        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.setAspectRatio(null);
        mMediaPlayer.setScale(0);
        mMediaPlayer.getVLCVout().addCallback(new IVLCVout.Callback() {
            @Override
            public void onSurfacesCreated(IVLCVout vlcVout) {
                LogUtils.d("onSurfacesCreated");
            }

            @Override
            public void onSurfacesDestroyed(IVLCVout vlcVout) {
                LogUtils.d("onSurfacesDestroyed");

            }
        });

    }

    private void handleLoopPlayer(){
        if(isLopping()){
            setDataSource(mDataSource);
            start();
        }
    }

    private void resetListener(){
        if(available()){
            mMediaPlayer.setEventListener(null);
        }
    }

    private void recycleBundle(){
        if(mBundle != null){
            mBundle.clear();
        }
    }

    private boolean available(){
        return mMediaPlayer != null;
    }

    private boolean hasDataSource(){
        return available() && mMediaPlayer.hasMedia();
    }

    @Override
    public void start() {
        try{
            if(hasDataSource() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PAUSED
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mWorkHandler.obtainMessage(INIT_START).sendToTarget();
            }
        }catch (Exception e){
            handleException(e,true);
        }
        mTargetState = STATE_PLAYING;
    }

    private void onStart(){
        mMediaPlayer.play();
        mCurrentState = STATE_PLAYING;
        sendPlayerEvent(Event.EVENT_ON_START);
        LogUtils.d("start");
    }

    @Override
    public void pause() {
        try{
            if(hasDataSource() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PLAYING
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mWorkHandler.obtainMessage(INIT_PAUSE).sendToTarget();
            }else{
                LogUtils.d("currentState = " + mCurrentState);
            }
        }catch (Exception e){
            handleException(e,true);
        }
        mTargetState = STATE_PAUSED;
    }

    private void onPause(){
        mMediaPlayer.pause();
        mCurrentState = STATE_PAUSED;
        sendPlayerEvent(Event.EVENT_ON_PAUSE);
        LogUtils.d("pause");
    }

    @Override
    public void stop() {
        if(hasDataSource() && (mCurrentState == STATE_PREPARED
                || mCurrentState == STATE_PLAYING
                || mCurrentState == STATE_PAUSED
                || mCurrentState == STATE_PLAYBACK_COMPLETED)){
            mWorkHandler.obtainMessage(INIT_STOP).sendToTarget();
        }else{
            LogUtils.d("currentState = " + mCurrentState);
        }
        mTargetState = STATE_STOPPED;
    }


    private void onStop(){
        final IMedia media = mMediaPlayer.getMedia();
        if (media != null) {
            mMediaPlayer.stop();
            LogUtils.d("stop");
            mMediaPlayer.setMedia(null);
            if(!media.isReleased()){
                media.release();
            }
        }
        mCurrentState = STATE_STOPPED;
        sendPlayerEvent(Event.EVENT_ON_STOP);
    }

    @Override
    public void release() {
        if(available()){
            mWorkHandler.obtainMessage(INIT_RELEASE).sendToTarget();
        }
        mTargetState = STATE_IDLE;
    }

    private void onRelease(){
        LogUtils.d("release");
        if(!mMediaPlayer.getLibVLC().isReleased()){
            mMediaPlayer.getLibVLC().release();
        }
        if(!mMediaPlayer.isReleased()){
            mMediaPlayer.release();
        }
        resetListener();
        mCurrentState = STATE_IDLE;
        sendPlayerEvent(Event.EVENT_ON_RELEASE);
        if(mHandlerThread != null){
            mHandlerThread.quitSafely();
        }
    }


    @Override
    public void reset() {
        if(available()){
            mWorkHandler.obtainMessage(INIT_RESET).sendToTarget();
        }
    }

    private void onReset(){
        mMediaPlayer.setMedia(null);
        mCurrentState = STATE_IDLE;
        sendPlayerEvent(Event.EVENT_ON_RESET);
    }

    @Override
    public boolean isPlaying() {
        if(available()){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getPlayerState() {
        if(available()){
            return mMediaPlayer.getPlayerState();
        }
        return super.getPlayerState();
    }

    @Override
    public void setVolume(float volume) {
        if(available()){
            mMediaPlayer.setVolume((int)volume);
        }
    }


    @Override
    public void seekTo(int msec) {
        if(mMediaPlayer.hasMedia() && !isReleased()){
            mMediaPlayer.setTime(msec);
            Bundle bundle = obtainBundle();
            bundle.putInt(EventBundleKey.KEY_TIME,msec);
            sendPlayerEvent(Event.EVENT_ON_SEEK_TO,bundle);
        }
    }

    @Override
    public int getCurrentPosition() {
        return (int)mMediaPlayer.getTime();
    }

    @Override
    public int getDuration() {
        return (int)mMediaPlayer.getLength();
    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayer.setRate(speed);
    }

    @Override
    public float getSpeed() {
        return mMediaPlayer.getRate();
    }

    @Override
    public void setLooping(boolean looping) {
        super.setLooping(looping);
    }


    private boolean isReleased(){
        return mMediaPlayer.isReleased();
    }

    @Override
    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }

    /**
     * 开始录制
     * @param directory 录制的存储路径
     * @return
     */
    public boolean startRecord(String directory){
        return mMediaPlayer.record(directory);
    }

    /**
     * 停止录制
     * @return
     */
    public boolean stopRecord(){
        return mMediaPlayer.record(null);
    }
}
