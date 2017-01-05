package com.king.pldroid.player;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.king.base.BaseActivity;
import com.king.base.model.EventMessage;
import com.king.base.util.LogUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

import java.io.IOException;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since  2016/12/16
 */
public class MediaPlayerActivity extends BaseActivity implements View.OnClickListener {

    public static final String DEFAULT_TEST_URL = "rtmp://live.hkstv.hk.lxdns.com/live/hks";

    private PLMediaPlayer mMediaPlayer;

    private AVOptions mAVOptions;

    private SurfaceView mSurfaceView;

    private ImageButton ibPlay;

    private ImageButton ibZoom;

    private SeekBar seekBar;

    private TextView tvCurrentTime,tvEndTime;

    private String mVideoPath = DEFAULT_TEST_URL;

    private int mSurfaceWidth,mSurfaceHeight;

    private int mOrientation;

    /**
     * Activity生命周期是否Pause
     */
    private boolean isActivityPause;

    /**
     * 视频播放是否是已经停止状态
     */
    private boolean isStoppedPlay;

    public MediaPlayerActivity() {
    }


    @Override
    public void initUI() {

        setContentView(R.layout.activity_media_player);

        mSurfaceView = findView(R.id.surfaceView);

        ibPlay = findView(R.id.ibPlay);
        ibZoom = findView(R.id.ibZoom);
        tvCurrentTime = findView(R.id.tvCurrentTime);
        tvEndTime = findView(R.id.tvEndTime);
        seekBar = findView(R.id.seekBar);



        mAVOptions = new AVOptions();

        // the unit of timeout is ms
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1

        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, 0);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = getIntent().getIntExtra("mediaCodec", 0);
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // whether start play automatically after prepared, default value is 1
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }


    private void mediaPlayerPrepare(){

        if(mMediaPlayer!=null){
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            return;
        }

        try {
            mMediaPlayer = new PLMediaPlayer(context,mAVOptions);
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepareAsync();
        } catch (UnsatisfiedLinkError e) {
            LogUtils.e(e);
        } catch (IOException e) {
            LogUtils.e(e);
        } catch (Exception e){
            LogUtils.e(e);
        }

    }

    //------------------------------


    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer) {
            LogUtils.d("onPrepared");
             plMediaPlayer.start();
            isStoppedPlay = false;

        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            LogUtils.d("onVideoSizeChanged, width=" + width + ",height=" + height);

            if(width!=0 && height!=0){

                float ratioWidth = (float) width/(float)mSurfaceWidth;
                float ratioHeight = (float)height/(float)mSurfaceHeight;
                float ratio = Math.max(ratioWidth,ratioHeight);

                width = (int)Math.ceil(width/ratio);
                height = (int)Math.ceil(height/ratio);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
                params.gravity = Gravity.CENTER;
                mSurfaceView.setLayoutParams(params);

            }

        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int percent) {
            LogUtils.i("onBufferingUpdate: " + percent + "%");
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer mp, int what, int extra) {
            LogUtils.i("OnInfo, what = " + what + ", extra = " + extra);
            switch (what){
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START://buffering

                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:

                    break;
            }

            return true;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            LogUtils.e("Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToast("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToast("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToast("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToast("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToast("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToast("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToast("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToast("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToast("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToast("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToast("unknown error !");
                    break;
            }
            // TODO: pls handle the error status here, reconnect or call finish()
//            release();
            if (isNeedReconnect) {
//                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };


    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            LogUtils.d("onCompletion");
        }
    };


    public void showToastOnUiThread(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(text);
            }
        });
    }


    //------------------------------

    @Override
    public void addListeners() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                mediaPlayerPrepare();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceWidth = width;
                mSurfaceHeight = height;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });

        ibPlay.setOnClickListener(this);
        ibZoom.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void initData() {

    }

    //------------------------------


    private void play(){

        if(isStoppedPlay){
            mediaPlayerPrepare();
        }else if(mMediaPlayer!=null){
            mMediaPlayer.start();
        }
    }

    private void pause(){
        if(mMediaPlayer!=null){
            mMediaPlayer.pause();
        }
    }

    private void stop(){
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        isStoppedPlay = true;
        mMediaPlayer = null;
    }


    private void release(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
        }
    }

    private boolean isPlaying(){
        return mMediaPlayer!=null && mMediaPlayer.isPlaying();
    }

    //------------------------------


    @Override
    protected void onPause() {
        super.onPause();
        isActivityPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
    }

    @Override
    public void onEventMessage(EventMessage em) {

    }

    //------------------------------

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void clickPlay(){
        if(isPlaying()){
            ibPlay.setImageResource(R.drawable.ic_play);
            pause();
        }else{
            ibPlay.setImageResource(R.drawable.ic_pause);
            play();
        }
    }

    private void clickRotation(){
        mOrientation = getRequestedOrientation();
        if(mOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


    }

    //------------------------------


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibPlay:
                clickPlay();
                break;
            case R.id.ibZoom:
                clickRotation();
                break;
        }
    }
}
