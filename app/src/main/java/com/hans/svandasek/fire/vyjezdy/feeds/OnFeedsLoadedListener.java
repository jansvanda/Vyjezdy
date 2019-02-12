package com.hans.svandasek.fire.vyjezdy.feeds;

import com.hans.svandasek.fire.vyjezdy.models.FeedItem;

import java.util.List;

/**
 * Created by Kartik_ch on 11/5/2015.
 */
public interface OnFeedsLoadedListener {
    void onSuccess(List<FeedItem> feedItems, boolean loadedNewFeeds);

    void onFailure(String message);
}
