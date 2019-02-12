package com.hans.svandasek.fire.vyjezdy.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.article.ArticleInteractor;
import com.hans.svandasek.fire.vyjezdy.article.ArticlePresenter;
import com.hans.svandasek.fire.vyjezdy.article.IArticleView;
import com.hans.svandasek.fire.vyjezdy.article.OnArticleLoadedListener;
import com.hans.svandasek.fire.vyjezdy.feeds.FeedsPresenter;
import com.hans.svandasek.fire.vyjezdy.feeds.IFeedsView;
import com.hans.svandasek.fire.vyjezdy.models.FeedItem;
import com.hans.svandasek.fire.vyjezdy.models.SettingsPreferences;
import com.hans.svandasek.fire.vyjezdy.utils.DatabaseUtil;
import com.hans.svandasek.fire.vyjezdy.utils.NetworkConnectionUtil;
import com.hans.svandasek.fire.vyjezdy.utils.WebsiteIntentUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hans.svandasek.fire.vyjezdy.services.SyncArticlesIntentService.STATUS;

public class NotificationArticleActivity extends AppCompatActivity implements IArticleView, IFeedsView {

    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
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
    String mUrl, mErrorMsg;
    Elements mParagraphs;
    ArticleInteractor mArticleInteractor;
    ArticleInteractor.ArticleAsyncLoader mArticleAsyncLoader;
    private OnArticleLoadedListener mOnArticleLoadedListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Looper.myLooper() == Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            Log.e("THREADING", "Notificationarticleactivity is main");
        }

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String title = sharedPreferences.getString("title", "title");
        String time1 = sharedPreferences.getString("time1", "time1");
        String content = sharedPreferences.getString("content", "content");
        String okres = sharedPreferences.getString("okres", "okres");
        String category = sharedPreferences.getString("category", "category");


        //set theme
        //setSystemTheme(getFeedThemeBgId());

        setContentView(R.layout.notif_activity_article);

        ButterKnife.bind(this);

        //get Feed item from intent bundle
        //FeedItem feedItem = getFeedItem();

        //set toolbar
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set default font sizes
        setFontSize();


        //set feed title
        txtFeedTitle.setText(title);

        //set feed category
        txtFeedCategory.setText(category);

        //set feed publish date
        txtFeedPubDate.setText(time1);

        //set okres
        txtFeedDistrict.setText(okres);


        txtContent.setText(content);
    }

    private void setFontSize() {
        txtFeedTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_TITLE_SIZE);
        txtFeedCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_CATEGORY_SIZE);
        txtFeedPubDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_PUBLISH_DATE_SIZE);
        txtFeedDistrict.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_PUBLISH_DATE_SIZE);
        txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsPreferences.ARTICLE_CONTENT_SIZE);
    }



    private int getFeedThemeBgId() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getInt("category_bg_img_id", -1);
    }

    private void setSystemTheme(int imgId) {
        if (!SettingsPreferences.THEME) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(NotificationArticleActivity.this, R.color.darkColorPrimaryDark));
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
                getWindow().setStatusBarColor(ContextCompat.getColor(NotificationArticleActivity.this, secondaryColor));
            }
        }
    }

    @OnClick(R.id.fab_archive)
    public void onArchiveArticle() {
//        if (mSaved) {
//            mArticlePresenter.removeArticle(getFeedItem());
//        } else {
//            mArticlePresenter.archiveArticle(getFeedItem(), txtContent.getText().toString());
//        }
    }

    @Override
    public void onArticleLoaded(String article) {
        //set content of the rss feed item url
        txtContent.setText(article);
    }

    @Override
    public void onArticleFailedToLoad(String message) {
        Toast.makeText(NotificationArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleSaved(String message) {
        mSaved = true;
        fabArchive.setImageResource(R.drawable.ic_archive_done_24dp);
        Toast.makeText(NotificationArticleActivity.this, "saved: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleSavingFailed(String message) {
        Toast.makeText(NotificationArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleRemoved(String message) {
        mSaved = false;
        fabArchive.setImageResource(R.drawable.ic_archive_24dp);
    }

    @Override
    public void onArticleRemovalFailed(String message) {
        Toast.makeText(NotificationArticleActivity.this, "Error:\n" + message, Toast.LENGTH_SHORT).show();
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
            if (NetworkConnectionUtil.isNetworkAvailable(NotificationArticleActivity.this)) {
                mArticlePresenter.attemptArticleLoading("http://www.firebrno.cz/rss/udalosti/breclav");
            } else {
                NetworkConnectionUtil.showNoNetworkDialog(NotificationArticleActivity.this);
            }
            return true;
        }

        if (id == R.id.action_share) {
            Intent shareintent = new Intent(Intent.ACTION_SEND);
            shareintent.putExtra(Intent.EXTRA_SUBJECT, "title");
            shareintent.putExtra(Intent.EXTRA_TEXT, "Link" + "\n\n" + txtContent.getText().toString());
            shareintent.setType("text/plain");

            // Create "Copy Link To Clipboard" Intent
            Intent clipboardIntent = new Intent(context, CopyToClipboardActivity.class);
            clipboardIntent.setData(Uri.parse("Link" + "\n\n" + txtContent.getText().toString()));

            // Create Chooser Intent with "Copy Link To Clipboard" option
            Intent chooserIntent = Intent.createChooser(shareintent, "Sdílet s");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{clipboardIntent});
            startActivity(chooserIntent);

            return true;
        }

        if (id == R.id.action_open_in_browser) {
            //Use chrome custom tabs if user wants in-app browser, otherwise use user's own selected browser for opening links
            if (SettingsPreferences.IN_APP_BROWSER) {
                new WebsiteIntentUtil(NotificationArticleActivity.this).loadCustomChromeTabs("http://www.firebrno.cz/rss/udalosti/breclav");
            } else {
                new WebsiteIntentUtil(NotificationArticleActivity.this).loadNormalBrowser("http://www.firebrno.cz/rss/udalosti/breclav");
            }
            return true;
        }


//        if (id == R.id.action_delete) {
//            MaterialDialog confirmDeleteDialog = new MaterialDialog.Builder(this)
//                    .title(R.string.delete_this_feed)
//                    .content(R.string.delete_this_feed_desc)
//                    .iconRes(R.drawable.ic_delete_24dp)
//                    .positiveText(R.string.delete)
//                    .negativeText(R.string.cancel)
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
//                            mFeedsPresenter.deleteSelectedFeed(getFeedItem());
//                            Intent intent = new Intent(NotificationArticleActivity.this, HomeActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }).build();
//            confirmDeleteDialog.show();
//            return true;
//        }

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
