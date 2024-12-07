package com.example.project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static net.bytebuddy.implementation.bind.annotation.RuntimeType.Verifier.check;
import static org.junit.Assert.assertEquals;

import static java.util.EnumSet.allOf;
import static java.util.regex.Pattern.matches;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class addReviewActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);
    @Test
    public void testActivityLaunch() {
        // Verify that a view in TrailActivity is displayed
        onView(withId(R.id.logintext))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @SuppressLint("CheckResult")
    @Test
    public void writeValidReview() {
        //checks if it can write a valid review by 1) logging in 2) using navbar 3) clicking on a trail
        onView(withId(R.id.login_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"));
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"));
        onView(withId(R.id.login_button)).perform(click());
    }
}
