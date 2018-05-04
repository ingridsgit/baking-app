package com.example.android.bakingapp;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class StepActivity extends AppCompatActivity {

    Recipe currentRecipe;
    Step selectedStep;
    StepFragment currentFragment;
    List<Step> steps;
    StepView stepBar;
    StepFragmentPagerAdapter pagerAdapter;
    ViewPager viewPager;
    SimpleExoPlayer simpleExoPlayer;

    static final String KEY_RECIPE = "recipe";
    static final String KEY_STEP = "step";
    static final String KEY_CURRENT_FRAGMENT = "current_fragment";
    static final String KEY_POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        if (savedInstanceState == null){
            selectedStep = getIntent().getParcelableExtra(DetailActivity.KEY_SELECTED_STEP);
            currentRecipe = getIntent().getParcelableExtra(DetailActivity.KEY_SELECTED_RECIPE);
        } else {
            selectedStep = savedInstanceState.getParcelable(KEY_STEP);
            currentRecipe = savedInstanceState.getParcelable(KEY_RECIPE);
            currentFragment = (StepFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, KEY_CURRENT_FRAGMENT);

        }


        steps = currentRecipe.getSteps();

        stepBar = findViewById(R.id.step_bar);
        stepBar.setStepsNumber(steps.size());
        stepBar.getState()
                .animationType(StepView.ANIMATION_NONE)
                .commit();

        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new StepFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedStep.getId());


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_RECIPE, currentRecipe);
        outState.putParcelable(KEY_STEP, selectedStep);
        getSupportFragmentManager().putFragment(outState, KEY_CURRENT_FRAGMENT, currentFragment);
        super.onSaveInstanceState(outState);
    }

    public class StepFragmentPagerAdapter extends FragmentPagerAdapter {


        private StepFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            selectedStep = steps.get(position);
            return StepFragment.newInstance(selectedStep);
        }

        @Override
        public int getCount() {
            return steps.size();
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return steps.get(position).getShortDesc();
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            StepFragment thisFragment = (StepFragment)super.instantiateItem(container, position);
            currentFragment = thisFragment;
            return thisFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, final int position, Object object) {
            container.post(new Runnable() {
                @Override
                public void run() {
                    setTitle(getPageTitle(position));
                }
            });
            stepBar.go(position, true);
            super.setPrimaryItem(container, position, object);
        }

//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//            if (audioFocusRequest != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    audioManager.abandonAudioFocusRequest(audioFocusRequest.build());
//                } else {
//                    audioManager.abandonAudioFocus(audioFocusChangeListener);
//                }
//            }
//        }


    }
}
