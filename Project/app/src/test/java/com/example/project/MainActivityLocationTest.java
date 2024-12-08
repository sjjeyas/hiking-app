package com.example.project;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.project.MainActivityLocation;
import com.example.project.classes.Trail;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityLocationTest {
    private List<Trail> mockTrails;
    private LatLng userLocation;

    @Before
    public void setUp() {
        // Setup user location
        userLocation = new LatLng(34.0522, -118.2437); // Los Angeles

        // Setup mock trails
        mockTrails = new ArrayList<>();
        mockTrails.add(new Trail("Trail 1", "Trail Location 1", 34.0522, -118.2437)); // Same as user
        mockTrails.add(new Trail("Trail 2", "Trail Location 2", 36.1699, -115.1398)); // Las Vegas
        mockTrails.add(new Trail("Trail 3", "Trail Location 3", 37.7749, -122.4194)); // San Francisco
    }

    @Test
    public void testGetTrailsWithDistances() {
        // Act
        List<Trail> result = MainActivityLocation.getTrailsWithDistances(mockTrails, userLocation);

        // Assert
        assertEquals(0, result.get(0).distanceFromUser, 0.01); // Trail 1 is at the user's location
        assertEquals(367.606, result.get(1).distanceFromUser, 0.01); // Trail 2
        assertEquals(559.124, result.get(2).distanceFromUser, 0.01); // Trail 3
    }

    @Test
    public void testSortTrailsByDistance() {
        // Act
        List<Trail> trailsWithDistances = MainActivityLocation.getTrailsWithDistances(mockTrails, userLocation);
        List<Trail> sortedTrails = MainActivityLocation.sortTrailsByDistance(trailsWithDistances);

        // Assert
        assertEquals("Trail 1", sortedTrails.get(0).name);
        assertEquals("Trail 2", sortedTrails.get(1).name);
        assertEquals("Trail 3", sortedTrails.get(2).name);
    }

    @Test
    public void testGetClosestTrails() {
        // Act
        List<Trail> trailsWithDistances = MainActivityLocation.getTrailsWithDistances(mockTrails, userLocation);
        List<Trail> closestTrails = MainActivityLocation.getClosestTrails(trailsWithDistances, 2);

        // Assert
        assertEquals(2, closestTrails.size());
        assertEquals("Trail 1", closestTrails.get(0).name);
        assertEquals("Trail 2", closestTrails.get(1).name);
    }

    @Test
    public void testCalculateDistanceWithSameLocation() {
        // Arrange
        LatLng point = new LatLng(34.0522, -118.2437); // Los Angeles

        // Act
        double distance = MainActivityLocation.calculateDistance(point, point);

        // Assert
        assertEquals(0, distance, 0.01); // Same location should have 0 distance
    }

    @Test
    public void testCalculateDistanceWithDifferentLocations() {
        // Arrange
        LatLng point1 = new LatLng(34.0522, -118.2437); // Los Angeles
        LatLng point2 = new LatLng(40.7128, -74.0060); // New York City

        // Act
        double distance = MainActivityLocation.calculateDistance(point1, point2);

        // Assert
        assertEquals(3935.75, distance, 0.01); // Distance between LA and NYC
    }

}
