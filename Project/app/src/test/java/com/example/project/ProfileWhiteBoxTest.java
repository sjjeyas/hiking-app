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

        // Mock FirebaseDatabase
        when(mockDatabase.child("testUserId")).thenReturn(mockUserRef);

        profileManager = new ProfileManager(mockAuth, mockDatabase);
    }

    @Test
    public void testGetCurrentUserId() {
        String userId = profileManager.getCurrentUserId();
        assertEquals("testUserId", userId);
    }

    @Test
    public void testGetUserProfile() {
        // Mock Task
        Task<DataSnapshot> mockTask = mock(Task.class);
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        // Mock data to simulate Firebase response
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("username", "testuser");
        mockData.put("name", "Test User");
        mockData.put("zipcode", "12345");

        // Stub Task behavior
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.getValue()).thenReturn(mockData);

        // Stub database `get()` behavior
        doAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockTask); // Simulate task completion
            return null;
        }).when(mockUserRef).get();

        // Verify behavior
        profileManager.getUserProfile("testUserId", task -> {
            assertTrue(task.isSuccessful());
            assertEquals("testuser", ((Map) task.getResult().getValue()).get("username"));
        });

        verify(mockUserRef, times(1)).get();
    }

    @Test
    public void testAddFriend() {
        // Mock Task
        Task<Void> mockTask = mock(Task.class);

        // Stub Task behavior
        when(mockTask.isSuccessful()).thenReturn(true);

        // Stub database `setValue()` behavior
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(0);
            listener.onComplete(mockTask); // Simulate task completion
            return null;
        }).when(mockUserRef).child("friends").child("friendName").setValue(true);

        // Verify behavior
        profileManager.addFriend("testUserId", "friendName", task -> {
            assertTrue(task.isSuccessful());
        });

        verify(mockUserRef.child("friends").child("friendName"), times(1)).setValue(true);
    }

    @Test
    public void testUnfriend() {
        // Mock Task
        Task<Void> mockTask = mock(Task.class);

        // Stub Task behavior
        when(mockTask.isSuccessful()).thenReturn(true);

        // Stub database `removeValue()` behavior
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener = invocation.getArgument(0);
            listener.onComplete(mockTask); // Simulate task completion
            return null;
        }).when(mockUserRef).child("friends").child("friendName").removeValue();

        // Verify behavior
        profileManager.unfriend("testUserId", "friendName", task -> {
            assertTrue(task.isSuccessful());
        });

        verify(mockUserRef.child("friends").child("friendName"), times(1)).removeValue();
    }

    @Test
    public void testIsFriend() {
        // Mock Task
        Task<DataSnapshot> mockTask = mock(Task.class);
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        // Stub Task behavior
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.exists()).thenReturn(true);

        // Stub database `get()` behavior
        doAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockTask); // Simulate task completion
            return null;
        }).when(mockUserRef).child("friends").child("friendName").get();

        // Verify behavior
        profileManager.isFriend("testUserId", "friendName", isFriend -> {
            assertTrue(isFriend);
        });

        verify(mockUserRef.child("friends").child("friendName"), times(1)).get();
    }


}