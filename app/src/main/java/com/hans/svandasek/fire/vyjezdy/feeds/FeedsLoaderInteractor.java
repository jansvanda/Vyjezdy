package com.hans.svandasek.fire.vyjezdy.feeds;

import android.content.Context;
import android.util.Log;

import com.hans.svandasek.fire.vyjezdy.models.FeedItem;
import com.hans.svandasek.fire.vyjezdy.models.SourceItem;
import com.hans.svandasek.fire.vyjezdy.utils.DatabaseUtil;
import com.hans.svandasek.fire.vyjezdy.utils.DateUtil;
import com.hans.svandasek.fire.rssmanager.OnRssLoadListener;
import com.hans.svandasek.fire.rssmanager.RssItem;
import com.hans.svandasek.fire.rssmanager.RssReader;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 11/5/2015.
 */
public class FeedsLoaderInteractor implements IFeedsLoaderInteractor, OnRssLoadListener {

    private Context mContext;
    private OnFeedsLoadedListener onFeedsLoadedListener;

    public FeedsLoaderInteractor(Context context) {
        mContext = context;
    }

    public void loadFeedsFromDb(final OnFeedsLoadedListener onFeedsLoadedListener) {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
            onFeedsLoadedListener.onSuccess(databaseUtil.getAllFeeds(), false);
        } catch (Exception e) {
            onFeedsLoadedListener.onFailure("Failed to load all feeds from database");
        }
    }

    public void loadFeedsFromDbBySource(final OnFeedsLoadedListener onFeedsLoadedListener, String source) {
        try {
            DatabaseUtil databaseUtil = new DatabaseUtil(mContext);
            onFeedsLoadedListener.onSuccess(databaseUtil.getFeedsBySource(source), false);
        } catch (Exception e) {
            onFeedsLoadedListener.onFailure("Failed to load selected feeds from database");
        }
    }

    public void loadFeedsAsync(final OnFeedsLoadedListener onFeedsLoadedListener, List<SourceItem> sourceItems) {
        this.onFeedsLoadedListener = onFeedsLoadedListener;

        String[] urlList = new String[sourceItems.size()];
        String[] sourceList = new String[sourceItems.size()];
        String[] categories = new String[sourceItems.size()];
        int[] categoryImgIds = new int[sourceItems.size()];
        for (int i = 0; i < urlList.length; i++) {
            urlList[i] = sourceItems.get(i).getSourceUrl();
            sourceList[i] = sourceItems.get(i).getSourceName();
            categories[i] = sourceItems.get(i).getSourceCategoryName();
            categoryImgIds[i] = sourceItems.get(i).getSourceCategoryImgId();
        }
        Log.e("url", urlList[0]);
        RssReader rssReader = new RssReader(mContext, urlList, sourceList, categories, categoryImgIds, this);
        rssReader.readRssFeeds();
    }

        private FeedItem getFeedItem(String title, String description, String link, String sourceName, String sourceUrlShort, String category, String pubDate, String district) {
        FeedItem feedItem = new FeedItem();
        feedItem.setItemTitle(title);
        feedItem.setItemDesc(description);
        feedItem.setItemLink(link);
        feedItem.setItemSource(sourceName);
        feedItem.setItemSourceUrl(sourceUrlShort);
        feedItem.setItemCategory(category);
        feedItem.setItemPubDate(pubDate);
        feedItem.setItemDistrict(district);
        return feedItem;
    }

    @Override
    public void onSuccess(List<RssItem> rssItems) {
        List<FeedItem> feedItems = new ArrayList<>();
        for (RssItem rssItem : rssItems) {
            String title = rssItem.getTitle();
            String description = rssItem.getDescription();
            String link = rssItem.getLink();
            String sourceName = rssItem.getSourceName();
            String sourceUrlShort = rssItem.getSourceUrl();
            String category = rssItem.getCategory();
            String pubDate = rssItem.getPubDate();
            String district = rssItem.getDistrict();

            try {
                pubDate = new DateUtil().getDate(pubDate);
            } catch (ParseException e) {
                e.printStackTrace();
                pubDate = "No date available";
            }

            FeedItem feedItem = getFeedItem(title, description, link, sourceName, sourceUrlShort, category, pubDate, district);
            feedItems.add(feedItem);
        }
        onFeedsLoadedListener.onSuccess(feedItems, true);
    }

    @Override
    public void onFailure(String message) {
        onFeedsLoadedListener.onFailure(message);
    }
}
