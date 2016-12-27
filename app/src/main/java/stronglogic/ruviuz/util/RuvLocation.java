package stronglogic.ruviuz.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by logicp on 12/19/16.
 * Geolocation Helper
 */

public class RuvLocation {
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 11;
    private static final int REQUEST_COARSE_LOCATION_PERMISSION = 13;

    private String provider;

    private LocationManager locationManager;

    private Activity mActivity;

    private GeoListener mListener;

    public static HashMap<String, String> provinceMap = new HashMap<>();

    //TODO create statesMap HashMap

    public RuvLocation(Activity activity, GeoListener listener, LocationManager locationManager) {
        this.mActivity = activity;
        this.mListener = listener;
        this.locationManager = locationManager;
        provinceMap.put("Ontario", "ON");
        provinceMap.put("Quebec", "QC");
        provinceMap.put("Saskwatchewan", "SK");
        provinceMap.put("Manitoba", "MB");
        provinceMap.put("Prince Edward Island", "PEI");
        provinceMap.put("Newfoundland", "NF");
        provinceMap.put("Nova Scotia", "NS");
        provinceMap.put("British Columbia", "BC");
        provinceMap.put("Northwest Territories", "NWT");
        provinceMap.put("Yukon", "YK");
        provinceMap.put("Alberta", "AB");
        provinceMap.put("Nunavut", "NV");
        provinceMap.put("New Brunswick", "NB");
    }

    public interface GeoListener {
        void sendLocation(Location location);
    }

    public boolean init() {
        final LocationManager mLocationManager = locationManager;
        boolean enabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mActivity.startActivity(intent);
        }
        return enabled;
    }

    public void getLocation() {
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            Log.d(TAG, location.toString());
            mListener.sendLocation(location);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mListener.sendLocation(location);
//                int lat = (int) location.getLatitude();
//                int lng = (int) location.getLongitude();
//                String locationString = "Latitude::" + String.valueOf(lat) + "\nLongitude::" + String.valueOf(lng);
//                Log.d(TAG, locationString);
//                locationTV.setText(locationString);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged");
//                switch (status) {
//                    case LocationProvider.AVAILABLE:
//                        locationTV.setText("GPS available again\n");
//                        break;
//                    case LocationProvider.OUT_OF_SERVICE:
//                        locationTV.setText("GPS out of service\n");
//                        break;
//                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                        locationTV.setText("GPS temporarily unavailable\n");
//                        break;
//                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }




}
