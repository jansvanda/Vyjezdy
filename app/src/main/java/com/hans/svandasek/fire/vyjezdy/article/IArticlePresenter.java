package com.hans.svandasek.fire.vyjezdy.article;

import com.hans.svandasek.fire.vyjezdy.models.FeedItem;

/**
 * Created by Kartik_ch on 12/2/2015.
 */
public interface IArticlePresenter {
    void attemptArticleLoading(String url);

    void archiveArticle(FeedItem feedItem, String article);

    void removeArticle(FeedItem feedItem);
}
