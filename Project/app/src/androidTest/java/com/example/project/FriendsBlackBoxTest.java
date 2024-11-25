package com.example.project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class FriendsBlackBoxTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    private View decorView;

    @Test
    public void testFindFriends() throws InterruptedException {
        // Route to friends page
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        onView(withContentDescription("More options")).perform(click()); //open three dots
        TimeUnit.SECONDS.sleep(1);
        onView(withText(R.string.action_friends)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.find_friends_button)).check(matches(isDisplayed()));

        // Click to go to find friends
        onView(withId(R.id.find_friends_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);

        // Check that page is now on friends search
        onView(withId(R.id.userSearch)).check(matches(isDisplayed()));

        // Search specific user (written with record Espresso test)
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.search_button), withContentDescription("Search"),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_bar),
                                        childAtPosition(
                                                withId(R.id.userSearch),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageView.perform(click());

        ViewInteraction searchAutoComplete = onView(
                allOf(withId(androidx.appcompat.R.id.search_src_text),
                        childAtPosition(
                                allOf(withId(androidx.appcompat.R.id.search_plate),
                                        childAtPosition(
                                                withId(androidx.appcompat.R.id.search_edit_frame),
                                                1)),
                                0),
                        isDisplayed()));
        searchAutoComplete.perform(replaceText("harrystyles"), closeSoftKeyboard());

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.userList),
                        childAtPosition(
                                withId(R.id.main),
                                2)))
                .atPosition(0);
        appCompatTextView2.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

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
    }

    @Test
    public void testAddRemoveFriend() throws InterruptedException{
        // Route to friends page
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        onView(withContentDescription("More options")).perform(click()); //open three dots
        TimeUnit.SECONDS.sleep(1);
        onView(withText(R.string.action_friends)).perform(click());
        TimeUnit.SECONDS.sleep(1);
        onView(withId(R.id.find_friends_button)).check(matches(isDisplayed()));

        // Click on valid friend
        onView(withText("princessdiana")).perform(click());
        TimeUnit.SECONDS.sleep(3);

        // Check that correct user profile has been opened
        onView(withId(R.id.namefield)).check(matches(withText("princessdiana")));
        onView(withId(R.id.dynamic_button)).check(matches(withText("unfriend")));
        TimeUnit.SECONDS.sleep(1);

        // Click on unfriend, check button updates
        onView(withId(R.id.dynamic_button)).perform(click());
        onView(withId(R.id.dynamic_button)).check(matches(withText("add friend")));
        TimeUnit.SECONDS.sleep(1);

        // Click on friend, check button updates
        onView(withId(R.id.dynamic_button)).perform(click());
        onView(withId(R.id.dynamic_button)).check(matches(withText("unfriend")));
        TimeUnit.SECONDS.sleep(1);

    }
}
