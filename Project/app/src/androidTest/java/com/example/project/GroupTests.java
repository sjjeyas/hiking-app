package com.example.project;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class GroupTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    private View decorView;
    @Test
    public void groupBlackBoxTest() throws InterruptedException {
        goToGroupSearch();
        onView(withId(R.id.searchList)).check(matches(hasMinimumChildCount(1)));
    }
    @Test
    public void makeGroupActivityTest() throws InterruptedException {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups").child("Test Group");

        // Check if "Test Group" exists in groups
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Delete the group if it exists
                    groupRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Test", "Existing group 'Test Group' deleted successfully.");
                        } else {
                            fail("Failed to delete existing group: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fail("Firebase query cancelled: " + databaseError.getMessage());
            }
        });

        goToGroupSearch();
        onView(withId(R.id.addgroup_button)).perform(click());
        TimeUnit.SECONDS.sleep(1);

        //check if on makeGroup
        onView(withId(R.id.makeGroupText)).check(matches(isDisplayed()));

        //make a Group
        onView(withId(R.id.groupNameInput)).perform(typeText("Test Group"), closeSoftKeyboard());
        onView(withId(R.id.trailNameInput)).perform(typeText("Big Boulder"), closeSoftKeyboard());
        onView(withId(R.id.capacityInput)).perform(typeText("5"), closeSoftKeyboard());
        onView(withId(R.id.submit_button)).perform(click());
        TimeUnit.SECONDS.sleep(1);

        // Check "testGroup" was added to the database
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assertTrue("Group 'Test Group' should exist", dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                fail("Firebase query cancelled: " + databaseError.getMessage());
            }
        });
    }
    @Test
    public void searchSelectGroupTest() throws InterruptedException {
        goToGroupSearch();
        onView(withId(R.id.searchView)).perform(click());
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText("USC Peaks"), closeSoftKeyboard());
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.searchList))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.groupname)).check(matches(withText("USC Peaks")));
    }

    public void goToGroupSearch() throws InterruptedException{
        onView(withId(R.id.editusername)).perform(typeText("joebiden@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword)).perform(typeText("joebiden"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        TimeUnit.SECONDS.sleep(3);
        onView(withContentDescription("More options")).perform(click()); //open three dots
        TimeUnit.SECONDS.sleep(1);
        onView(withText(R.string.action_groupsearch)).perform(click());
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
