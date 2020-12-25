package com.king.player.ijkplayer;


import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.king.player.ijkplayer.source.IjkDataSource;
import com.king.player.ijkplayer.source.RawMediaDataSource;
import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.util.LogUtils;

import java.io.FileDescriptor;
import java.util.Collection;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class IjkPlayer extends KingPlayer<IjkMediaPlayer> {

    private IjkMediaPlayer mMediaPlayer;

    private Context mContext;

    private DataSource mDataSource;

    private Bundle mBundle = obtainBundle();

    public IjkPlayer(@NonNull Context context){
        this(context,null);
    }

    public IjkPlayer(@NonNull Context context,@Nullable IjkMediaPlayer mediaPlayer){
        this.mContext = context.getApplicationContext();
        mMediaPlayer = mediaPlayer != null ? mediaPlayer : new IjkMediaPlayer();
    }

    @Override
    public void setDataSource(@NonNull DataSource dataSource) {
        try {
            LogUtils.d("setDataSource");
            if(mDataSource != null){
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                resetListener();
            }

            mDataSource = dataSource;

            if(mDataSource instanceof IjkDataSource){
                initOptions(mMediaPlayer,((IjkDataSource) mDataSource).getOptions());
            }
            addListener();
            if(!TextUtils.isEmpty(mDataSource.getPath())){
                mMediaPlayer.setDataSource(mDataSource.getPath());
            }else if(mDataSource.getUri() != null){
                mMediaPlayer.setDataSource(mContext,mDataSource.getUri(),mDataSource.getHeaders());
            }else if(mDataSource.getAssetFileDescriptor(mContext) != null){
                FileDescriptor fileDescriptor = mDataSource.getAssetFileDescriptor(mContext).getFileDescriptor();
                if(fileDescriptor != null){
                    mMediaPlayer.setDataSource(fileDescriptor);
                }else{
                    mMediaPlayer.setDataSource(new RawMediaDataSource(mDataSource.getAssetFileDescriptor(mContext)));
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

    public void initOptions(@NonNull IjkMediaPlayer mediaPlayer, @Nullable Collection<IjkDataSource.OptionModel> options){

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);

        //accurate seek
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        if(options != null){
            for(IjkDataSource.OptionModel option: options){
                if(option.isString()){
                    mediaPlayer.setOption(option.getCategory(),option.getName(), option.getValueString());
                }else{
                    mediaPlayer.setOption(option.getCategory(),option.getName(), option.getValueLong());
                }
            }
        }
    }

    private void addListener(){
        LogUtils.d("onPrepared");
        mMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer,int width, int height, int sarNum, int sarDen) {
                sendVideoSizeChangeEvent(width,height);
            }
        });

        mMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
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

        mMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
                sendBufferingUpdateEvent(percent);
            }
        });

        mMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                sendPlayerEvent(Event.EVENT_ON_PLAY_COMPLETE);
            }
        });

        mMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                LogUtils.w("onError: " + what + ", extra:" + extra);
                int event = ErrorEvent.ERROR_EVENT_COMMON;
                switch (what){
                    case IMediaPlayer.MEDIA_ERROR_IO:
                        event = ErrorEvent.ERROR_EVENT_IO;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                        event = ErrorEvent.ERROR_EVENT_MALFORMED;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        event = ErrorEvent.ERROR_EVENT_TIMED_OUT;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                        event = ErrorEvent.ERROR_EVENT_UNKNOWN;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        event = ErrorEvent.ERROR_EVENT_UNSUPPORTED;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        event = ErrorEvent.ERROR_EVENT_SERVER_DIED;
                        break;
                    case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
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

        mMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer,int what, int extra) {
                int event = Event.EVENT_ON_COMMON;
                switch (what){
                    case IMediaPlayer.MEDIA_INFO_AUDIO_DECODED_START:
                        LogUtils.d("MEDIA_INFO_AUDIO_DECODED_START");
                        event = Event.EVENT_ON_AUDIO_DECODER_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        LogUtils.d("MEDIA_INFO_AUDIO_RENDERING_START");
                        event = Event.EVENT_ON_AUDIO_RENDER_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START:
                        LogUtils.d("MEDIA_INFO_AUDIO_SEEK_RENDERING_START");
                        event = Event.EVENT_ON_AUDIO_SEEK_RENDERING_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        LogUtils.d("MEDIA_INFO_BAD_INTERLEAVING");
                        event = Event.EVENT_ON_BAD_INTERLEAVING;
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        LogUtils.d("MEDIA_INFO_BUFFERING_END");
                        event = Event.EVENT_ON_BUFFERING_END;
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        LogUtils.d("MEDIA_INFO_BUFFERING_START");
                        event = Event.EVENT_ON_BUFFERING_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:
                        LogUtils.d("MEDIA_INFO_COMPONENT_OPEN");
                        event = Event.EVENT_ON_COMPONENT_OPEN;
                        break;
                    case IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO:
                        LogUtils.d("MEDIA_INFO_FIND_STREAM_INFO");
                        break;
                    case IMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE:
                        LogUtils.d("MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE");
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        LogUtils.d("MEDIA_INFO_METADATA_UPDATE");
                        event = Event.EVENT_ON_METADATA_UPDATE;
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        LogUtils.d("MEDIA_INFO_NETWORK_BANDWIDTH");
                        event = Event.EVENT_ON_NETWORK_BANDWIDTH;
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        LogUtils.d("MEDIA_INFO_NOT_SEEKABLE");
                        event = Event.EVENT_ON_NOT_SEEK_ABLE;
                        break;
                    case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
                        LogUtils.d("MEDIA_INFO_OPEN_INPUT");
                        break;
                    case IMediaPlayer.MEDIA_INFO_STARTED_AS_NEXT:
                        LogUtils.d("MEDIA_INFO_STARTED_AS_NEXT");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        LogUtils.d("MEDIA_INFO_SUBTITLE_TIMED_OUT");
                        event = Event.EVENT_ON_SUBTITLE_TIMED_OUT;
                        break;
                    case IMediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR:
                        LogUtils.d("MEDIA_INFO_TIMED_TEXT_ERROR");
                        event = Event.EVENT_ON_TIMED_TEXT_ERROR;
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNKNOWN:
                        LogUtils.d("MEDIA_INFO_UNKNOWN");
                        event = Event.EVENT_ON_UNKNOWN;
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        LogUtils.d("MEDIA_INFO_UNSUPPORTED_SUBTITLE");
                        event = Event.EVENT_ON_UNSUPPORTED_SUBTITLE;
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START:
                        LogUtils.d("MEDIA_INFO_VIDEO_DECODED_START");
                        event = Event.EVENT_ON_VIDEO_DECODED_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        LogUtils.d("MEDIA_INFO_VIDEO_RENDERING_START");
                        event = Event.EVENT_ON_VIDEO_RENDER_START;
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        LogUtils.d("MEDIA_INFO_VIDEO_RENDERING_START");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:
                        LogUtils.d("MEDIA_INFO_VIDEO_SEEK_RENDERING_START");
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
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

        mMediaPlayer.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {

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
        return (int)mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        if(!available()){
            return -1;
        }
        return (int) mMediaPlayer.getDuration();
    }

    @Override
    public void setSpeed(float speed) {
        try{
            if (available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mMediaPlayer.setSpeed(speed);
            }
        }catch (Exception e){
            handleException(e,false);
        }
    }

    @Override
    public float getSpeed() {
        try{
            if(available()) {
                return mMediaPlayer.getSpeed(1.0f);
            }
        }catch (Exception e){
            handleException(e,false);
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
    public IjkMediaPlayer getPlayer() {
        return mMediaPlayer;
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

    @Override
    public void updateSurface(int width, int height) {

    }

    @Override
    public void surfaceDestroy() {

    }
}
