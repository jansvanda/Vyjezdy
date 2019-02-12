package com.hans.svandasek.fire.vyjezdy.ui.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.models.SettingsPreferences;
import com.hans.svandasek.fire.vyjezdy.models.SourceItem;
import com.hans.svandasek.fire.vyjezdy.sources.ISourceView;
import com.hans.svandasek.fire.vyjezdy.sources.SourcesPresenter;
import com.hans.svandasek.fire.vyjezdy.utils.WebsiteIntentUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {

    public static MaterialProgressBar mMaterialProgressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityTheme();
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setToolBar();

        getFragmentManager().beginTransaction().replace(R.id.frame_layout_settings, new SettingsFragment()).commit();

    }

    private void setActivityTheme() {
        if (!SettingsPreferences.THEME) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(SettingsActivity.this, R.color.darkColorPrimaryDark));
            }
            getWindow().setBackgroundDrawableResource(R.color.darkColorBackground);
        }
    }

    private void setToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, ISourceView {
        private final static int STORAGE_PERMISSION_RC = 1;
        private static String EMAIL_SUBJECT = "Custom curated list - Munch";
        private static String MESSAGE_TYPE = "message/rfc822";
        private static String SOURCE_NAME = "source_name";
        private static String SOURCE_URL = "source_url";
        private static String SOURCE_CATEGORY = "source_category";
        private static String FEEDS_NOT_WORKING_TUTORIAL_URL = "http://crazyhitty.com/blog/2016/january/make-your-feeds-work-with-munch.html";
        private SourcesPresenter mSourcesPresenter;
        private Preference mFeedsNotWorkingPreference;
        private ListPreference district;


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            //bind the preference views
            bindPreferences();

            if (mSourcesPresenter == null) {
                mSourcesPresenter = new SourcesPresenter(this, getActivity());
            }

            //load available sources
            mSourcesPresenter.getSourceItems();

            //add preference click listener
            mFeedsNotWorkingPreference.setOnPreferenceClickListener(this);
        }


        private void bindPreferences() {
            mFeedsNotWorkingPreference = findPreference(getActivity().getResources().getString(R.string.perf_feeds_not_working_key));
            district = (ListPreference) findPreference(getActivity().getResources().getString(R.string.notifications_district));
        }


        @Override
        public boolean onPreferenceClick(Preference preference) {


            if (preference.getKey().equals(getActivity().getResources().getString(R.string.perf_feeds_not_working_key))) {
                if (SettingsPreferences.IN_APP_BROWSER) {
                    new WebsiteIntentUtil(getActivity()).loadCustomChromeTabs(FEEDS_NOT_WORKING_TUTORIAL_URL);
                } else {
                    new WebsiteIntentUtil(getActivity()).loadNormalBrowser(FEEDS_NOT_WORKING_TUTORIAL_URL);
                }
            }

            return false;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object updatedObject) {

            //false is returned so that the preference is not saved
            return false;
        }

        @Override
        public void dataSourceSaved(String message) {
            Log.e("Source Saved", message);
        }

        @Override
        public void dataSourceSaveFailed(String message) {
            Log.e("Source Saving Failed", message);
        }

        //no use
        @Override
        public void dataSourceLoaded(List<String> sourceNames) {

        }

        @Override
        public void dataSourceItemsLoaded(List<SourceItem> sourceItems) {

            CharSequence[] sources = new CharSequence[sourceItems.size()];
            for (int i = 0; i < sourceItems.size(); i++) {
                sources[i] = sourceItems.get(i).getSourceName();
            }
        }

        //no use
        @Override
        public void dataSourceLoadingFailed(String message) {

        }

        //no use
        @Override
        public void sourceItemModified(SourceItem sourceItem, String oldName) {

        }

        //no use
        @Override
        public void sourceItemModificationFailed(String message) {

        }

        //no use
        @Override
        public void sourceItemDeleted(SourceItem sourceItem) {

        }

        //no use
        @Override
        public void sourceItemDeletionFailed(String message) {

        }


    }
}
