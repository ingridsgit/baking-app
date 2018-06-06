package com.example.android.bakingapp;

import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static android.support.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityMobilePortraitTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickOnRecyclerView_opensDetailActivity() {
        ViewAction clickOnRecyclerView = actionOnItemAtPosition(0, click());
        onView(withId(R.id.recycler_view_list)).perform(clickOnRecyclerView);
        onView(withId(R.id.servings_text_view)).check(matches(withText("Ingredients for 8 people:")));

    }





}
