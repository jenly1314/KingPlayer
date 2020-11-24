package com.king.player.app

import android.net.Uri
import android.os.Bundle
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.king.player.kingplayer.DataSource
import com.king.player.kingplayer.media.SysPlayer
import com.king.player.kingplayer.view.VideoView
import com.king.player.vlcplayer.VlcPlayer
import kotlinx.android.synthetic.main.video_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class VideoViewActivity : AppCompatActivity() {


    val TAG = "Jenly"

    //    val url = "rtsp://192.168.100.212:554/realtimevideo/0"
//    val url = "rtsp://192.168.100.208:554/realtimevideo/0"
//    val url = "rtsp://admin:123456@192.168.100.212:554/realtimevideo/0"
//    val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_view_activity)
        videoView.player = SysPlayer(this)
//        videoView.player = VlcPlayer(this)
        videoView.setOnSurfaceListener(object: VideoView.OnSurfaceListener{
            override fun onSurfaceCreated(surface: Surface, width: Int, height: Int) {
            }

            override fun onSurfaceSizeChanged(surface: Surface, width: Int, height: Int) {

            }

            override fun onSurfaceDestroyed(surface: Surface) {

            }

        })

        val dataSource = DataSource(url)
        videoView.setDataSource(dataSource)


//        videoView.setOnBufferingUpdateListener { mp, percent ->
//            Log.d(TAG, "percent:${percent}")
//        }
//
//        videoView.setOnCompletionListener {
//            Log.d(TAG, "onCompleted")
//        }
//
//        videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
//            Log.d(TAG, "OnError -> what:${what}|extra:${extra}")
//            true
//        })
//
//        videoView.setOnInfoListener { mp, what, extra ->
//            Log.d(TAG, "OnInfo -> what:${what}|extra:${extra}")
//            false
//        }

//        surfaceView.holder.addCallback(object : SurfaceHolder.Callback{
//            override fun surfaceCreated(holder: SurfaceHolder) {
//                videoView.setDisplay(holder)
//                videoView.play("rtsp://admin:123456@192.168.100.212:554/realtimevideo/0")
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

//        videoView.setEventListener {
//            val buffer = ByteBuffer.allocate(4)
//            buffer.putInt(it.type)
//            Log.d(TAG, "type:${HexUtils.toHexString(buffer.array())}")
//            Log.d(TAG, "isPlaying:${videoView.isPlaying}")
//        }
    }

    override fun onStart() {
        super.onStart()
//        mMediaPlayer.play(uri);
//        val media: Media = Media(videoView.mediaPlayer.libVLC, Uri.parse(url))
//        media.addOption(":rtsp-user=icsadmin")
//        media.addOption(":rtsp-pwd=123456")

//        videoView.start()
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }


    override fun onPause() {
        super.onPause()
        videoView.pause()
    }


    override fun onStop() {
        super.onStop()
        videoView.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()

    }
}