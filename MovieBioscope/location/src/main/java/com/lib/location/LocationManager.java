package com.lib.location;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.lib.location.model.LocationInfo;
import com.lib.location.util.LocationInterface;
import com.lib.location.util.LocationUtil;
import com.lib.location.volley.VolleyUtil;
import com.lib.utility.util.CustomIntent;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by aarokiax on 2/13/2017.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final String TAG = "LocationManager";

    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;

    public void getCurrentLocation() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
            if (null != mGoogleApiClient && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            } else {
                getLocation();
            }
        }
    }

    protected void getLocation() {
        if(null==mLocationRequest){
            configureLocationRequest();
        }
        //checkForPermission();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            processLocation(mLastLocation);

        }
    }

    /*private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(LocationApplication.getLocationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }*/

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(LocationApplication.getLocationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected synchronized void configureLocationRequest(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(LocationInterface.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(LocationInterface.FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(LocationInterface.MINIMUM_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged :: "+location.toString());
        processLocation(location);
    }

    private void processLocation(Location newLocation){
        if (null != newLocation) {
            Log.d(TAG, "Latitude" + newLocation.getLatitude() + "Long" + newLocation.getLongitude());
            LocationInfo routeData = LocationUtil.getRouteInfo();
            if (null != routeData) {
                VolleyUtil.getLocationInfo(newLocation.getLatitude(), newLocation.getLongitude(), routeData.getSource(), routeData.getDestination());
            }
            Geocoder geocoder = new Geocoder(LocationApplication.getLocationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                // In this we get just a single address.
                addresses = geocoder.getFromLocation(newLocation.getLatitude(), newLocation.getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (null != address) {
                        String subLocality = address.getSubLocality();
                        String city = address.getLocality();
                        String currentArea=null;
                        if(null!=address.getAddressLine(1)) {
                            currentArea = address.getAddressLine(0)+" "+address.getAddressLine(1);
                        }
                        if (null != subLocality) {
                            LocationUtil.updateCurentLocation(subLocality,city,currentArea);
                        } else {
                            LocationUtil.updateCurentLocation(address.getLocality(),null,currentArea);

                        }
                        LocalBroadcastManager.getInstance(LocationApplication.getLocationContext()).sendBroadcast(new Intent(CustomIntent.ACTION_CURRENT_LOCATION_CHANGED));
                    }
                }
            } catch (IOException ioException) {
                Log.e(TAG, ioException.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e(TAG, "Latitude = " + newLocation.getLatitude() +
                        ", Longitude = " + newLocation.getLongitude(), illegalArgumentException);
            }

        }
    }
}
