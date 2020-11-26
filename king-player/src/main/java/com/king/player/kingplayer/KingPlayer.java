package com.king.player.kingplayer;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.king.player.kingplayer.util.LogUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public abstract class KingPlayer<Player> implements IPlayer<Player>, ISurface {

    protected int mCurrentState;

    protected int mTargetState;

    private int mBufferPercentage;

    private int mVideoWidth;

    private int mVideoHeight;

    private boolean isLopping;

    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private OnBufferingUpdateListener mOnBufferingUpdateListener;

    private OnPlayerEventListener mOnPlayerEventListener;

    private OnErrorListener mOnErrorListener;


    @Override
    public int getPlayerState() {
        return mCurrentState;
    }

    @Override
    public int getVideoWidth() {
        return mVideoWidth;
    }

    @Override
    public int getVideoHeight() {
        return mVideoHeight;
    }

    @Override
    public void setLooping(boolean looping) {
        this.isLopping = looping;
    }

    @Override
    public boolean isLopping() {
        return isLopping;
    }

    @Override
    public float getBufferPercentage() {
        return mBufferPercentage;
    }

    protected void handleException(Exception e, boolean isReset){
        if(e != null){
            LogUtils.e(e);
        }
        sendErrorEvent(ErrorEvent.ERROR_EVENT_EXCEPTION);
        if(isReset){
            reset();
        }
    }


    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener){
        mOnVideoSizeChangedListener = listener;
    }

    protected final void sendVideoSizeChangeEvent(int videoWidth,int videoHeight){
        if(mOnVideoSizeChangedListener != null){
            mOnVideoSizeChangedListener.onVideoSizeChanged(videoWidth,videoHeight);
        }
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        Bundle bundle = obtainBundle();
        bundle.putInt(EventBundleKey.KEY_VIDEO_WIDTH,videoWidth);
        bundle.putInt(EventBundleKey.KEY_VIDEO_HEIGHT,videoHeight);
        sendPlayerEvent(Event.EVENT_ON_VIDEO_SIZE_CHANGE,bundle);
    }

    public void setOnPlayerEventListener(OnPlayerEventListener listener){
        mOnPlayerEventListener = listener;
    }

    protected final void sendPlayerEvent(@Event int event){
        sendPlayerEvent(event,null);
    }

    protected final void sendPlayerEvent(@Event int event, @Nullable Bundle bundle){
        if(mOnPlayerEventListener != null){
            mOnPlayerEventListener.onEvent(event,bundle);
        }
    }

    public interface OnErrorListener{
        void onErrorEvent(@ErrorEvent int event,@Nullable Bundle bundle);
    }

    public void setOnErrorListener(OnErrorListener listener){
        this.mOnErrorListener = listener;
    }


    protected final void sendErrorEvent(@ErrorEvent int event){
        sendPlayerEvent(event,null);
    }

    protected final void sendErrorEvent(@ErrorEvent int event,@Nullable Bundle bundle){
        if(mOnErrorListener != null) {
            mOnErrorListener.onErrorEvent(event,bundle);
        }
    }

    public interface OnBufferingUpdateListener{
        void onBufferingUpdate(int percent);
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener){
        this.mOnBufferingUpdateListener = listener;
    }

    protected final void sendBufferingUpdateEvent(int percent){
        mBufferPercentage = percent;
        if(mOnBufferingUpdateListener != null){
            mOnBufferingUpdateListener.onBufferingUpdate(percent);
        }
    }

    public interface OnVideoSizeChangedListener{
        void onVideoSizeChanged(int videoWidth,int videoHeight);
    }

    public interface OnPlayerEventListener{
        /**
         *
         * @param event {@link }
         * @param bundle
         */
        void onEvent(@Event int event, Bundle bundle);
    }

    public static Bundle obtainBundle(){
        return new Bundle();
    }

    public final static class EventBundleKey{
        public static final String KEY_TIME = "event_time";
        public static final String KEY_POSITION = "event_position";
        public static final String KEY_VIDEO_WIDTH = "event_video_width";
        public static final String KEY_VIDEO_HEIGHT = "event_height";
        public static final String KEY_ORIGINAL_EVENT = "event_original_event";
        public static final String KEY_ORIGINAL_EXTRA = "event_original_extra";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorEvent{

        int ERROR_EVENT_COMMON = 0x9000;

        int ERROR_EVENT_UNKNOWN = 0x9001;

        int ERROR_EVENT_SERVER_DIED = 0x9002;

        int ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 0x9003;

        int ERROR_EVENT_IO = 0x9004;

        int ERROR_EVENT_MALFORMED = 0x9005;

        int ERROR_EVENT_UNSUPPORTED = 0x9006;

        int ERROR_EVENT_TIMED_OUT = 0x9007;

        int ERROR_EVENT_EXCEPTION = 0x9008;
    }


    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {

        int EVENT_ON_COMMON = 0x1000;

        /**
         * when decoder set data source
         */
        int EVENT_ON_DATA_SOURCE_SET = 0x1001;

        /**
         * when surface holder update
         */
        int EVENT_ON_SURFACE_HOLDER_UPDATE = 0x1002;

        /**
         * when surface update
         */
        int EVENT_ON_SURFACE_UPDATE = 0x1003;

        /**
         * when you call {@link IPlayer#start()}
         */
        int EVENT_ON_START = 0x1004;

        /**
         * when you call {@link IPlayer#pause()}
         */
        int EVENT_ON_PAUSE = 0x1005;

        /**
         * when you call {@link IPlayer#stop()}
         */
        int EVENT_ON_STOP = 0x1006;

        /**
         * when you call {@link IPlayer#release()}
         */
        int EVENT_ON_RELEASE = 0x1007;

        /**
         * when you call {@link IPlayer#reset()}
         */
        int EVENT_ON_RESET = 0x1008;

        /**
         * when decoder start buffering stream
         */
        int EVENT_ON_BUFFERING_START = 0x1009;

        /**
         * when decoder buffering stream end
         */
        int EVENT_ON_BUFFERING_END = 0x1010;


        /**
         * when you call {@link IPlayer#seekTo(int)}
         */
        int EVENT_ON_SEEK_TO = 0x1011;

        /**
         * when seek complete
         */
        int EVENT_ON_SEEK_COMPLETE = 0x1012;

        /**
         * when player start render video stream
         */
        int EVENT_ON_VIDEO_RENDER_START = 0x1013;

        /**
         * when play complete
         */
        int EVENT_ON_PLAY_COMPLETE = 0x1014;

        /**
         * on video size change
         */
        int EVENT_ON_VIDEO_SIZE_CHANGE = 0x1015;

        /**
         * on decoder prepared
         */
        int EVENT_ON_PREPARED = 0x1016;

        /**
         * on player timer counter update
         * if timer stopped, you could not receive this event code.
         */
        int EVENT_ON_TIMER_UPDATE = 0x1017;

        /**
         * on get video rotation.
         */
        int EVENT_ON_VIDEO_ROTATION_CHANGED = 0x1018;

        /**
         * when player start render audio stream
         */
        int EVENT_ON_AUDIO_RENDER_START = 0x1019;

        /**
         * when audio decoder start
         */
        int EVENT_ON_AUDIO_DECODER_START = 0x1020;

        /**
         * when audio seek rendering start
         */
        int EVENT_ON_AUDIO_SEEK_RENDERING_START = 0x1021;

        /**
         * network bandwidth
         */
        int EVENT_ON_NETWORK_BANDWIDTH = 0x1022;

        /**
         * bad interleaving
         */
        int EVENT_ON_BAD_INTERLEAVING = 0x1023;

        /**
         * not support seek ,may be live.
         */
        int EVENT_ON_NOT_SEEK_ABLE = 0x1024;

        /**
         * on meta data update
         */
        int EVENT_ON_METADATA_UPDATE = 0x1025;

        /**
         * Failed to handle timed text track properly.
         */
        int EVENT_ON_TIMED_TEXT_ERROR = 0x1026;

        /**
         * Subtitle track was not supported by the media framework.
         */
        int EVENT_ON_UNSUPPORTED_SUBTITLE = 0x1027;

        /**
         * Reading the subtitle track takes too long.
         */
        int EVENT_ON_SUBTITLE_TIMED_OUT = 0x1028;

        /**
         * on play status update
         */
        int EVENT_ON_STATUS_CHANGE = 0x1029;

        int EVENT_ON_UNKNOWN = 0x1030;

        int EVENT_ON_VIDEO_DECODED_START = 0x1031;

        int EVENT_ON_COMPONENT_OPEN = 0x1032;

    }

}
