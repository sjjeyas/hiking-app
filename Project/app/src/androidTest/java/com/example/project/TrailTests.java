package com.example.project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TrailTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void trailBlackBoxTest() throws InterruptedException {
        goToTrailSearch();
        onView(withId(R.id.searchList)).check(matches(hasMinimumChildCount(1)));
    }
    @Test
    public void searchSelectTrailTest() throws InterruptedException {
        goToTrailSearch();
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("Big Boulder"), closeSoftKeyboard());
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.searchList))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.trailname)).check(matches(withText("Big Boulder")));
    }
    @Test
    public void seeReviewsBlackBoxTest() throws InterruptedException {
        goToTrailSearch();
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.searchList))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.seereview_button)).perform(click());
        onView(withId(R.id.titlereview)).check(matches(withSubstring("Reviews: ")));
    }

    public void goToTrailSearch() throws InterruptedException{
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        onView(withContentDescription("More options")).perform(click()); //open three dots
        TimeUnit.SECONDS.sleep(1);
        onView(withText(R.string.action_trailsearch)).perform(click());
        TimeUnit.SECONDS.sleep(1);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position)
    {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    };
}
