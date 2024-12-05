package com.example.project;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.project.MainActivityLocation;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivityLocationTest {

    private Context mockContext;
    private Geocoder mockGeocoder;
    private MockedStatic<Log> logMock;

    @Before
    public void setUp() {
        // Mock context and Geocoder
        mockContext = mock(Context.class);
        mockGeocoder = mock(Geocoder.class);

        // Mock Log.d and Log.e to prevent actual logging during tests
        logMock = Mockito.mockStatic(Log.class);
    }

//    @Test
//    public void testGetLocationFromZipcode() throws IOException {
//        String zipcode = "90001"; // Example ZIP code
//
//        // Mock Geocoder's behavior
//        Address mockAddress = mock(Address.class);
//        mockAddress.setLatitude(34.0522); // Set mock latitude
//        mockAddress.setLongitude(-118.2437); // Set mock longitude
//        List<Address> mockAddresses = Arrays.asList(mockAddress);
//
//        // When Geocoder calls getFromLocationName(), return mock addresses
//        when(mockGeocoder.getFromLocationName(zipcode, 1)).thenReturn(mockAddresses);
//
//        // Call the method
//        LatLng result = MainActivityLocation.getLocationFromZipcode(mockContext, zipcode);
//
//        // Verify the result
//        assertNotNull(result);
//        assertEquals(34.0522, result.latitude, 0.0001); // Check latitude
//        assertEquals(-118.2437, result.longitude, 0.0001); // Check longitude
//
//        // Verify that Log.d was called
//        logMock.verify(() -> Log.d(anyString(), anyString()), times(1)); // Check if Log.d() was called once
//    }
//
//    @Test
//    public void testGetLocationFromCity() throws IOException {
//        String city = "Los Angeles"; // Example city name
//
//        // Mock Geocoder's behavior
//        Address mockAddress = mock(Address.class);
//        mockAddress.setLatitude(34.0522); // Set mock latitude for Los Angeles
//        mockAddress.setLongitude(-118.2437); // Set mock longitude for Los Angeles
//        List<Address> mockAddresses = Arrays.asList(mockAddress);
//
//        // When Geocoder calls getFromLocationName(), return mock addresses
//        when(mockGeocoder.getFromLocationName(city, 1)).thenReturn(mockAddresses);
//
//        // Call the method
//        LatLng result = MainActivityLocation.getLocationFromCity(mockContext, city);
//
//        // Verify the result
//        assertNotNull(result);
//        assertEquals(34.0522, result.latitude, 0.0001); // Check latitude
//        assertEquals(-118.2437, result.longitude, 0.0001); // Check longitude
//
//        // Verify that Log.d was called
//        logMock.verify(() -> Log.d(anyString(), anyString()), times(1)); // Check if Log.d() was called once
//    }

    @Test
    public void testCalculateDistance() {
        // Arrange
        LatLng start = new LatLng(34.0522, -118.2437); // Los Angeles
        LatLng end = new LatLng(36.1699, -115.1398); // Las Vegas

        // Act
        double distance = MainActivityLocation.calculateDistance(start, end);

        // Assert
        assertEquals(370.716, distance, 5.0);
    }

}
