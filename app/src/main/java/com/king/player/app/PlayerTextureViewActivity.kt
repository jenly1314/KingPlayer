package com.king.player.app

import android.graphics.SurfaceTexture
import android.view.TextureView
import com.king.player.app.base.BaseActivity
import com.king.player.exoplayer.ExoPlayer
import com.king.player.kingplayer.source.DataSource
import com.king.player.kingplayer.util.LogUtils
import kotlinx.android.synthetic.main.player_texture_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class PlayerTextureViewActivity: BaseActivity() {

//    val player by lazy { IjkPlayer(this) }
    val player by lazy { ExoPlayer(this) }
//    val player by lazy { VlcPlayer(this) }
//    val player by lazy { SysPlayer(this) }

//    val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"
//    val url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"


    override fun getTitleText(): String {
        return "TextureView"
    }

    override fun getLayoutId(): Int {
        return R.layout.player_texture_view_activity
    }

    override fun initData() {

        //视频大小改变监听
        player.setOnVideoSizeChangedListener { videoWidth, videoHeight ->
            LogUtils.d("VideoSizeChanged:${videoWidth} * $videoHeight")
            textureView.setVideoSize(videoWidth,videoHeight)
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

        textureView.surfaceTextureListener = object: TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                LogUtils.d("onSurfaceTextureAvailable: $width * $height")
                player.setSurface(surface)
                player.updateSurface(width,height)

                val dataSource = DataSource(url)
                player.setDataSource(dataSource)
                player.start()
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                LogUtils.d("onSurfaceTextureSizeChanged: $width * $height")
                player.updateSurface(width,height)
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                LogUtils.d("onSurfaceTextureDestroyed")
                player.surfaceDestroy()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

            }

        }

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

