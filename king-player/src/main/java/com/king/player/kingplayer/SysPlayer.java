package com.king.player.kingplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.util.LogUtils;



/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class SysPlayer extends KingPlayer<MediaPlayer> {

    private MediaPlayer mMediaPlayer;

    private Context mContext;

    private DataSource mDataSource;

    private Bundle mBundle = obtainBundle();

    public SysPlayer(@NonNull Context context){
        this(context,null);
    }

    public SysPlayer(@NonNull Context context,MediaPlayer mediaPlayer){
        this.mContext = context.getApplicationContext();
        this.mMediaPlayer = mediaPlayer != null ? mediaPlayer : new MediaPlayer();
    }

    @Override
    public void setSurface(@NonNull SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(surfaceHolder);
        mMediaPlayer.setScreenOnWhilePlaying(true);
        sendPlayerEvent(Event.EVENT_ON_SURFACE_HOLDER_UPDATE);
    }

    @Override
    public void setSurface(@NonNull Surface surface) {
        mMediaPlayer.setSurface(surface);
        sendPlayerEvent(Event.EVENT_ON_SURFACE_UPDATE);
    }

    @Override
    public void setSurface(@NonNull SurfaceTexture surfaceTexture) {
        setSurface(new Surface(surfaceTexture));
    }

    private void addListener(){
        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                sendVideoSizeChangeEvent(width,height);
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                LogUtils.d("onPrepared");
                mCurrentState = STATE_PREPARED;
                sendPlayerEvent(Event.EVENT_ON_PREPARED);

                if (mTargetState == STATE_PLAYING) {
                    start();
                }else if(mTargetState == STATE_PAUSED){
                    pause();
                }else if(mTargetState == STATE_STOPPED
                        || mTargetState == STATE_IDLE){
                    reset();
                }
            }
        });

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                sendBufferingUpdateEvent(percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                sendPlayerEvent(Event.EVENT_ON_PLAY_COMPLETE);
            }
        });


        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                LogUtils.w("onError: " + what + ", extra:" + extra);
                int event = ErrorEvent.ERROR_EVENT_COMMON;
                switch (what){
                    case MediaPlayer.MEDIA_ERROR_IO:
                        event = ErrorEvent.ERROR_EVENT_IO;
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        event = ErrorEvent.ERROR_EVENT_MALFORMED;
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        event = ErrorEvent.ERROR_EVENT_TIMED_OUT;
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        event = ErrorEvent.ERROR_EVENT_UNKNOWN;
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        event = ErrorEvent.ERROR_EVENT_UNSUPPORTED;
                        break;
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        event = ErrorEvent.ERROR_EVENT_SERVER_DIED;
                        break;
                    case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        event = ErrorEvent.ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
                        break;
                }
                mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EVENT,what);
                mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EXTRA,extra);
                sendErrorEvent(event,mBundle);
                recycleBundle();
                return true;
            }
        });

        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                int event = Event.EVENT_ON_COMMON;
                switch (what){
                    case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:
                        LogUtils.d("MEDIA_INFO_AUDIO_NOT_PLAYING");
                        break;
                    case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        LogUtils.d("MEDIA_INFO_BAD_INTERLEAVING");
                        event = Event.EVENT_ON_BAD_INTERLEAVING;
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        LogUtils.d("MEDIA_INFO_BUFFERING_END");
                        event = Event.EVENT_ON_BUFFERING_END;
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        LogUtils.d("MEDIA_INFO_BUFFERING_START");
                        event = Event.EVENT_ON_BUFFERING_START;
                        break;
                    case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        LogUtils.d("MEDIA_INFO_METADATA_UPDATE");
                        event = Event.EVENT_ON_METADATA_UPDATE;
                        break;
                    case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        LogUtils.d("MEDIA_INFO_NOT_SEEKABLE");
                        event = Event.EVENT_ON_NOT_SEEK_ABLE;
                        break;
                    case MediaPlayer.MEDIA_INFO_STARTED_AS_NEXT:
                        LogUtils.d("MEDIA_INFO_STARTED_AS_NEXT");
                        break;
                    case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        LogUtils.d("MEDIA_INFO_SUBTITLE_TIMED_OUT");
                        event = Event.EVENT_ON_SUBTITLE_TIMED_OUT;
                        break;
                    case MediaPlayer.MEDIA_INFO_UNKNOWN:
                        LogUtils.d("MEDIA_INFO_UNKNOWN");
                        event = Event.EVENT_ON_UNKNOWN;
                        break;
                    case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        LogUtils.d("MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                        event = Event.EVENT_ON_UNSUPPORTED_SUBTITLE;
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING:
                        LogUtils.d("MEDIA_INFO_VIDEO_NOT_PLAYING");
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        LogUtils.d("MEDIA_INFO_VIDEO_RENDERING_START");
                        event = Event.EVENT_ON_VIDEO_RENDER_START;
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        LogUtils.d("MEDIA_INFO_VIDEO_TRACK_LAGGING");
                        break;
                    default:
                        LogUtils.d("onInfo:" + what);
                        break;
                }

                mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EVENT,what);
                mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EXTRA,extra);
                sendPlayerEvent(event,mBundle);
                recycleBundle();
                return true;
            }
        });
    }

    private void resetListener(){
        if(available()){
            mMediaPlayer.setOnVideoSizeChangedListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnBufferingUpdateListener(null);
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnInfoListener(null);
        }
    }

    private void recycleBundle(){
        if(mBundle != null){
            mBundle.clear();
        }
    }


    @Override
    public void updateSurface(int width, int height) {

    }

    @Override
    public void surfaceDestroy() {

    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public void setDataSource(@NonNull DataSource dataSource) {
        try {

            if(mDataSource != null){
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                resetListener();
            }

            mDataSource = dataSource;
            addListener();
            if(!TextUtils.isEmpty(mDataSource.getPath())){
               mMediaPlayer.setDataSource(mDataSource.getPath());
            }else if(mDataSource.getUri() != null){
                mMediaPlayer.setDataSource(mContext,mDataSource.getUri(),mDataSource.getHeaders());
            }else if(mDataSource.getAssetFileDescriptor(mContext) != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mMediaPlayer.setDataSource(mDataSource.getAssetFileDescriptor(mContext));
                }else{
                    AssetFileDescriptor assetFileDescriptor = mDataSource.getAssetFileDescriptor(mContext);
                    if(assetFileDescriptor.getDeclaredLength() < 0){
                        mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
                    }else{
                        mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),assetFileDescriptor.getStartOffset(),assetFileDescriptor.getDeclaredLength());
                    }

                }
            }else{
                LogUtils.d(mDataSource.toString());
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(isLopping());
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARING;
            sendPlayerEvent(Event.EVENT_ON_DATA_SOURCE_SET);
        } catch (Exception e) {
            handleException(e,false);
            mCurrentState = STATE_ERROR;
        }
    }

    private boolean available(){
        return mMediaPlayer != null;
    }

    private boolean hasDataSource(){
        return available() && mDataSource != null;
    }

    @Override
    public void start() {
        try{
            if(hasDataSource() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PAUSED
                    || mCurrentState == STATE_STOPPED
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
                LogUtils.d("start");
                sendPlayerEvent(Event.EVENT_ON_START);

            }else{
                LogUtils.d("currentState = " + mCurrentState);
            }
        }catch (Exception e){
            handleException(e,true);
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        try{
            if(hasDataSource() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PLAYING
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
                sendPlayerEvent(Event.EVENT_ON_PAUSE);
                LogUtils.d("pause");
            }else{
                LogUtils.d("currentState = " + mCurrentState);
            }
        }catch (Exception e){
            handleException(e,true);
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void stop() {
        try{
            if(hasDataSource() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PLAYING
                    || mCurrentState == STATE_PAUSED
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mMediaPlayer.stop();
                mCurrentState = STATE_STOPPED;
                sendPlayerEvent(Event.EVENT_ON_STOP);
                LogUtils.d("stop");
            }else{
                LogUtils.d("currentState = " + mCurrentState);
            }
        }catch (Exception e){
            handleException(e,true);
        }
        mTargetState = STATE_STOPPED;
    }

    @Override
    public void release() {
        if(available()){
            mMediaPlayer.release();
            mCurrentState = STATE_IDLE;
            sendPlayerEvent(Event.EVENT_ON_RELEASE);
            LogUtils.d("release");
        }
        resetListener();
        mTargetState = STATE_IDLE;
    }

    @Override
    public void reset() {
        if(available()){
            mMediaPlayer.reset();
            mCurrentState = STATE_IDLE;
            sendPlayerEvent(Event.EVENT_ON_RESET);
            LogUtils.d("reset");
        }
        mTargetState = STATE_IDLE;
    }

    @Override
    public boolean isPlaying() {
        try {
            if(available()){
                return mMediaPlayer.isPlaying();
            }
        }catch (Exception e){
            handleException(e,false);
        }
        return false;
    }


    @Override
    public void setVolume(float volume) {
        if(available()){
            mMediaPlayer.setVolume(volume,volume);
        }
    }

    @Override
    public void seekTo(int msec) {
        try{
            if(available() && (mCurrentState == STATE_PREPARED
                    || mCurrentState == STATE_PAUSED
                    || mCurrentState == STATE_PLAYBACK_COMPLETED)){
                mMediaPlayer.seekTo(msec);
                Bundle bundle = obtainBundle();
                bundle.putInt(EventBundleKey.KEY_TIME,msec);
                sendPlayerEvent(Event.EVENT_ON_SEEK_TO,bundle);
            }
        }catch (Exception e){
            handleException(e,true);
        }

    }

    @Override
    public int getCurrentPosition() {
        if(!available()){
            return -1;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        if(!available()){
            return -1;
        }
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setSpeed(float speed) {
        try{
            if (available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMediaPlayer.getPlaybackParams().setSpeed(speed);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public float getSpeed() {
        try{
            if(available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mMediaPlayer.getPlaybackParams().getSpeed();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 1.0f;
    }

    @Override
    public void setLooping(boolean looping) {
        setLooping(looping);
        if(available()){
            mMediaPlayer.setLooping(looping);
        }

    }


    @Override
    public MediaPlayer getPlayer() {
        return mMediaPlayer;
    }
}
