package com.king.player.app

import android.view.Surface
import com.king.player.app.base.BaseActivity
import com.king.player.ijkplayer.IjkPlayer
import com.king.player.kingplayer.KingPlayer
import com.king.player.kingplayer.source.DataSource
import com.king.player.kingplayer.util.LogUtils
import com.king.player.kingplayer.view.VideoView
import kotlinx.android.synthetic.main.video_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
open class VideoViewActivity : BaseActivity() {


//        val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"
//    val url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"

    open fun createPlayer(): KingPlayer<*> {
        return IjkPlayer(this)
    }

    override fun getTitleText(): String {
        return "VideoView"
    }

    override fun getLayoutId(): Int {
        return R.layout.video_view_activity
    }

    override fun initData() {

        videoView.player = createPlayer()
        val dataSource = DataSource(url)
        videoView.setDataSource(dataSource)

        videoView.setOnSurfaceListener(object : VideoView.OnSurfaceListener {
            override fun onSurfaceCreated(surface: Surface, width: Int, height: Int) {
                LogUtils.d("onSurfaceCreated: $width * $height")
                videoView.start()
            }

            override fun onSurfaceSizeChanged(surface: Surface, width: Int, height: Int) {
                LogUtils.d("onSurfaceSizeChanged: $width * $height")
            }

            override fun onSurfaceDestroyed(surface: Surface) {
                LogUtils.d("onSurfaceDestroyed")
            }

        })

        //缓冲更新监听
        videoView.setOnBufferingUpdateListener {
            LogUtils.d("buffering: $it")
        }
        //播放事件监听
        videoView.setOnPlayerEventListener { event, bundle ->

        }
        //错误事件监听
        videoView.setOnErrorListener { event, bundle ->

        }

    }


    override fun onPause() {
        super.onPause()
        videoView.pause()
    }


    override fun onDestroy() {
        super.onDestroy()
        videoView.release()

    }
}