package com.king.player.app.ijk

import com.king.player.app.VideoViewActivity
import com.king.player.ijkplayer.IjkPlayer
import com.king.player.kingplayer.KingPlayer

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
class IjkVideoViewActivity : VideoViewActivity() {

    override fun createPlayer(): KingPlayer<*> {
        return IjkPlayer(this)
    }

    override fun getTitleText(): String {
        return "IjkPlayer"
    }
}