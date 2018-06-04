//package com.example.android.bakingapp;
//
//
//import android.app.Activity;
//import android.app.Instrumentation;
//import android.content.Context;
//import android.content.Intent;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.espresso.DataInteraction;
//import android.support.test.espresso.IdlingRegistry;
//import android.support.test.espresso.ViewInteraction;
//import android.support.test.espresso.intent.Intents;
//import android.support.test.espresso.intent.matcher.ComponentNameMatchers;
//import android.support.test.espresso.intent.rule.IntentsTestRule;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static android.support.test.espresso.Espresso.onData;
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.Espresso.pressBack;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
//import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
//import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.Intents.intending;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
//import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
//import static android.support.test.espresso.Espresso.onData;
//import static android.support.test.espresso.Espresso.onView;
//
//import static org.hamcrest.Matchers.not;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.anything;
//import static org.hamcrest.Matchers.is;
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class DetailActivityMobilePortraitTest {
//
//    @Rule
//    public ActivityTestRule<DetailActivity> detailActivityTestRule =
//            new ActivityTestRule<DetailActivity>(DetailActivity.class){
////                @Override
////                protected Intent getActivityIntent() {
////                    Context targetContext = InstrumentationRegistry.getInstrumentation()
////                            .getTargetContext();
////                    Intent starterIntent = new Intent(targetContext, DetailActivity.class);
////                    starterIntent.putExtra("RECIPE", NUTELLA_PIE_RECIPE);
////                    return starterIntent;
////
////                }
//            };
//
////    private MyIdlingResource myIdlingResource;
//    private final Ingredient[] INGREDIENTS_ARRAY = new Ingredient[]{
//            new Ingredient(2,"CUP","Graham Cracker crumbs"),
//            new Ingredient(6,"TBLSP","unsalted butter, melted"),
//            new Ingredient(0.5,"CUP","granulated sugar"),
//            new Ingredient(1.5,"TSP","salt"),
//            new Ingredient(5,"TBLSP","vanilla"),
//            new Ingredient(1,"K","Nutella or other chocolate-hazelnut spread"),
//            new Ingredient(500,"G","Mascapone Cheese(room temperature)"),
//            new Ingredient(1,"CUP","heavy cream(cold)"),
//            new Ingredient(4,"OZ","cream cheese(softened)")
//        };
//    private final ArrayList<Ingredient> INGREDIENTS = new ArrayList<>(Arrays.asList(INGREDIENTS_ARRAY));
//    private final Step[] STEPS_ARRAY = new Step[]{
//            new Step(0,"Recipe Introduction","Recipe Introduction",
//                    "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4",""),
//            new Step(1,"Starting prep","1. Preheat the oven to 350\u00b0F. Butter a 9\" deep dish pie pan.","",""),
//            new Step(2,"Prep the cookie crust.", "2. Whisk the graham cracker crumbs, 50 grams (1/4 cup) of sugar, and 1/2 teaspoon of salt together in a medium bowl. " +
//                    "Pour the melted butter and 1 teaspoon of vanilla into the dry ingredients and stir together until evenly mixed.",
//                    "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd9a6_2-mix-sugar-crackers-creampie/2-mix-sugar-crackers-creampie.mp4",""),
//            new Step(3, "Press the crust into baking form.","3. Press the cookie crumb mixture into the prepared pie pan and bake for 12 minutes. Let crust cool to room temperature.",
//        "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd9cb_4-press-crumbs-in-pie-plate-creampie/4-press-crumbs-in-pie-plate-creampie.mp4",""),
//            new Step(4,"Start filling prep","4. Beat together the nutella, mascarpone," +
//                    " 1 teaspoon of salt, and 1 tablespoon of vanilla on medium speed in a stand mixer or high speed with a hand mixer until fluffy.",
//                    "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd97a_1-mix-marscapone-nutella-creampie/1-mix-marscapone-nutella-creampie.mp4",""),
//            new Step(5,"Finish filling prep","5. Beat the cream cheese and 50 grams (1/4 cup) of sugar on medium speed in a stand mixer or high speed " +
//                    "with a hand mixer for 3 minutes. Decrease the speed to medium-low and gradually add in the cold cream. " +
//                    "Add in 2 teaspoons of vanilla and beat until stiff peaks form.",
//                    "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffda20_7-add-cream-mix-creampie/7-add-cream-mix-creampie.mp4", ""),
//            new Step(6,"Finishing Steps","6. Pour the filling into the prepared crust and smooth the top. Spread the whipped cream over the filling. " +
//                    "Refrigerate the pie for at least 2 hours. Then it's ready to serve!", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/" +
//                    "58ffda45_9-add-mixed-nutella-to-crust-creampie/9-add-mixed-nutella-to-crust-creampie.mp4","")};
//    private ArrayList<Step> STEPS = new ArrayList<>(Arrays.asList(STEPS_ARRAY));
//
//    private final Recipe NUTELLA_PIE_RECIPE = new Recipe(
//            1,
//            "Nutella Pie",
//            INGREDIENTS,
//            STEPS,
//            8,
//            "");
//
//    @Before
//    public void stubAllExternalIntents(){
//        Intents.init();
//        intending(not(isInternal()))
//                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
//        Intent openDetailActivity = new Intent();
//        openDetailActivity.putExtra("RECIPE", NUTELLA_PIE_RECIPE);
//
//        intending(toPackage("com.example.android.bakingapp"))
//                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, openDetailActivity));
//    }
//
////    @Before
////    public void registerMyIdlingResource(){
////        myIdlingResource = detailActivityTestRule.getActivity().getMyIdlingResource();
////        IdlingRegistry.getInstance().register(myIdlingResource);
////    }
//
//    @Test
//    public void clickOnStepListView_OpensStepFragment() {
//
//
//
//
//
////        onData(anything()).inAdapterView(withId(R.id.step_list)).atPosition(0).perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        onView(withId(R.id.servings_text_view)).check(matches(withText("Ingredients for 8 people:")));
//        onView(allOf(withId(R.id.description_view), isCompletelyDisplayed()))
//                .check(matches(isDisplayed()));
//
//
//    }
//
//    @After
//    public void releaseIntents(){
//        Intents.release();
//    }
//
//
//
//
//    private static Matcher<View> childAtPosition(
//            final Matcher<View> parentMatcher, final int position) {
//
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("Child at position " + position + " in parent ");
//                parentMatcher.describeTo(description);
//            }
//
//            @Override
//            public boolean matchesSafely(View view) {
//                ViewParent parent = view.getParent();
//                return parent instanceof ViewGroup && parentMatcher.matches(parent)
//                        && view.equals(((ViewGroup) parent).getChildAt(position));
//            }
//        };
//    }
////    @After
////    public void unregisterIdlingResources(){
////        if (myIdlingResource != null){
////            IdlingRegistry.getInstance().unregister(myIdlingResource);
////        }
////    }
//}
