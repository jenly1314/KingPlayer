package com.king.player.app

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.permissionx.guolindev.PermissionX

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionX.init(this)
            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if(allGranted){

                }
            }

    }


    fun onClick(v: View){
        when(v.id){
            R.id.btnSurfaceView -> startActivity(Intent(this,PlayerSurfaceViewActivity::class.java))
            R.id.btnTextureView -> startActivity(Intent(this,PlayerTextureViewActivity::class.java))
            R.id.btnVideoView -> startActivity(Intent(this,VideoViewActivity::class.java))
        }
    }
}
