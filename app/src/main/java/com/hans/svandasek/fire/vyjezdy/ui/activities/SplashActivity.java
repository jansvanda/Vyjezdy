package com.hans.svandasek.fire.vyjezdy.ui.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.hans.svandasek.fire.vyjezdy.BuildConfig;
import com.hans.svandasek.fire.vyjezdy.R;
import com.hans.svandasek.fire.vyjezdy.models.SettingsPreferences;
import com.hans.svandasek.fire.vyjezdy.ui.adapters.SplashPagerAdapter;
import com.hans.svandasek.fire.vyjezdy.utils.FadePageTransformerUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.ibrahimsn.particle.ParticleView;

public class SplashActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.container)
    ViewPager viewPager;
    @Bind(R.id.image_button_next)
    ImageButton imgBtnNext;

    private SplashPagerAdapter mSplashPagerAdapter;
    private ParticleView particleView;

    AnimationDrawable anim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        runOnce();

        setContentView(R.layout.activity_splash);
        //particleView = findViewById(R.id.particleView_splash);

        ButterKnife.bind(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSplashPagerAdapter = new SplashPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSplashPagerAdapter);
        viewPager.setOffscreenPageLimit(4);
        //set page transformer
        viewPager.setPageTransformer(false, new FadePageTransformerUtil());

        viewPager.addOnPageChangeListener(this);

        LinearLayout anim_layout = findViewById(R.id.animated_container);

        anim = (AnimationDrawable) anim_layout.getBackground();
        anim.setEnterFadeDuration(4000);
        anim.setExitFadeDuration(2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //particleView.resume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //particleView.pause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }



    private void runOnce() {
        if (!SettingsPreferences.isNewInstall(SplashActivity.this)) {
            runIntent(HomeActivity.class);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                imgBtnNext.setImageResource(R.drawable.ic_arrow_forward_24dp);
                break;
            case 1:
                imgBtnNext.setImageResource(R.drawable.ic_arrow_forward_24dp);
                break;
            case 2:
                imgBtnNext.setImageResource(R.drawable.ic_arrow_forward_24dp);
                break;
            case 3:
                imgBtnNext.setImageResource(R.drawable.ic_done_24dp);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick(R.id.image_button_next)
    public void OnNext() {
        if (viewPager.getCurrentItem() == 3) {
            SettingsPreferences.setNewInstall(SplashActivity.this);
            runIntent(HomeActivity.class);
            finish();
        } else if (viewPager.getCurrentItem() < 3) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    }

    private void runIntent(Class resultActivity) {
        Intent intent = new Intent(SplashActivity.this, resultActivity);
        startActivity(intent);
    }

}
