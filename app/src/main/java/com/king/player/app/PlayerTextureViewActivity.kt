package com.king.player.app

import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import com.king.player.kingplayer.DataSource
import com.king.player.kingplayer.media.SysPlayer
import com.king.player.kingplayer.util.LogUtils
import com.king.player.vlcplayer.VlcPlayer
import kotlinx.android.synthetic.main.player_texture_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class PlayerTextureViewActivity: AppCompatActivity() {

    val player by lazy { VlcPlayer(this) }
//    val player by lazy { SysPlayer(this) }

    var isCreate = false

    val TAG = "Jenly"

//    val url = "rtsp://192.168.100.221:554/realtimevideo/0"
//    val url = "rtsp://192.168.100.208:554/realtimevideo/0"
//    val url = "rtsp://admin:123456@192.168.100.212:554/realtimevideo/0"
//    val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"
//    val url = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_texture_view_activity)

//        player.setOnBufferingUpdateListener { mp, percent ->
//            Log.d(TAG, "percent:${percent}")
//        }
//
//        player.setOnCompletionListener {
//            Log.d(TAG, "onCompleted")
//        }
//
//        player.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
//            Log.d(TAG, "OnError -> what:${what}|extra:${extra}")
//            true
//        })
//
//        player.setOnInfoListener { mp, what, extra ->
//            Log.d(TAG, "OnInfo -> what:${what}|extra:${extra}")
//            false
//        }

//        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                player.setDisplay(holder)
//                player.play("rtsp://admin:123456@192.168.100.212:554/realtimevideo/0")
//                isCreate = true
//            }
//
//            override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun surfaceDestroyed(holder: SurfaceHolder) {
//
//            }
//
//        })

        btnRecord.setOnClickListener {
            val path = Environment.getExternalStorageDirectory().absolutePath + "/Download"
//            val result = player.record(path)
//            Log.d("Jenly", "record:" + result)
        }

        player.setOnVideoSizeChangedListener { videoWidth, videoHeight ->
            LogUtils.d("VideoSizeChanged:${videoWidth} * $videoHeight")
            textureView.setVideoSize(videoWidth,videoHeight)
        }

        textureView.surfaceTextureListener = object: TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
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
                player.updateSurface(width,height)
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                player.surfaceDestroy()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

            }

        }


//        player.setVideoSurface(textureView)

//        val dataSource = DataSource(url)
//        val dataSource = DataSource(Uri.parse(url))
//        dataSource.options.add(":rtsp-user=icsadmin")
//        dataSource.options.add(":rtsp-pwd=123456")
//        player.setDataSource(dataSource)
//        player.setEventListener {
//            val buffer = ByteBuffer.allocate(4)
//            buffer.putInt(it.type)
//            Log.d(TAG, "type:${HexUtils.toHexString(buffer.array())}")
//            Log.d(TAG, "isPlaying:${player.isPlaying}")
//        }
    }

    override fun onStart() {
        super.onStart()
//        mMediaPlayer.play(uri);
//        val media: Media = Media(player.mediaPlayer.libVLC, Uri.parse(url))
//        media.addOption(":rtsp-user=icsadmin")
//        media.addOption(":rtsp-pwd=123456")


//        player.play()
    }

    override fun onResume() {
        super.onResume()
        player.start()
//        val path = Environment.getExternalStorageDirectory().absolutePath + "/Download/1.mp4"
//        player.record(path)
    }


    override fun onPause() {
        super.onPause()
        player.pause()
    }


    override fun onStop() {
        super.onStop()
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()

    }
}

