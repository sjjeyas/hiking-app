//package com.example.project;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//import static org.mockito.Mockito.*;
//
//import android.location.Address;
//import android.location.Geocoder;
//
//import com.example.project.MainActivity;
//import com.google.android.gms.maps.CameraUpdate;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MapWhiteBoxTest {
//
//    private MainActivity mainActivity;
//
//    @Mock
//    private GoogleMap mockMap;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mainActivity = new MainActivity();
//        mainActivity.mMap = mockMap;
//    }
//
//    @Test
//    public void testGetLocationFromZipcode() throws IOException {
//        // Arrange
//        Geocoder mockGeocoder = mock(Geocoder.class);
//        List<Address> mockAddresses = new ArrayList<>();
//        Address mockAddress = mock(Address.class);
//        mockAddresses.add(mockAddress);
//
//        // Mock behavior
//        when(mockGeocoder.getFromLocationName("90007", 1)).thenReturn(mockAddresses);
//        when(mockAddress.getLatitude()).thenReturn(34.0224);
//        when(mockAddress.getLongitude()).thenReturn(-118.2851);
//
//        MainActivity mainActivity = new MainActivity();
//
//        // Act
//        LatLng result = mainActivity.getLocationFromZipcode("90007", mockGeocoder);
//
//        // Assert
//        assertEquals(34.0224, result.latitude, 0.0001);
//        assertEquals(-118.2851, result.longitude, 0.0001);
//    }
//
////    @Test
////    public void testUpdateMapWithNewLocation() {
////        CameraUpdate mockCameraUpdate = mock(CameraUpdate.class);
////        when(CameraUpdateFactory.newLatLngZoom(any(LatLng.class), anyFloat())).thenReturn(mockCameraUpdate);
////
////        LatLng newLocation = new LatLng(34.0522, -118.2437);
////        mainActivity.updateMapWithNewLocation(newLocation);
////
////        verify(mockMap).moveCamera(mockCameraUpdate);
////    }
////
////    @Test
////    public void testFetchTrailsAndDisplayMarkers() {
////        DatabaseReference mockDatabaseReference = mock(DatabaseReference.class);
////        FirebaseDatabase mockFirebaseDatabase = mock(FirebaseDatabase.class);
////        when(mockFirebaseDatabase.getReference("trails")).thenReturn(mockDatabaseReference);
////
////        DataSnapshot mockDataSnapshot = mock(DataSnapshot.class);
////        List<DataSnapshot> mockChildren = new ArrayList<>();
////        DataSnapshot mockChild = mock(DataSnapshot.class);
////        mockChildren.add(mockChild);
////
////        when(mockDataSnapshot.getChildren()).thenReturn(mockChildren);
////        when(mockChild.getValue(Trail.class)).thenReturn(new Trail("Trail Name", "City Name"));
////
////        mainActivity.setDatabaseReference(mockDatabaseReference);
////
////        mainActivity.fetchTrailsAndDisplayMarkers(new LatLng(34.0522, -118.2437));
////        verify(mockMap, atLeastOnce()).addMarker(any());
////    }
//}
