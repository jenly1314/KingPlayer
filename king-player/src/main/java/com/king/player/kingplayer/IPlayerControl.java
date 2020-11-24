package com.king.player.kingplayer;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface IPlayerControl {

    void    start();
    void    pause();
    int     getDuration();
    int     getCurrentPosition();
    void    seekTo(int pos);
    boolean isPlaying();
    int     getBufferPercentage();
    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();
}
