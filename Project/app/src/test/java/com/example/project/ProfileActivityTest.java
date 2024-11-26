package com.example.project;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


import android.content.Intent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RunWith(MockitoJUnitRunner.class)
public class ProfileActivityTest {

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
//        when(mockDatabase.child("testUserId")).thenReturn(mockUserRef);

        profileManager = new ProfileManager(mockAuth, mockDatabase);
    }

    @Test
    public void testGetCurrentUserId() {
        String userId = profileManager.getCurrentUserId();
        assertEquals("testUserId", userId);
    }

    @Test
    public void testGetUserProfile() {
        // Mock Firebase task
        Task<DataSnapshot> mockTask = mock(Task.class);
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);
        Map<String, Object> mockData = new HashMap<>();
        mockData.put("username", "testuser");
        mockData.put("name", "Test User");
        mockData.put("zipcode", "12345");

        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
        when(mockSnapshot.getValue()).thenReturn(mockData);

        doAnswer(invocation -> {
            OnCompleteListener<DataSnapshot> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockUserRef).get();

        // Verify that getUserProfile fetches data successfully
        profileManager.getUserProfile("testUserId", task -> {
            assertTrue(task.isSuccessful());
            DataSnapshot snapshot = task.getResult();
            assertEquals("testuser", ((Map) snapshot.getValue()).get("username"));
        });

        verify(mockUserRef, times(1)).get();
    }



}