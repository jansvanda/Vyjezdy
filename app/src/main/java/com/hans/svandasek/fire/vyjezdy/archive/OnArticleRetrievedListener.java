package com.hans.svandasek.fire.vyjezdy.archive;

import com.hans.svandasek.fire.vyjezdy.models.FeedItem;

import java.util.List;

/**
 * Created by Kartik_ch on 12/9/2015.
 */
public interface OnArticleRetrievedListener {
    void onSuccess(List<FeedItem> feedItems);

    void onFailure(String message);
}
