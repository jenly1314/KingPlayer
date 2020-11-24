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
        public static final String KEY_BUFFERING_PERCENT = "event_buffering_percent";
        public static final String KEY_LENGTH = "event_length";
        public static final String KEY_VIDEO_WIDTH = "event_video_width";
        public static final String KEY_VIDEO_HEIGHT = "event_height";
        public static final String KEY_EXTRA = "event_extra";
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorEvent{

        int ERROR_EVENT_DATA_PROVIDER_ERROR = 0x9000;

        //A error that causes a play to terminate
        int ERROR_EVENT_COMMON = 0x9011;

        int ERROR_EVENT_UNKNOWN = 0x9012;

        int ERROR_EVENT_SERVER_DIED = 0x9013;

        int ERROR_EVENT_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 0x9014;

        int ERROR_EVENT_IO = 0x9015;

        int ERROR_EVENT_MALFORMED = 0x9016;

        int ERROR_EVENT_UNSUPPORTED = 0x9017;

        int ERROR_EVENT_TIMED_OUT = 0x9018;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {

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

//        /**
//         * when you call {@link IPlayer#resume()}
//         */
//        int EVENT_ON_RESUME = 0x1006;

        /**
         * when you call {@link IPlayer#stop()}
         */
        int EVENT_ON_STOP = 0x1007;

        /**
         * when you call {@link IPlayer#release()}
         */
        int EVENT_ON_RELEASE = 0x1008;


        /**
         * when you call {@link IPlayer#reset()}
         */
        int EVENT_ON_RESET = 0x1009;

        /**
         * when decoder start buffering stream
         */
        int EVENT_ON_BUFFERING_START = 0x1010;

        /**
         * when decoder buffering stream end
         */
        int EVENT_ON_BUFFERING_END = 0x1011;

        /**
         * when decoder buffering percentage update
         */
        int EVENT_ON_BUFFERING_UPDATE = 0x1012;

        /**
         * when you call {@link IPlayer#seekTo(int)}
         */
        int EVENT_ON_SEEK_TO = 0x1013;

        /**
         * when seek complete
         */
        int EVENT_ON_SEEK_COMPLETE = 0x1014;

        /**
         * when player start render video stream
         */
        int EVENT_ON_VIDEO_RENDER_START = 0x1015;

        /**
         * when play complete
         */
        int EVENT_ON_PLAY_COMPLETE = 0x1016;

        /**
         * on video size change
         */
        int EVENT_ON_VIDEO_SIZE_CHANGE = 0x1017;

        /**
         * on decoder prepared
         */
        int EVENT_ON_PREPARED = 0x1018;

        /**
         * on player timer counter update
         * if timer stopped, you could not receive this event code.
         */
        int EVENT_ON_TIMER_UPDATE = 0x1019;

        /**
         * on get video rotation.
         */
        int EVENT_ON_VIDEO_ROTATION_CHANGED = 99020;

        /**
         * when player start render audio stream
         */
        int EVENT_ON_AUDIO_RENDER_START = 0x1021;

        /**
         * when audio decoder start
         */
        int EVENT_ON_AUDIO_DECODER_START = 0x1022;

        /**
         * when audio seek rendering start
         */
        int EVENT_ON_AUDIO_SEEK_RENDERING_START = 0x1023;

        /**
         * network bandwidth
         */
        int EVENT_ON_NETWORK_BANDWIDTH = 0x1024;

        /**
         * bad interleaving
         */
        int EVENT_ON_BAD_INTERLEAVING = 0x1025;

        /**
         * not support seek ,may be live.
         */
        int EVENT_ON_NOT_SEEK_ABLE = 0x1026;

        /**
         * on meta data update
         */
        int EVENT_ON_METADATA_UPDATE = 0x1027;

        /**
         * Failed to handle timed text track properly.
         */
        int EVENT_ON_TIMED_TEXT_ERROR = 0x1028;

        /**
         * Subtitle track was not supported by the media framework.
         */
        int EVENT_ON_UNSUPPORTED_SUBTITLE = 0x1029;

        /**
         * Reading the subtitle track takes too long.
         */
        int EVENT_ON_SUBTITLE_TIMED_OUT = 0x1030;

        /**
         * on play status update
         */
        int EVENT_ON_STATUS_CHANGE = 0x1031;


        /**
         * if you set data provider for player, call back this method when provider start load data.
         */
        int EVENT_ON_PROVIDER_DATA_START = 0x1050;

        /**
         * call back this method when provider load data success.
         */
        int EVENT_ON_PROVIDER_DATA_SUCCESS = 0x1051;

        /**
         * call back this method when provider load data error.
         */
        int EVENT_ON_PROVIDER_DATA_ERROR = 0x1052;

    }

}
