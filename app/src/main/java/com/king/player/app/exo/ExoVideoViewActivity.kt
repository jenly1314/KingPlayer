package com.king.player.app.exo

import com.king.player.app.VideoViewActivity
import com.king.player.exoplayer.ExoPlayer
import com.king.player.kingplayer.KingPlayer

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class ExoVideoViewActivity : VideoViewActivity() {

    override fun createPlayer(): KingPlayer<*> {
        return ExoPlayer(this)
    }

    override fun getTitleText(): String {
        return "ExoPlayer"
    }
}