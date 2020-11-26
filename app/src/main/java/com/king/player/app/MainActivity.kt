package com.king.player.app

import android.content.Intent
import android.view.View
import com.king.player.app.base.BaseActivity
import com.king.player.app.exo.ExoVideoViewActivity
import com.king.player.app.ijk.IjkVideoViewActivity
import com.king.player.app.sys.SysVideoViewActivity
import com.king.player.app.vlc.VlcVideoViewActivity

class MainActivity : BaseActivity() {


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }


    fun onClick(v: View){
        when(v.id){
            R.id.btnIjkPlayer -> startActivity(Intent(this,IjkVideoViewActivity::class.java))
            R.id.btnExoPlayer -> startActivity(Intent(this,ExoVideoViewActivity::class.java))
            R.id.btnVlcPlayer -> startActivity(Intent(this,VlcVideoViewActivity::class.java))
            R.id.btnSysPlayer -> startActivity(Intent(this,SysVideoViewActivity::class.java))
            R.id.btnSurfaceView -> startActivity(Intent(this,PlayerSurfaceViewActivity::class.java))
            R.id.btnTextureView -> startActivity(Intent(this,PlayerTextureViewActivity::class.java))
            R.id.btnVideoView -> startActivity(Intent(this,VideoViewActivity::class.java))
        }
    }
}
