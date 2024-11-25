package com.example.project;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LoginBlackBoxTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    private View decorView;

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void testLoginWrongInfo() throws InterruptedException {
        // Test if the email is the wrong email
        onView(withId(R.id.editusername)).perform(typeText("wrongemail@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(1);

        // Should still be on the login page
        onView(withId(R.id.login_button)).perform(click());

        // Test correct email but wrong password
        onView(withId(R.id.editusername)).perform(replaceText("joebiden@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(replaceText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(1);

        // Should still be on the login page
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    public void testLoginRightInfo() throws InterruptedException {
        // Test if the all info is correct
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);

        // Should route to main page
        onView(withId(R.id.zipSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()));
    }
}