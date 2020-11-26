package com.king.player.app.vlc

import com.king.player.app.VideoViewActivity
import com.king.player.kingplayer.KingPlayer
import com.king.player.vlcplayer.VlcPlayer

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class VlcVideoViewActivity : VideoViewActivity() {

    override fun createPlayer(): KingPlayer<*> {
        return VlcPlayer(this)
    }

    override fun getTitleText(): String {
        return "VlcPlayer"
    }
}