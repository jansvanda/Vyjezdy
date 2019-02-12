package com.hans.svandasek.fire.vyjezdy.sources;

import android.content.Context;

import com.hans.svandasek.fire.vyjezdy.models.SourceItem;

/**
 * Created by Kartik_ch on 11/7/2015.
 */
public interface ISourcePresenter {
    void addSource(SourceItem sourceItem);

    void getSources();

    void getSourceItems();
}
