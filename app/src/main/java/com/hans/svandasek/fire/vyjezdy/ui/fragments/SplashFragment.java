package com.hans.svandasek.fire.vyjezdy.ui.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hans.svandasek.fire.vyjezdy.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kartik_ch on 12/17/2015.
 */
public class SplashFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @Bind(R.id.relative_layout_fragment_splash)
    RelativeLayout relativeLayoutSplash;
    @Bind(R.id.text_view_splash_title)
    TextView txtSplashTitle;
    @Bind(R.id.text_view_splash_desc)
    TextView txtSplashDesc;
    @Bind(R.id.image_view_splash)
    ImageView imgSplash;
    @Bind(R.id.image_view_empty_dot_1)
    ImageView imgEmptyDot1;
    @Bind(R.id.image_view_empty_dot_2)
    ImageView imgEmptyDot2;
    @Bind(R.id.image_view_empty_dot_3)
    ImageView imgEmptyDot3;
    @Bind(R.id.image_view_empty_dot_4)
    ImageView imgEmptyDot4;
    @Bind(R.id.image_view_selected_dot_1)
    ImageView imgSelectedDot1;
    @Bind(R.id.image_view_selected_dot_2)
    ImageView imgSelectedDot2;
    @Bind(R.id.image_view_selected_dot_3)
    ImageView imgSelectedDot3;
    @Bind(R.id.image_view_selected_dot_4)
    ImageView imgSelectedDot4;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SplashFragment newInstance(int sectionNumber) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        txtSplashTitle.setText(getString(R.string.section_format, sectionNumber));


        switch (sectionNumber) {
            case 1:
                txtSplashTitle.setText(R.string.splash_title_1);
                txtSplashDesc.setText(R.string.splash_desc_1);
                imgSplash.setImageResource(R.drawable.splash_1);
                imgSelectedDot1.setVisibility(View.VISIBLE);
                imgSelectedDot2.setVisibility(View.INVISIBLE);
                imgSelectedDot3.setVisibility(View.INVISIBLE);
                imgSelectedDot4.setVisibility(View.INVISIBLE);
                break;
            case 2:
                txtSplashTitle.setText(R.string.splash_title_2);
                txtSplashDesc.setText(R.string.splash_desc_2);
                GlideDrawableImageViewTarget splashScreen = new GlideDrawableImageViewTarget(imgSplash);
                Glide.with(this).load(R.raw.districts_anim).into(splashScreen);
                imgSelectedDot1.setVisibility(View.INVISIBLE);
                imgSelectedDot2.setVisibility(View.VISIBLE);
                imgSelectedDot3.setVisibility(View.INVISIBLE);
                imgSelectedDot4.setVisibility(View.INVISIBLE);

                break;
            case 3:
                txtSplashTitle.setText(R.string.splash_title_3);
                txtSplashDesc.setText(R.string.splash_desc_3);
                imgSplash.setImageResource(R.drawable.splash_3);
                imgSelectedDot1.setVisibility(View.INVISIBLE);
                imgSelectedDot2.setVisibility(View.INVISIBLE);
                imgSelectedDot3.setVisibility(View.VISIBLE);
                imgSelectedDot4.setVisibility(View.INVISIBLE);
                break;
            case 4:
                txtSplashTitle.setText(R.string.splash_title_4);
                txtSplashDesc.setText(R.string.splash_desc_4);
                imgSplash.setImageResource(R.drawable.splash_3);
                imgSelectedDot1.setVisibility(View.INVISIBLE);
                imgSelectedDot2.setVisibility(View.INVISIBLE);
                imgSelectedDot3.setVisibility(View.INVISIBLE);
                imgSelectedDot4.setVisibility(View.VISIBLE);
                break;
        }
    }


}
