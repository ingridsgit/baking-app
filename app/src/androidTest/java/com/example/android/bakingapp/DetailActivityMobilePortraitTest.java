package com.example.android.bakingapp;


import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DetailActivityMobilePortraitTest {


    @Rule
    public ActivityTestRule<MainActivity> detailActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void clickOnStepListView_OpensStepFragment() {
        ViewAction clickOnRecyclerView = actionOnItemAtPosition(0, click());
        onView(withId(R.id.recycler_view_list)).perform(clickOnRecyclerView);

        onData(anything())
                .inAdapterView(withId(R.id.step_list))
                .atPosition(0)
                .perform(click());

        onView(allOf(withId(android.R.id.list), isCompletelyDisplayed()))
                .check(matches(isDisplayed()));

    }

}
