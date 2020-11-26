# KingPlayer

[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/KingPlayer/master/app/release/app-release.apk)
[![API](https://img.shields.io/badge/API-21%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](https://jenly1314.github.io/)
[![QQGroup](https://img.shields.io/badge/QQGroup-20867961-blue.svg)](http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad)


KingPlayer 一个专注于 Android 视频播放器（IjkPlayer、ExoPlayer、VlcPlayer、MediaPlayer）的基础库，无缝切换内核。


## 功能说明
- [x] 主要播放相关核心功能
- [x] 播放器无缝切换
  - [x] MediaPlayer封装实现
  - [x] IjkPlayer封装实现
  - [x] ExoPlayer封装实现
  - [x] VlcPlayer封装实现
- [ ] 控制图层相关
  - [ ] 待补充...

## 示例

布局示例
```xml
    <com.king.player.kingplayer.view.VideoView
        android:id="@+id/videoView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

代码示例
```kotlin
        //初始化一个视频播放器（IjkPlayer、ExoPlayer、VlcPlayer、SysPlayer）
        videoView.player = IjkPlayer(context)
        videoView.setOnSurfaceListener(object : VideoView.OnSurfaceListener {
            override fun onSurfaceCreated(surface: Surface, width: Int, height: Int) {
                LogUtils.d("onSurfaceCreated: $width * $height")
                val dataSource = DataSource(url)
                videoView.setDataSource(dataSource)
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
        


```
```kotlin
        
        //------------ 控制相关
        //开始
        videoView.start()
        //暂停
        videoView.pause()
        //进度调整到指定位置
        videoView.seekTo(pos)
        //停止
        videoView.stop()
        //释放
        videoView.release()
        //重置
        videoView.reset()
```


更多使用详情，请查看[app](app)中的源码使用示例


## 感谢
[Ijkplayer](https://github.com/bilibili/ijkplayer)
[Exoplayer](https://github.com/google/ExoPlayer)
[vlc-android](https://code.videolan.org/videolan/vlc-android)
[MediaPlayer](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaPlayer.java)

## 赞赏
如果您喜欢KingPlayer，或感觉KingPlayer帮助到了您，可以点右上角“Star”支持一下，您的支持就是我的动力，谢谢 :smiley:<p>
您也可以扫描下面的二维码，请作者喝杯咖啡 :coffee:
    <div>
        <img src="https://jenly1314.github.io/image/pay/wxpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/alipay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/pay/qqpay.png" width="280" heght="350">
        <img src="https://jenly1314.github.io/image/alipay_red_envelopes.jpg" width="233" heght="350">
    </div>

## 关于我
   Name: <a title="关于作者" href="https://about.me/jenly1314" target="_blank">Jenly</a>

   Email: <a title="欢迎邮件与我交流" href="mailto:jenly1314@gmail.com" target="_blank">jenly1314#gmail.com</a> / <a title="给我发邮件" href="mailto:jenly1314@vip.qq.com" target="_blank">jenly1314#vip.qq.com</a>

   CSDN: <a title="CSDN博客" href="http://blog.csdn.net/jenly121" target="_blank">jenly121</a>

   CNBlogs: <a title="博客园" href="https://www.cnblogs.com/jenly" target="_blank">jenly</a>

   GitHub: <a title="GitHub开源项目" href="https://github.com/jenly1314" target="_blank">jenly1314</a>

   Gitee: <a title="Gitee开源项目" href="https://gitee.com/jenly1314" target="_blank">jenly1314</a>

   加入QQ群: <a title="点击加入QQ群" href="http://shang.qq.com/wpa/qunwpa?idkey=8fcc6a2f88552ea44b1411582c94fd124f7bb3ec227e2a400dbbfaad3dc2f5ad" target="_blank">20867961</a>
   <div>
       <img src="https://jenly1314.github.io/image/jenly666.png">
       <img src="https://jenly1314.github.io/image/qqgourp.png">
   </div>

   
   
