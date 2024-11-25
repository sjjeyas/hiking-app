//package com.example.project;
//
//import android.view.View;
//
//import androidx.test.espresso.Espresso;
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.assertion.ViewAssertions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import org.hamcrest.CoreMatchers;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(AndroidJUnit4.class)
//public class MapBlackBoxTest {
//
//    @Rule
//    public ActivityScenarioRule<MainActivity> activityRule =
//            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Test
//    public void testMapDefaultLocation() {
//        // Check if the map fragment is displayed
//        Espresso.onView(ViewMatchers.withId(R.id.mapFragmentContainer))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
//
//        // Check if the default location is set (e.g., map camera is initialized)
//        activityRule.getScenario().onActivity(activity -> {
//            LatLng defaultLocation = activity.getLocationFromZipcode("90007");
//            assert defaultLocation != null;
//        });
//    }
//
//    @Test
//    public void testSearchButtonUpdatesMap() {
//        // Enter a valid zip code
//        Espresso.onView(ViewMatchers.withId(R.id.editZip))
//                .perform(ViewActions.typeText("10001"), ViewActions.closeSoftKeyboard());
//
//        // Click the search button
//        Espresso.onView(ViewMatchers.withId(R.id.search_button))
//                .perform(ViewActions.click());
//
//        // Check for a Toast message indicating success or error
//        Espresso.onView(CoreMatchers.any(View.class)).inRoot(new ToastMatcher())
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
//    }
//
//    @Test
//    public void testCurrentLocationButton() {
//        // Click the "current location" button
//        Espresso.onView(ViewMatchers.withId(R.id.default_location))
//                .perform(ViewActions.click());
//
//        // Verify the map is updated with the default location
//        activityRule.getScenario().onActivity(activity -> {
//            LatLng defaultLocation = activity.getLocationFromZipcode("90007");
//            assert defaultLocation != null;
//        });
//    }
//
//    @Test
//    public void testTrailMarkersDisplayed() {
//        // Check that markers are added when map is updated
//        activityRule.getScenario().onActivity(activity -> {
//            LatLng testLocation = new LatLng(34.0522, -118.2437); // Example: Los Angeles
//            activity.updateMapWithNewLocation(testLocation);
//            // Simulate fetching trails and ensure at least one marker is displayed
//            activity.fetchTrailsAndDisplayMarkers(testLocation);
//            assert activity.mMap != null && activity.mMap.getCameraPosition().target.equals(testLocation);
//        });
//    }
//}
