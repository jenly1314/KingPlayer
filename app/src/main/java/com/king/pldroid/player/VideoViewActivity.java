package com.king.pldroid.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.king.base.BaseActivity;
import com.king.base.model.EventMessage;
import com.king.base.util.LogUtils;
import com.king.pldroid.player.widget.KingMediaController;
import com.king.pldroid.player.widget.MediaController;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.IMediaController;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;


/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2016/12/20
 */
public class VideoViewActivity extends BaseActivity {


    private TextView tvTitle;

    private PLVideoView mVideoView;

    private KingMediaController mMediaController;

    private AVOptions mAVOptions;

    private String mVideoPath = MediaPlayerActivity.DEFAULT_TEST_URL;

    /**
     * 显示比例
     */
    private int mDisplayAspectRatio;

    /**
     * 屏幕角度
     */
    private int mOrientation;

    private boolean isPortrait;


    @Override
    public void initUI() {
        setContentView(R.layout.activity_video_view);

        tvTitle = findView(R.id.tvTitle);

        mVideoView = findView(R.id.videoView);


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


        mVideoView.setAVOptions(mAVOptions);

        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setVideoPath(mVideoPath);

        mMediaController = new KingMediaController(context);
        mMediaController.setClickZoomListener(onClickZoomListener);
        mMediaController.setClickRatioListener(onClickRatioListener);

        mVideoView.setMediaController(mMediaController);

        isPortrait = getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }


    public View.OnClickListener onClickZoomListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            clickRotation();
        }
    };

    public View.OnClickListener onClickRatioListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switchDisplayAspectRatio();
        }
    };

    public void switchDisplayAspectRatio(){
        mDisplayAspectRatio = (mVideoView.getDisplayAspectRatio()+1)%5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mDisplayAspectRatio){
            case PLVideoView.ASPECT_RATIO_ORIGIN:
                showToast("Origin mode.");
                break;
            case PLVideoView.ASPECT_RATIO_FIT_PARENT:
                showToast("Fit parent.");
                break;
            case PLVideoView.ASPECT_RATIO_PAVED_PARENT:
                showToast("Paved parent.");
                break;
            case PLVideoView.ASPECT_RATIO_4_3:
                showToast("4 : 3.");
                break;
            case PLVideoView.ASPECT_RATIO_16_9:
                showToast("16 : 9.");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!isPortrait){
                clickRotation();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void clickRotation(){
        mOrientation = getRequestedOrientation();

        if(mOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation != Configuration.ORIENTATION_LANDSCAPE){
            isPortrait = true;
            tvTitle.setVisibility(View.VISIBLE);
        }else{
            isPortrait = false;
            tvTitle.setVisibility(View.GONE);
        }
        LogUtils.e("isPortrait:" +isPortrait);
        mMediaController.updateZoomButton(isPortrait);

    }

    @Override
    public void addListeners() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy");
        mVideoView.stopPlayback();
    }


    public void test(){

    }

    //------------------------------


//    private PLMediaPlayer.OnPreparedListener mOnPreparedListener = new PLMediaPlayer.OnPreparedListener() {
//        @Override
//        public void onPrepared(PLMediaPlayer plMediaPlayer) {
//            LogUtils.d("onPrepared");
//            plMediaPlayer.start();
//            isStoppedPlay = false;
//
//        }
//    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            LogUtils.d("onVideoSizeChanged, width=" + width + ",height=" + height);

//            if(width!=0 && height!=0){
//
//                float ratioWidth = (float) width/(float)mSurfaceWidth;
//                float ratioHeight = (float)height/(float)mSurfaceHeight;
//                float ratio = Math.max(ratioWidth,ratioHeight);
//
//                width = (int)Math.ceil(width/ratio);
//                height = (int)Math.ceil(height/ratio);
//
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,height);
//                params.gravity = Gravity.CENTER;
//                mSurfaceView.setLayoutParams(params);
//
//            }

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

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
//            LogUtils.i("OnInfo, what = " + what + ", extra = " + extra);
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

    @Override
    public void initData() {

    }

    @Override
    public void onEventMessage(EventMessage em) {

    }



}
