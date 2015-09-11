package weatherapp.speakfeel.com.weatherapp;

/**
 * Created by nikhilthiruvengadam on 9/10/15.
 */
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;
import java.io.IOException;
import java.util.*;


public class LocationListenerWrapper implements LocationListener {

    public static double latitude;
    public static double longitude;

    @Override
    public void onLocationChanged(Location loc)
    {
        loc.getLatitude();
        loc.getLongitude();
        latitude=loc.getLatitude();
        longitude=loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        //print "Currently GPS is Disabled";
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        //print "GPS got Enabled";
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    public static String getCityName(Context context, double latitude, double longitude) throws IOException {
        String cityName = "";
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            cityName = addresses.get(0).getLocality();
        }
        return cityName;
    }


}
