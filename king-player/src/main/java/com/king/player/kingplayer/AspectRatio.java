package com.king.player.kingplayer;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
@IntDef(value = {AspectRatio.AR_ASPECT_FIT_PARENT, AspectRatio.AR_ASPECT_FILL_PARENT, AspectRatio.AR_ASPECT_WRAP_CONTENT, AspectRatio.AR_MATCH_PARENT, AspectRatio.AR_16_9_FIT_PARENT, AspectRatio.AR_4_3_FIT_PARENT})
@Retention(RetentionPolicy.SOURCE)
public @interface AspectRatio {
    int AR_ASPECT_FIT_PARENT = 0; // without clip
    int AR_ASPECT_FILL_PARENT = 1; // may clip
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;
}
