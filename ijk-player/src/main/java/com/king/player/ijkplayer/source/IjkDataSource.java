package com.king.player.ijkplayer.source;

import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.source.IOption;

import java.util.Collection;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class IjkDataSource extends DataSource implements IOption<IjkDataSource.OptionModel> {

    private Collection<IjkDataSource.OptionModel> options;

    @Override
    public Collection<OptionModel> getOptions() {
        return options;
    }

    @Override
    public void setOptions(Collection<OptionModel> options) {
        this.options = options;
    }

    public static class OptionModel{
        private int category;
        private String name;
        private long valueLong;
        private String valueString;
        private boolean isString;

        /**
         *
         * @param category {@link tv.danmaku.ijk.media.player.IjkMediaPlayer}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_FORMAT}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_CODEC}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_PLAYER}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_SWS}
         * @param name
         * @param value
         */
        public OptionModel(int category, String name, long value) {
            this.category = category;
            this.name = name;
            this.valueLong = value;
            isString = false;
        }

        /**
         *
         * @param category {@link tv.danmaku.ijk.media.player.IjkMediaPlayer}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_FORMAT}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_CODEC}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_PLAYER}
         * {@link tv.danmaku.ijk.media.player.IjkMediaPlayer#OPT_CATEGORY_SWS}
         * @param name
         * @param value
         */
        public OptionModel(int category, String name, String value) {
            this.category = category;
            this.name = name;
            this.valueString = value;
            isString = true;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getValueLong() {
            return valueLong;
        }

        public void setValue(long value) {
            this.valueLong = value;
            isString = false;
        }

        public String getValueString() {
            return valueString;
        }

        public void setValue(String value) {
            this.valueString = value;
            isString = true;
        }

        public boolean isString(){
            return isString;
        }
    }
}
