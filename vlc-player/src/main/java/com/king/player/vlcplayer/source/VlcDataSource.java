package com.king.player.vlcplayer.source;

import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.source.IOption;

import java.util.Collection;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class VlcDataSource extends DataSource implements IOption<String> {

    private Collection<String> options;


    public Collection<String> getOptions() {
        return options;
    }

    public void setOptions(Collection<String> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "VlcDataSource{" +
                "options=" + options +
                "} " + super.toString();
    }
}
