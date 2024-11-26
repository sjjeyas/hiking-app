package com.example.project;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ProfileWhiteBoxTest {

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private DatabaseReference mockDatabase;

    @Mock
    private DatabaseReference mockUserRef;

    private ProfileManager profileManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Mock FirebaseAuth
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        // Mock FirebaseDatabase behavior for user profile
        when(mockDatabase.child("testUserId")).thenReturn(mockUserRef);
        Task<DataSnapshot> mockTask = mock(Task.class);
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        when(mockUserRef.child("friends")).thenReturn(mockUserRef);

        // Mock user data
        Map<String, Object> mockUserData = new HashMap<>();
        mockUserData.put("username", "testuser@email.com");
        mockUserData.put("name", "testUserId");
        mockUserData.put("zipcode", "12345");

        // Configure the mock Task and DataSnapshot for user profile
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.getValue()).thenReturn(mockUserData);

        // Mock get() call to return the mockTask for user profile
        when(mockUserRef.get()).thenReturn(mockTask);

        // Mock the friends reference
        DatabaseReference mockFriendsRef = mock(DatabaseReference.class);
        when(mockUserRef.child("friends")).thenReturn(mockFriendsRef);

        // Mock a specific friend's reference
        DatabaseReference mockFriendRef = mock(DatabaseReference.class);
        when(mockFriendsRef.child("friendName")).thenReturn(mockFriendRef);

        // Stub `setValue()` for adding a friend
        Task<Void> mockAddFriendTask = mock(Task.class);
        when(mockAddFriendTask.isSuccessful()).thenReturn(true);
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(0);
            listener.onComplete(mockAddFriendTask); // Simulate successful task completion
            return null;
        }).when(mockFriendRef).setValue(true);

        // Stub `removeValue()` for unfriending
        Task<Void> mockUnfriendTask = mock(Task.class);
        when(mockUnfriendTask.isSuccessful()).thenReturn(true);
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(0);
            listener.onComplete(mockUnfriendTask); // Simulate successful task completion
            return null;
        }).when(mockFriendRef).removeValue();

        // Stub `get()` for checking friend status
        Task<DataSnapshot> mockFriendCheckTask = mock(Task.class);
        DataSnapshot mockFriendSnapshot = mock(DataSnapshot.class);
        when(mockFriendCheckTask.isSuccessful()).thenReturn(true);
        when(mockFriendCheckTask.getResult()).thenReturn(mockFriendSnapshot);
        when(mockFriendSnapshot.exists()).thenReturn(true); // Simulate the friend exists
        doAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockFriendCheckTask); // Simulate successful task completion
            return null;
        }).when(mockFriendRef).get();

        // Initialize ProfileManager
        profileManager = new ProfileManager(mockAuth, mockDatabase);
    }




    @Test
    public void testGetCurrentUserId() {
        String userId = profileManager.getCurrentUserId();
        assertEquals("testUserId", userId);
    }

    @Test
    public void testGetUserProfile() {
        // Verify behavior
        profileManager.getUserProfile("testUserId", task -> {
            assertTrue(task.isSuccessful());
            DataSnapshot snapshot = task.getResult();
            assertNotNull(snapshot);

            // Assert data consistency
            Map<String, Object> userData = (Map<String, Object>) snapshot.getValue();
            assertEquals("testuser@email.com", userData.get("username"));
            assertEquals("testUserId", userData.get("name"));
            assertEquals("12345", userData.get("zipcode"));
        });

        // Verify that get() was called on the correct database reference
        verify(mockUserRef, times(1)).get();
    }

//    @Test
//    public void testAddFriend() {
//        profileManager.addFriend("testUserId", "friendName", task -> {
//            assertTrue(task.isSuccessful());
//        });
//
//        verify(mockUserRef.child("friends").child("friendName"), times(1)).setValue(true);
//    }
//
//    @Test
//    public void testUnfriend() {
//        profileManager.unfriend("testUserId", "friendName", task -> {
//            assertTrue(task.isSuccessful());
//        });
//
//        verify(mockUserRef.child("friends").child("friendName"), times(1)).removeValue();
//    }
//
//    @Test
//    public void testIsFriend() {
//        // Arrange: Mock the `get()` behavior for the friend's reference
//        Task<DataSnapshot> mockFriendCheckTask = mock(Task.class);
//        DataSnapshot mockFriendSnapshot = mock(DataSnapshot.class);
//
//        // Simulate successful task and friend existence
//        when(mockFriendCheckTask.isSuccessful()).thenReturn(true);
//        when(mockFriendCheckTask.getResult()).thenReturn(mockFriendSnapshot);
//        when(mockFriendSnapshot.exists()).thenReturn(true); // Simulate the friend exists
//
//        // Simulate the `get()` method on the friend's reference
//        doAnswer(invocation -> {
//            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
//            listener.onComplete(mockFriendCheckTask); // Simulate task completion
//            return null;
//        }).when(mockUserRef.child("friends").child("friendName")).get();
//
//        // Act: Test the `isFriend` method
//        profileManager.isFriend("testUserId", "friendName", isFriend -> {
//            // Assert: Verify that the callback returns `true` for the friend status
//            assertTrue(isFriend);
//        });
//
//        // Verify: Ensure the correct database reference `get()` method was called
//        verify(mockUserRef.child("friends").child("friendName"), times(1)).get();
//    }


}