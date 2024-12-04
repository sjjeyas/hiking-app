package com.example.project;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivityLocation {
    private static final String TAG = "MainActivityLocation";

    // Fetch LatLng from ZIP code
    public static LatLng getLocationFromZipcode(Context context, String zipcode) {
        Log.d(TAG, "Attempting to fetch location for ZIP code: " + zipcode);
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(zipcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                Log.d(TAG, "Geocoder returned location: " + address.getLatitude() + ", " + address.getLongitude());
                return new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                Log.e(TAG, "No results from Geocoder for ZIP code: " + zipcode);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed for ZIP code: " + zipcode + " with error: " + e.getMessage());
        }
        return null;
    }

    // Fetch LatLng from city name
    public static LatLng getLocationFromCity(Context context, String city) {
        Log.d(TAG, "Attempting to fetch location for city: " + city);
        Geocoder geocoder = new Geocoder(context, Locale.US);
        try {
            List<Address> addresses = geocoder.getFromLocationName(city, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed for city: " + city + " - " + e.getMessage());
        }
        return null;
    }

    // Calculate distance between two LatLng points
    public static double calculateDistance(LatLng start, LatLng end) {
        Log.d(TAG, "Calculating distance between points: " + start + " and " + end);
        double earthRadius = 6371; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(end.latitude - start.latitude);
        double dLng = Math.toRadians(end.longitude - start.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(start.latitude)) * Math.cos(Math.toRadians(end.latitude)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c; // Distance in kilometers
    }
}
