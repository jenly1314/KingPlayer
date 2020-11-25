package com.king.player.app

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.king.player.exoplayer.ExoPlayer
import com.king.player.kingplayer.source.DataSource
import kotlinx.android.synthetic.main.player_surface_view_activity.*

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class PlayerSurfaceViewActivity: AppCompatActivity() {

//    val player by lazy { IjkPlayer(this) }
    val player by lazy { ExoPlayer(this) }
//    val player by lazy { VlcPlayer(this) }
//    val player by lazy { SysPlayer(this) }

    var isCreate = false

    val TAG = "Jenly"

//    val url = "rtsp://192.168.100.212:554/realtimevideo/0"
//    val url = "rtsp://192.168.100.208:554/realtimevideo/0"
//    val url = "rtsp://admin:123456@192.168.100.212:554/realtimevideo/0"
//    val url = "rtmp://58.200.131.2:1935/livetv/hunantv"
    val url = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_surface_view_activity)

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

        player.setOnVideoSizeChangedListener { videoWidth, videoHeight ->
            surfaceView.setVideoSize(videoWidth,videoHeight)
        }
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.setSurface(holder)
                player.updateSurface(surfaceView.width,surfaceView.height)
                val dataSource =
                    DataSource(url)
                player.setDataSource(dataSource)

                player.start()
//                player.player.setOnVideoSizeChangedListener { mp, width, height ->
//
//                    surfaceView.setVideoSize(width,height)
////                    val point = player.changeVideoSize(width,height,surfaceView.width,surfaceView.height)
////                    val lp = surfaceView.layoutParams
////                    lp.width = point.x
////                    lp.height = point.y
////                    Log.d("Jenly","video:${width},${height}|${point.x},${point.y}")
////                    surfaceView.layoutParams = lp
////                    helper?.let {
////                        it.setVideoSize(width,height)
////                        it.doMeasure(surfaceView.measuredWidth,surfaceView.measuredHeight)
////                        val lp = surfaceView.layoutParams
////                        lp.width = it.measuredHeight
////                        lp.height = width / height * it.measuredHeight
////                        Log.d("Jenly","video:${width},${height}|${it.measuredWidth},${it.measuredHeight}")
////                        surfaceView.layoutParams = lp
////                    }
//                }
//                player.player.setOnPreparedListener {
//                    player.start()
//
//                }



            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
                player.updateSurface(surfaceView.width,surfaceView.height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                player.surfaceDestroy()
            }

        })



//        player.setVideoSurface(surfaceView)
//
//        val dataSource = DataSource(Uri.parse(url))
//        dataSource.options.add(":rtsp-user=icsadmin")
//        dataSource.options.add(":rtsp-pwd=123456")
//        dataSource.options.add(":rtsp-tcp")
//        player.setDataSource(dataSource)

//        player.setVideoView(surfaceView)
//
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
//
//        player.play(media)

    }

    override fun onResume() {
        super.onResume()
        player.start()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }


    override fun onStop() {
        super.onStop()
//        player.stop()
    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()

    }
}

