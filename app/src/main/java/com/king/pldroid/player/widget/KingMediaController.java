package com.king.pldroid.player.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.king.base.util.LogUtils;
import com.king.pldroid.player.R;
import com.pili.pldroid.player.IMediaController;

import java.util.Locale;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2016/12/19
 */
public class KingMediaController extends FrameLayout implements IMediaController {

    private IMediaController.MediaPlayerControl mPlayer;

    private Context context;

    private static final int SEEK_TO_POST_DELAY_MILLIS = 200;

    /**
     * 默认消失时间
     */
    private static int sDefaultTimeOut = 3000;

    private static final int FADE_OUT = 0X01;

    private static final int SHOW_PROGRESS = 0X02;

    private View mRootView;

    private View mAnchorView;

    private ImageButton ibPlay,ibZoom,ibRatio;

    private TextView tvCurrentTime,tvEndTime;

    private SeekBar seekBar;

    private PopupWindow mPopupWindow;

    private AudioManager mAudioManager;

    private int mAnimStyle;

    private long mDuration;

    private long mPosition;

    /**
     * 控制器是否显示
     */
    private boolean mIsShow;

    /**
     * 是否拖动进度条
     */
    private boolean mIsDragging;

    private boolean mIsInstantSeeking;

    private Runnable mLastSeekBarRunnable;

    private boolean isPortrait;



    public KingMediaController(Context context) {
        super(context);
        init(context);
    }


    public void setClickRatioListener(OnClickListener onClickListener){
        ibRatio.setOnClickListener(onClickListener);
    }

    public void setClickZoomListener(OnClickListener onClickListener){
        ibZoom.setOnClickListener(onClickListener);
    }

    public void updateZoomButton(boolean isPortrait){
        this.isPortrait = isPortrait;
        LogUtils.d("isPortrait:"  + isPortrait);
        updateZoomButton();
    }

    private void updateZoomButton(){


        if(isPortrait){
            ibZoom.setImageResource(R.drawable.ic_zoom_in);
        } else {
            ibZoom.setImageResource(R.drawable.ic_zoom_out);
        }
    }



    private void init(Context context){
        this.context = context.getApplicationContext();


        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mIsInstantSeeking = true;

        mRootView = makeControllerView();

        ibPlay = findRootViewById(R.id.ibPlay);
        ibPlay.setOnClickListener(onClickPauseListener);
        ibZoom = findRootViewById(R.id.ibZoom);
        ibRatio = findRootViewById(R.id.ibRatio);
        seekBar = findRootViewById(R.id.seekBar);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListtener);
        tvCurrentTime = findRootViewById(R.id.tvCurrentTime);
        tvEndTime = findRootViewById(R.id.tvEndTime);

        initPopupwindow();

    }


    public <T extends View> T findRootViewById(@IdRes int id){
        return (T)mRootView.findViewById(id);
    }


    private void initPopupwindow(){
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(null);
        mPopupWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
        mPopupWindow.setContentView(mRootView);
        mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void setMediaPlayer(MediaPlayerControl mediaPlayerControl) {
        mPlayer = mediaPlayerControl;
        updatePasuePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging
     * True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mIsInstantSeeking = seekWhenDragging;
    }

    public void updatePasuePlay(){
        if(mRootView == null || mPlayer == null)
            return;

        if(mPlayer.isPlaying()){
            ibPlay.setImageResource(R.drawable.ic_pause);
        }else{
            ibPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying())
            mPlayer.pause();
        else
            mPlayer.start();
        updatePasuePlay();
    }

    public View makeControllerView(){
        return  ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bottom_controller,this,false);
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds).toString();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeOut);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeOut);
        return false;
    }

    private OnClickListener onClickPauseListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            doPauseResume();
        }
    };


    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListtener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {

            if(fromuser){
                long newPosition = mDuration * progress / 1000L;

                if(mIsInstantSeeking){
                    postDelayedSeekTo(newPosition);
                }

                String time = generateTime(newPosition);
                tvCurrentTime.setText(time);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsDragging = true;
            show(60000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mIsInstantSeeking)
                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            show(sDefaultTimeOut);
            mHandler.removeMessages(SHOW_PROGRESS);
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS,1000);
            mIsDragging = false;
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
    };

    private void postDelayedSeekTo(final long position){
        mHandler.removeCallbacks(mLastSeekBarRunnable);
        mLastSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                mPlayer.seekTo(position);
            }
        };
        mHandler.postDelayed(mLastSeekBarRunnable,SEEK_TO_POST_DELAY_MILLIS);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:

                    updateProgress();
                    if(!mIsDragging && mIsShow){
                        sendEmptyMessageDelayed(SHOW_PROGRESS,1000-(mPosition % 1000));
                    }
                    updatePasuePlay();
                    break;

                default:
                    break;
            }
        }
    };

    private void updateProgress(){

        mPosition = mPlayer.getCurrentPosition();
        mDuration = mPlayer.getDuration();

        if(mDuration>0){
            long pos = 1000L * mPosition / mDuration;
            seekBar.setProgress((int)pos);
        }

        tvCurrentTime.setText(generateTime(mPosition));
        tvEndTime.setText(generateTime(mDuration));

    }


    @Override
    public void show() {
        show(sDefaultTimeOut);
    }

    @Override
    public void show(int timeout) {

        if(!mIsShow){

            int[] location = new int[2];

            if(mAnchorView != null){
                mAnchorView.getLocationOnScreen(location);
                showPopuwindow(mAnchorView,location);
            }else{
                showPopuwindow(mRootView,location);
            }

            mIsShow = true;
        }
        updatePasuePlay();

        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if(timeout != 0){
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendEmptyMessageDelayed(FADE_OUT,timeout);
        }


    }

    private void showPopuwindow(View parent,int[] location){
        Rect rect = new Rect(location[0],location[1],location[0] + parent.getWidth(),location[1] + parent.getHeight());
        mPopupWindow.setAnimationStyle(mAnimStyle);
        mPopupWindow.setContentView(mRootView);
        mPopupWindow.showAtLocation(parent, Gravity.BOTTOM,rect.left,0);
    }

    @Override
    public void hide() {
        if(mIsShow){
            mHandler.removeMessages(SHOW_PROGRESS);

            if(mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }
        }

        mIsShow = false;
    }

    @Override
    public boolean isShowing() {
        return mIsShow;
    }

    @Override
    public void setAnchorView(View view) {
        mAnchorView = view;
        if(mAnchorView == null){
            sDefaultTimeOut = 0;
        }

        removeAllViews();

    }
}
