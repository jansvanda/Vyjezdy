package com.hans.svandasek.fire.rssmanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 11/15/2015.
 */
public class RssReader implements OnFeedLoadListener {
    private String[] mUrlList, mSourceList, mCategories;
    private int[] mCategoryImgIds;
    private Context mContext;
    private List<RssItem> mRssItems = new ArrayList<>();
    private RssParser mRssParser;
    private int mPosition = 0;
    private MaterialDialog mMaterialDialog;
    private OnRssLoadListener mOnRssLoadListener;
    Activity context;


    //Pro použití dole, první písmeno velkým
    public static String upperCaseFirst(String value) {

        // Convert String to char array.
        char[] array = value.toCharArray();
        // Modify first element in array.
        array[0] = Character.toUpperCase(array[0]);
        // Return string.
        return new String(array);
    }

    public RssReader(Context context, String[] urlList, String[] sourceList, String[] categories, int[] categoryImgIds, OnRssLoadListener onRssLoadListener) {
        this.mContext = context;
        this.mUrlList = urlList;
        this.mSourceList = sourceList;
        this.mCategories = categories;
        this.mCategoryImgIds = categoryImgIds;
        this.mOnRssLoadListener = onRssLoadListener;
    }

    public void readRssFeeds() {
        mMaterialDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.loading_feeds)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .cancelable(false)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (mRssParser != null) {
                            mRssParser.cancel(true);
                        }
                        mOnRssLoadListener.onFailure("User performed dismiss action");
                    }
                })
                .build();
        mMaterialDialog.show();

        mMaterialDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (mRssParser != null) {
                    mRssParser.cancel(true);
                }
                mOnRssLoadListener.onFailure("User performed dismiss action");
            }
        });

        if (mRssItems != null) {
            mRssItems.clear();
        }
        parseRss(0);
    }

    private void parseRss(int position) {
        if (position != mUrlList.length) {
            mRssParser = new RssParser(mUrlList[position], this);
            mRssParser.execute();
            String source = getWebsiteName(mUrlList[position]);
            mMaterialDialog.setContent(source);
        } else {
            mMaterialDialog.dismiss();
            mOnRssLoadListener.onSuccess(mRssItems);
        }
    }

    private String getWebsiteName(String url) {
        URI uri;
        try {
            uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onSuccess(Elements items) {
        for (Element item : items) {
            mRssItems.add(getRssItem(item));
        }
        mPosition++;
        parseRss(mPosition);
    }

    @Override
    public void onFailure(String message) {
        mOnRssLoadListener.onFailure(message);
    }


    private RssItem getRssItem(Element element) {
        // Nechceme zobrazovat všechny informace v nadpisu
        String title_all = element.select("title").first().text();
        String titlex = title_all
                .replace("Požár - ","")
                .replace("Ostatní - ","")
                .replace("Dopravní nehoda - ", "")
                .replace("Technická pomoc - ","")
                .replace("Záchrana osob/zvířat - ","")
                .replace("Chemické látky - ","")
                .replace(" - Břeclav", "")
                .replace(" - Blansko", "")
                .replace(" - Brno-město", "")
                .replace(" - Brno-venkov", "")
                .replace(" - Hodonín", "")
                .replace(" - Vyškov", "")
                .replace(" - Znojmo", "")
                .replace(" - Břeclav", "")
                .toLowerCase();

        //první písmeno velkým
        String title = upperCaseFirst(titlex);

        String description = element.select("description").first().text();
        String link = element.select("link").first().text();
        String sourceName = mSourceList[mPosition];
        String sourceUrl = getWebsiteName(mUrlList[mPosition]);

        String imageUrl;
        if (!element.select("media|thumbnail").isEmpty()) {
            imageUrl = element.select("media|thumbnail").attr("url");
        } else if (!element.select("media|content").isEmpty()) {
            imageUrl = element.select("media|content").attr("url");
        } else if (!element.select("image").isEmpty()) {
            imageUrl = element.select("image").attr("url");
        } else {
            imageUrl = null;
        }

        //Přidělení našich kategorií ke category
        String pozar = "Požár";
        String ostatni = "Ostatní";
        String dn = "Dopravní nehoda";
        String tp = "Technická pomoc";
        String zoz = "Záchrana osob/zvířat";
        String ch = "Chemické látky";

        String category = "";
        if (title_all.contains(pozar)) {
            category = pozar;
        } else if (title_all.contains(ostatni)){
            category = ostatni;
        } else if (title_all.contains(dn)){
            category = dn;
        } else if (title_all.contains(tp)){
            category = tp;
        } else if (title_all.contains(zoz)){
            category =  zoz;
        } else if (title_all.contains(ch)){
            category = ch;
        }

        //Přidělení okresu
        String blansko = "Blansko";
        String b_mesto = "Brno-město";
        String b_venkov = "Brno-venkov";
        String breclav = "Břeclav";
        String hodonin = "Hodonín";
        String vyskov = "Vyškov";
        String znojmo = "Znojmo";

        String district = "";
        if (title_all.contains(blansko)) {
            district = blansko;
        } else if (title_all.contains(b_mesto)){
            district = b_mesto;
        } else if (title_all.contains(b_venkov)){
            district = b_venkov;
        } else if (title_all.contains(breclav)){
            district = breclav;
        } else if (title_all.contains(hodonin)){
            district =  hodonin;
        } else if (title_all.contains(vyskov)){
            district =  vyskov;
        } else if (title_all.contains(znojmo)){
            district = znojmo;
        }


        //datum
        String pubDate = "";
        if (!element.select("pubDate").isEmpty()) {
            pubDate = element.select("pubDate").first().text();
        }

        int categoryImgId = mCategoryImgIds[mPosition];


        RssItem rssItem = new RssItem();

        rssItem.setTitle(title);
        rssItem.setDescription(description);
        rssItem.setLink(link);
        rssItem.setSourceName(sourceName);
        rssItem.setSourceUrl(sourceUrl);
        rssItem.setImageUrl(imageUrl);
        rssItem.setCategory(category);
        rssItem.setPubDate(pubDate);
        rssItem.setCategoryImgId(categoryImgId);
        rssItem.setDistrict(district);

        return rssItem;


    }
}
