package com.king.player.kingplayer;


import androidx.annotation.NonNull;



/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface IPlayer<Player> {

    // all possible internal states
     int STATE_ERROR = -1;
     int STATE_IDLE = 0;
     int STATE_PREPARING = 1;
     int STATE_PREPARED = 2;
     int STATE_PLAYING = 3;
     int STATE_PAUSED = 4;
     int STATE_STOPPED = 5;
     int STATE_PLAYBACK_COMPLETED = 6;



    void setDataSource(@NonNull DataSource dataSource);

    /**
     * 播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 释放
     */
    void release();

    /**
     * 重置
     */
    void reset();

    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying();

    /**
     * 获取播放器状态
     * @return
     */
    int getPlayerState();

    /**
     * 设置音量
     * @param volume
     */
    void setVolume(float volume);

    /**
     * 获取音量
     * @return
     */
    float getVolume();

    /**
     * 播放指定位置
     * @param msec
     */
    void seekTo(int msec);

    /**
     * 获取当前播放位置（单位：ms）
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取视频总时长（单位：ms）
     * @return
     */
    int getDuration();

    /**
     * 设置播放速率
     * @param speed
     */
    void setSpeed(float speed);

    /**
     * 获取播放速率
     * @return
     */
    float getSpeed();

    /**
     * 设置是否循环播放
     * @param looping
     */
    void setLooping(boolean looping);

    /**
     * 是否循环播放
     * @return
     */
    boolean isLopping();

    /**
     * 获取缓冲百分比
     * @return
     */
    float getBufferPercentage();


    Player getPlayer();


}
