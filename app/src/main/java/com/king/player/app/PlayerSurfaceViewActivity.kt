package com.king.player.app

import android.view.SurfaceHolder
import com.king.player.app.base.BaseActivity
import com.king.player.exoplayer.ExoPlayer
import com.king.player.ijkplayer.IjkPlayer
import com.king.player.kingplayer.source.DataSource
import com.king.player.kingplayer.util.LogUtils
import com.king.player.vlcplayer.VlcPlayer
import kotlinx.android.synthetic.main.player_surface_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class PlayerSurfaceViewActivity: BaseActivity() {

    val player by lazy { IjkPlayer(this) }
//    val player by lazy { ExoPlayer(this) }
//    val player by lazy { VlcPlayer(this) }
//    val player by lazy { SysPlayer(this) }

    //    val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"
//    val url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"


    override fun getTitleText(): String {
        return "SurfaceView"
    }

    override fun getLayoutId(): Int {
        return R.layout.player_surface_view_activity
    }

    override fun initData() {
        //视频大小改变监听
        player.setOnVideoSizeChangedListener { videoWidth, videoHeight ->
            surfaceView.setVideoSize(videoWidth,videoHeight)
        }

        //缓冲更新监听
        player.setOnBufferingUpdateListener {
            LogUtils.d("buffering: $it")
        }
        //播放事件监听
        player.setOnPlayerEventListener { event, bundle ->

        }
        //错误事件监听
        player.setOnErrorListener { event, bundle ->

        }

        val dataSource = DataSource(url)
        player.setDataSource(dataSource)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                LogUtils.d("surfaceCreated")
                player.setSurface(holder)
                player.updateSurface(surfaceView.width,surfaceView.height)

                player.start()

            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                LogUtils.d("surfaceChanged: $width * $height")
                player.updateSurface(surfaceView.width,surfaceView.height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                LogUtils.d("surfaceDestroyed")
                player.surfaceDestroy()
            }

        })
    }



    override fun onPause() {
        super.onPause()
        player.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()

    }
}

