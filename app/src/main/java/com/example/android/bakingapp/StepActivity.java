

//Copyright 2017 Bogdan Kornev.
//
//        Licensed under the Apache License, Version 2.0 (the "License");
//        you may not use this file except in compliance with the License.
//        You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Unless required by applicable law or agreed to in writing, software
//        distributed under the License is distributed on an "AS IS" BASIS,
//        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//        See the License for the specific language governing permissions and
//        limitations under the License.

package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

public class StepActivity extends AppCompatActivity {

    Recipe currentRecipe;
    Step selectedStep;
    ArrayListFragment currentFragment;
    ArrayList<Step> steps;
    StepView stepBar;
    StepFragmentPagerAdapter pagerAdapter;
    ViewPager viewPager;
    static boolean isLandscape = false;

    static final String KEY_RECIPE = "recipe";
    static final String KEY_STEP = "step";
    static final String KEY_CURRENT_FRAGMENT = "current_fragment";

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
            currentFragment = (ArrayListFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, KEY_CURRENT_FRAGMENT);

        }

        //find out if the device is in landscape mode
        stepBar = findViewById(R.id.step_bar);
        isLandscape = stepBar == null;
        steps = currentRecipe.getSteps();
        if (!isLandscape){
            stepBar.setStepsNumber(steps.size());
            stepBar.getState()
                    .animationType(StepView.ANIMATION_NONE)
                    .commit();
        }

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new StepFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(selectedStep.getId());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ArrayListFragment.isFocusGranted){
            ArrayListFragment.abandonAudioFocus();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_RECIPE, currentRecipe);
        outState.putParcelable(KEY_STEP, selectedStep);
        if (currentFragment.isAdded()){
            getSupportFragmentManager().putFragment(outState, KEY_CURRENT_FRAGMENT, currentFragment);
        }

        super.onSaveInstanceState(outState);
    }


    public List<Step> getSteps() {
        return steps;
    }


    public class StepFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private StepFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position, steps);
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
            ArrayListFragment thisFragment = (ArrayListFragment) super.instantiateItem(container, position);
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
            if (!isLandscape){
                stepBar.go(position, true);
            }
            super.setPrimaryItem(container, position, object);
        }




    }

}
