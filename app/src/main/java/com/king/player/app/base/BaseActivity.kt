package com.king.player.app.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.king.player.app.R

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
abstract class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        title = getTitleText()
        initData()
    }

    open fun getTitleText(): String{
        return getString(R.string.app_name)
    }

    abstract fun getLayoutId() : Int

    open fun initData(){

    }
}