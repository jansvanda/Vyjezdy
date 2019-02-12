package com.hans.svandasek.fire.vyjezdy.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.models.SettingsPreferences;
import com.hans.svandasek.fire.vyjezdy.models.SourceItem;
import com.hans.svandasek.fire.vyjezdy.sources.ISourceView;
import com.hans.svandasek.fire.vyjezdy.sources.SourcesPresenter;
import com.hans.svandasek.fire.vyjezdy.ui.fragments.ArchiveFragment;
import com.hans.svandasek.fire.vyjezdy.ui.fragments.FeedsFragment;
import com.hans.svandasek.fire.vyjezdy.utils.AnimationUtil;
import com.hans.svandasek.fire.vyjezdy.utils.DateUtil;
import com.hans.svandasek.fire.vyjezdy.utils.FadeAnimationUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;



public class HomeActivity extends AppCompatActivity implements ISourceView, AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.text_view_toolbar_title)
    TextView txtToolbarTitle;
    @Bind(R.id.recycler_view_feeds)
    RecyclerView recyclerViewFeeds;
    @Bind(R.id.main_layout)
    RelativeLayout mainLayout;
    @Bind(R.id.secondary_layout)
    RelativeLayout secondaryLayout;
    @Bind(R.id.material_edit_text_source_name)
    MaterialEditText eTxtSourceName;
    @Bind(R.id.material_edit_text_source_url)
    MaterialEditText eTxtSourceUrl;
    @Bind(R.id.button_category)
    Button btnCategory;
    @Bind(R.id.button_save)
    Button btnSave;
    @Bind(R.id.image_view_category)
    ImageView imgCategory;
    @Bind(R.id.text_view_category)
    TextView txtCategory;
    @Bind(R.id.category_layout)
    LinearLayout categoryLayout;
    @Bind(R.id.spinner_sources)
    Spinner spinnerSources;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    int mNavPosition = 0;
    private SourcesPresenter mSourcesPresenter;
    private AnimationUtil mAnimationUtil;
    private DateUtil mDateUtil;
    private SourceItem mSourceItem;
    private boolean mAddFeedStatus = false;
    private static Context mContext;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = getApplicationContext();
        super.onCreate(savedInstanceState);

        //initialize settings
        SettingsPreferences.init(HomeActivity.this);

        //set theme
        setActivityTheme();

        setContentView(R.layout.activity_home);

        ButterKnife.bind(HomeActivity.this);

        //set theme of other ui elements

        if (mSourcesPresenter == null) {
            mSourcesPresenter = new SourcesPresenter(HomeActivity.this, HomeActivity.this);
        }

        setSourcesSpinner();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //set the first item enabled true
        navigationView.getMenu().getItem(0).setChecked(true);

        //show changelog if user opens the app for first time
        if (SettingsPreferences.CHANGE_LOG_DIALOG_SHOW) {
            SettingsPreferences.showChangeLog(HomeActivity.this);
        }


        FirebaseInstanceId.getInstance().getToken();

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("Data", "Key: " + key + " Value: " + value);
            }
        }


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


    }


    public static Context getContext() {
        return mContext;
    }

    private void setActivityTheme() {
        if (!SettingsPreferences.THEME) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
            getWindow().setBackgroundDrawableResource(R.color.darkColorBackground);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_feeds) {
            if (secondaryLayout.getVisibility() == View.INVISIBLE && mNavPosition != 0) {
                mSourcesPresenter.getSources();
                //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(spinnerSources, 500);
                spinnerSources.setVisibility(View.VISIBLE);
                fragment = new FeedsFragment().setInstance("breclav");
                txtToolbarTitle.setText("Add Feed");
                txtToolbarTitle.setVisibility(View.INVISIBLE);
            }
            mNavPosition = 0;
            //new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(txtToolbarTitle, 1500);
        } else if (id == R.id.nav_archive) {
            hideSpinnerAndFab();
            fragment = new ArchiveFragment();
            showTitle("Archive");
            mNavPosition = 1;
        } else if (id == R.id.nav_settings) {
            runIntent(SettingsActivity.class);
            mNavPosition = 2;
        } else if (id == R.id.nav_about) {
            runIntent(AboutActivity.class);
            mNavPosition = 3;
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void runIntent(final Class resultActivityClass) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomeActivity.this, resultActivityClass);
                startActivity(intent);
            }
        }, 200);
    }

    private void showTitle(String title) {
        txtToolbarTitle.setText(title);
        if (txtToolbarTitle.getVisibility() != View.VISIBLE) {
            new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(txtToolbarTitle, 500);
        }
    }

    private void hideSpinnerAndFab() {
        if (spinnerSources.getVisibility() != View.INVISIBLE || secondaryLayout.getVisibility() != View.INVISIBLE) {
            new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(spinnerSources, 500);
        }
        //spinnerSources.setVisibility(View.INVISIBLE);
    }

    //set the selected fragment onto the screen(activity)
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.main_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setSourcesSpinner() {
        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                int spinnerValue = sharedPreferences.getInt("spinnerChoice", -1);

//        SharedPreferences sharedPref = mContext.getSharedPreferences();1
//        int spinnerValue = sharedPref.getInt("spinnerChoice", -1);
                if (spinnerValue != -1)
                    spinnerSources.setSelection(spinnerValue);

                mSourcesPresenter.getSources();
                spinnerSources.setOnItemSelectedListener(HomeActivity.this);
                spinnerSources.setSelection(spinnerValue);
            }
        }).start();


    }

    @OnClick(R.id.button_save)
    public void saveSource() {
        String sourceName = eTxtSourceName.getText().toString();
        String sourceUrl = eTxtSourceUrl.getText().toString();
        String sourceCategory = txtCategory.getText().toString();
        String sourceCategoryImd = txtCategory.getText().toString();
        //Toast.makeText(HomeActivity.this, "name: "+sourceName+", url: "+sourceUrl, Toast.LENGTH_SHORT).show();
        mDateUtil = new DateUtil();
        String date = mDateUtil.getCurrDate();

        mSourceItem = new SourceItem();
        mSourceItem.setSourceName(sourceName);
        mSourceItem.setSourceUrl(sourceUrl);
        mSourceItem.setSourceCategoryName(sourceCategory);
        //mSourceItem.setSourceCategoryImgId(sourceCategoryImd);
        mSourceItem.setSourceDateAdded(date);

        mSourcesPresenter.addSource(mSourceItem);
    }

    // spinner Items (select from different sources)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(HomeActivity.this, "Item selected: " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
        if (i == 0) {
            loadFragment(new FeedsFragment().setInstance("all_sources"));
        } else {
            loadFragment(new FeedsFragment().setInstance(adapterView.getItemAtPosition(i).toString()));
        }

        int Choice = spinnerSources.getSelectedItemPosition();
        SharedPreferences sharedPref = getSharedPreferences("SourceName", 0);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putInt("spinnerChoice", Choice);
        prefEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void enableSecondaryLayout(boolean status) {
        eTxtSourceName.setEnabled(status);
        eTxtSourceUrl.setEnabled(status);
        btnCategory.setEnabled(status);
        btnSave.setEnabled(status);
        //categoryLayout.setVisibility(View.INVISIBLE);
        /*if(status){
            categoryLayout.setVisibility(View.VISIBLE);
        }else {
            categoryLayout.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public void dataSourceSaved(String message) {
        //add newly added items to spinner. Not working currently
        mSourcesPresenter.getSources();

        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
        enableSecondaryLayout(false);
        //If called instantly after save button is clicked, will make the reveal(hide) animation lag a little bit,
        //so run the animation after a certain period of time.
        //mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(toolbar, 500);
                drawer.setEnabled(true);
                if (SettingsPreferences.CIRCULAR_REVEAL) {
                    mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
                } else {
                    new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(secondaryLayout, 500);
                    new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(mainLayout, 500);
                }
            }
        }, 500);
        mAddFeedStatus = false;
        categoryLayout.setVisibility(View.INVISIBLE);
        clearSourceValues();
    }

    @Override
    public void dataSourceSaveFailed(String message) {
        if (message.equals("name_url_category_empty")) {
            message = "Name, url and category cannot be empty";
            eTxtSourceName.setError("Enter a valid name");
            eTxtSourceUrl.setError("Enter a valid url");
        } else if (message.equals("name_empty")) {
            message = "Name cannot be empty";
            eTxtSourceName.setError("Enter a valid name");
        } else if (message.equals("url_empty")) {
            message = "Url cannot be empty";
            eTxtSourceUrl.setError("Enter a valid url");
        } else if (message.equals("category_empty")) {
            message = "Category cannot be empty";
        } else if (message.equals("incorrect_url")) {
            message = "Invalid url";
            eTxtSourceUrl.setError("Enter a valid url");
        }
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dataSourceLoaded(List<String> sourceNames) {
        //Toast.makeText(HomeActivity.this, "data source loaded", Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.spinner_text, sourceNames);
        spinnerSources.setAdapter(adapter);
    }

    //do not use it
    @Override
    public void dataSourceItemsLoaded(List<SourceItem> sourceItems) {

    }

    @Override
    public void dataSourceLoadingFailed(String message) {
        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    //No use
    @Override
    public void sourceItemModified(SourceItem sourceItem, String oldName) {

    }

    //No use
    @Override
    public void sourceItemModificationFailed(String message) {

    }

    //No use
    @Override
    public void sourceItemDeleted(SourceItem sourceItem) {

    }

    //No use
    @Override
    public void sourceItemDeletionFailed(String message) {

    }

    private void clearSourceValues() {
        eTxtSourceName.setText("");
        eTxtSourceUrl.setText("");
        imgCategory.setImageDrawable(null);
        txtCategory.setText(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mAddFeedStatus) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(toolbar, 500);
                    if (SettingsPreferences.CIRCULAR_REVEAL) {
                        mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
                    } else {
                        new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(secondaryLayout, 500);
                        new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(mainLayout, 500);
                    }
                }
            }, 500);
            mAddFeedStatus = false;
        } else {
            super.onBackPressed();
        }

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

    }
}
