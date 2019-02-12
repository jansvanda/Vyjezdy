package com.hans.svandasek.fire.vyjezdy.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.article.ArticlePresenter;
import com.hans.svandasek.fire.vyjezdy.article.IArticleView;
import com.hans.svandasek.fire.vyjezdy.feeds.FeedsPresenter;
import com.hans.svandasek.fire.vyjezdy.feeds.IFeedsView;
import com.hans.svandasek.fire.vyjezdy.models.FeedItem;
import com.hans.svandasek.fire.vyjezdy.models.SettingsPreferences;
import com.hans.svandasek.fire.vyjezdy.utils.NetworkConnectionUtil;
import com.hans.svandasek.fire.vyjezdy.utils.WebsiteIntentUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArticleActivity extends AppCompatActivity implements IArticleView, IFeedsView {

    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
    private static final  String MY_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab_archive)
    FloatingActionButton fabArchive;
    @Bind(R.id.text_view_feed_title)
    TextView txtFeedTitle;
    @Bind(R.id.text_view_feed_category)
    TextView txtFeedCategory;
    @Bind(R.id.text_view_feed_pub_date)
    TextView txtFeedPubDate;
    @Bind(R.id.text_view_content)
    TextView txtContent;
    @Bind(R.id.image_view_article)
    ImageView imgArticle;
    @Bind(R.id.text_view_district)
    TextView txtFeedDistrict;

    private boolean mSaved = false;
    private ArticlePresenter mArticlePresenter;
    private FeedsPresenter mFeedsPresenter;
    Context context = this;
    private AdView mAdView;
    private AdView adView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set theme
        setSystemTheme(getFeedThemeBgId());

        setContentView(R.layout.activity_article);

        ButterKnife.bind(this);

        //get Feed item from intent bundle
        FeedItem feedItem = getFeedItem();

        //set toolbar
        setToolbar(feedItem);

        //set default font sizes
        setFontSize();

        //set feed title
        txtFeedTitle.setText(feedItem.getItemTitle());

        //set feed category
        txtFeedCategory.setText(feedItem.getItemCategory());

        //set feed publish date
        txtFeedPubDate.setText(feedItem.getItemPubDate());

        txtFeedDistrict.setText(feedItem.getItemDistrict());



        //set feed image
        Glide.with(ArticleActivity.this)
                .load(feedItem.getItemImgUrl())
                .centerCrop()
                .crossFade()
                .into(imgArticle);

        //load article presenter
        if (mArticlePresenter == null) {
            mArticlePresenter = new ArticlePresenter(ArticleActivity.this, this);
        }

        //load feeds presenter
        if (mFeedsPresenter == null) {
            mFeedsPresenter = new FeedsPresenter(ArticleActivity.this, this);
        }

        //only load the data online if this activity was opened from FeedsFragment
        //, else if it was redirected from ArchiveFragment then load the saved article
        if (feedItem.getItemWebDesc().isEmpty()) {
            mSaved = false;
            if (NetworkConnectionUtil.isNetworkAvailable(ArticleActivity.this)) {
                mArticlePresenter.attemptArticleLoading(feedItem.getItemLink());
            } else {
                NetworkConnectionUtil.showNoNetworkDialog(ArticleActivity.this);
            }
        } else {
            mSaved = true;
            fabArchive.setImageResource(R.drawable.ic_archive_done_24dp);
            txtContent.setText(feedItem.getItemWebDesc());
        }/* else if(!feedItem.getItemWebDescSync().isEmpty()){
            txtContent.setText(feedItem.getItemWebDescSync());
        }*/


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, "ca-app-pub-9643426756805437~1994500921");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        adView = findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("819BF3BEF14F000525869D2645FB83F3")
                .build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        AdManager adManager = new AdManager(ArticleActivity.this, "ca-app-pub-3940256099942544/1033173712");
        adManager.createAd();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void setFontSize() {
        txtFeedTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_TITLE_SIZE);
        txtFeedCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_CATEGORY_SIZE);
        txtFeedPubDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_PUBLISH_DATE_SIZE);
        txtFeedDistrict.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_PUBLISH_DATE_SIZE);
        txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_CONTENT_SIZE);
    }

    private void setToolbar(FeedItem feedItem) {
        toolbar.setTitle(feedItem.getItemCategory());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private FeedItem getFeedItem() {
        Bundle bundle = getIntent().getExtras();
        FeedItem feedItem = new FeedItem();
        feedItem.setItemTitle(bundle.getString("title", ""));
        feedItem.setItemCategory(bundle.getString("category", ""));
        feedItem.setItemDesc(bundle.getString("description", ""));
        feedItem.setItemImgUrl(bundle.getString("img_url", ""));
        feedItem.setItemCategoryImgId(bundle.getInt("image_id", 0));
        feedItem.setItemLink(bundle.getString("link", ""));
        feedItem.setItemPubDate(bundle.getString("pub_date", ""));
        feedItem.setItemSource(bundle.getString("source", ""));
        feedItem.setItemSourceUrl(bundle.getString("source_url", ""));
        feedItem.setItemWebDesc(bundle.getString("article_content", ""));
        feedItem.setItemWebDescSync(bundle.getString("sync_desc", ""));
        feedItem.setItemDistrict(bundle.getString("district", ""));
        return feedItem;
    }

    private int getFeedThemeBgId() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getInt("category_bg_img_id", -1);
    }

    private void setSystemTheme(int imgId) {
        if (!SettingsPreferences.THEME) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(ArticleActivity.this, R.color.darkColorPrimaryDark));
            }
            getWindow().setBackgroundDrawableResource(R.color.darkColorBackground);
        } else {

            int primaryColor = -1, secondaryColor = -1;

            switch (imgId) {
                case R.drawable.cyan_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Cyan);
                    primaryColor = R.color.md_cyan_500;
                    secondaryColor = R.color.md_cyan_700;
                    break;
                case R.drawable.green_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Green);
                    primaryColor = R.color.md_green_500;
                    secondaryColor = R.color.md_green_700;
                    break;
                case R.drawable.red_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Red);
                    primaryColor = R.color.md_red_900;
                    secondaryColor = R.color.md_red_900;
                    break;
                case R.drawable.orange_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Orange);
                    primaryColor = R.color.md_deep_orange_500;
                    secondaryColor = R.color.md_deep_orange_700;
                    break;
                case R.drawable.lime_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Lime);
                    primaryColor = R.color.md_lime_500;
                    secondaryColor = R.color.md_lime_700;
                    break;
                case R.drawable.teal_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Teal);
                    primaryColor = R.color.md_teal_500;
                    secondaryColor = R.color.md_teal_700;
                    break;
                case R.drawable.purple_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Purple);
                    primaryColor = R.color.md_deep_purple_500;
                    secondaryColor = R.color.md_deep_purple_700;
                    break;
                case R.drawable.grey_circle:
                    setTheme(R.style.AppThemeCustom_NoActionBar_Grey);
                    primaryColor = R.color.md_grey_500;
                    secondaryColor = R.color.md_grey_700;
                    break;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //failed at coloring the status bar transparent
                //getWindow().setNavigationBarColor(ContextCompat.getColor(ArticleActivity.this, primaryColor));
                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().setStatusBarColor(ContextCompat.getColor(ArticleActivity.this, secondaryColor));
            }
        }
    }

    @OnClick(R.id.fab_archive)
    public void onArchiveArticle() {
        if (mSaved) {
            mArticlePresenter.removeArticle(getFeedItem());
        } else {
            mArticlePresenter.archiveArticle(getFeedItem(), txtContent.getText().toString());
        }
    }

    @Override
    public void onArticleLoaded(String article) {
        //set content of the rss feed item url
        txtContent.setText(article);
    }

    @Override
    public void onArticleFailedToLoad(String message) {
        Toast.makeText(ArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleSaved(String message) {
        mSaved = true;
        fabArchive.setImageResource(R.drawable.ic_archive_done_24dp);
        Toast.makeText(ArticleActivity.this, "saved: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleSavingFailed(String message) {
        Toast.makeText(ArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleRemoved(String message) {
        mSaved = false;
        fabArchive.setImageResource(R.drawable.ic_archive_24dp);
    }

    @Override
    public void onArticleRemovalFailed(String message) {
        Toast.makeText(ArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //override default back button present on the toolbar
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_zkratky) {

            MaterialDialog confirmDeleteDialog = new MaterialDialog.Builder(this)
                    .title(R.string.zkratky)
                    .content(R.string.zkratky_description)
                    .iconRes(R.drawable.ic_zkratky_black_24dp)
                    .negativeText(R.string.dismiss)
                    .build();
            confirmDeleteDialog.show();
            return true;
        }

        if (id == R.id.action_refresh) {
            if (NetworkConnectionUtil.isNetworkAvailable(ArticleActivity.this)) {
                mArticlePresenter.attemptArticleLoading(getFeedItem().getItemLink());
            } else {
                NetworkConnectionUtil.showNoNetworkDialog(ArticleActivity.this);
            }
            return true;
        }

        if (id == R.id.action_share) {
            Intent shareintent = new Intent(Intent.ACTION_SEND);
            shareintent.putExtra(Intent.EXTRA_SUBJECT, getFeedItem().getItemTitle());
            shareintent.putExtra(Intent.EXTRA_TEXT, getFeedItem().getItemLink() + "\n\n" + txtContent.getText().toString());
            shareintent.setType("text/plain");

            // Create "Copy Link To Clipboard" Intent
            Intent clipboardIntent = new Intent(context, CopyToClipboardActivity.class);
            clipboardIntent.setData(Uri.parse(getFeedItem().getItemLink() + "\n\n" + txtContent.getText().toString()));

            // Create Chooser Intent with "Copy Link To Clipboard" option
            Intent chooserIntent = Intent.createChooser(shareintent, "Sdílet s");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { clipboardIntent });
            startActivity(chooserIntent);

            return true;
        }

        if (id == R.id.action_open_in_browser) {
            //Use chrome custom tabs if user wants in-app browser, otherwise use user's own selected browser for opening links
            if (SettingsPreferences.IN_APP_BROWSER) {
                new WebsiteIntentUtil(ArticleActivity.this).loadCustomChromeTabs(getFeedItem().getItemLink());
            } else {
                new WebsiteIntentUtil(ArticleActivity.this).loadNormalBrowser(getFeedItem().getItemLink());
            }
            return true;
        }


        if (id == R.id.action_delete) {
            MaterialDialog confirmDeleteDialog = new MaterialDialog.Builder(this)
                    .title(R.string.delete_this_feed)
                    .content(R.string.delete_this_feed_desc)
                    .iconRes(R.drawable.ic_delete_24dp)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            mFeedsPresenter.deleteSelectedFeed(getFeedItem());
                            Intent intent = new Intent(ArticleActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).build();
            confirmDeleteDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //no use
    @Override
    public void feedsLoaded(List<FeedItem> feedItems) {

    }

    //no use
    @Override
    public void loadingFailed(String message) {

    }
}
