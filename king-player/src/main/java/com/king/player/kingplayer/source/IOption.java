package com.king.player.kingplayer.source;

import java.util.Collection;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public interface IOption<T> {

    Collection<T> getOptions();

    void setOptions(Collection<T> t);
}
