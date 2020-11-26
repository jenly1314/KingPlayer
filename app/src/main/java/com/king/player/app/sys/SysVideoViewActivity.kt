package com.king.player.app.sys

import com.king.player.app.VideoViewActivity
import com.king.player.kingplayer.KingPlayer
import com.king.player.kingplayer.SysPlayer

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class SysVideoViewActivity : VideoViewActivity() {

    override fun createPlayer(): KingPlayer<*> {
        return SysPlayer(this)
    }

    override fun getTitleText(): String {
        return "SysPlayer"
    }
}