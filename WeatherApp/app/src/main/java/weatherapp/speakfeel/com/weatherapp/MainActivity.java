package weatherapp.speakfeel.com.weatherapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

import android.location.LocationListener;
import android.location.LocationManager;


public class MainActivity extends Activity implements View.OnClickListener {
    //TextView city;
    TextView temperature;
    TextView summary;
    TextView dateTime;
    Button getTemperatureButton;

    //URL to get JSON Array
    private static final String URL = "https://api.forecast.io/forecast/";
    private static final String API_KEY = "a8b2ddaedd8d59e023e1ed19db054fb9";
    private static final String TAG_CURRENTLY = "currently";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_TEMPERATURE = "temperature";
    private static final String DEGREE  = "\u00b0";

    JSONObject currently = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(0xFF00e5ee));
        bar.setTitle("Speakfeel Weather App");

        getTemperatureButton = (Button) findViewById(R.id.getTemperatureButton);
        getTemperatureButton.setOnClickListener(this);
        new JSONParse().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public long convertFahreheitToTemperature(Double FahrenheitTemp){
        return Math.round((((FahrenheitTemp - 32) * 5)/9)*10)/10;
    }

    @Override
    public void onClick(View view){

        new JSONParse().execute();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LocationManager locationManager = (LocationManager)
                    getSystemService(getApplicationContext().LOCATION_SERVICE);
            //Check GPS is on
            LocationListener locationListener = new LocationListenerWrapper();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            dateTime = (TextView)findViewById(R.id.dateTime);
            summary = (TextView)findViewById(R.id.summary);
            temperature = (TextView)findViewById(R.id.temperature);
            //city = (TextView) findViewById(R.id.city);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Weather...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject json = null;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(URL+API_KEY+"/"+ LocationListenerWrapper.latitude + "," + LocationListenerWrapper.longitude);
                urlConnection = (HttpURLConnection) url
                        .openConnection();
                InputStream in = urlConnection.getInputStream();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);
                json = new JSONObject(responseStrBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace(); //If you want further info on failure...
                }
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                //String sCity = "";
                currently = json.getJSONObject(TAG_CURRENTLY);
                /*sCity = LocationListenerWrapper.getCityName( getBaseContext(),
                        LocationListenerWrapper.longitude,LocationListenerWrapper.latitude);*/
                String weatherTemperature = currently.getString(TAG_TEMPERATURE);
                String weatherSummary = currently.getString(TAG_SUMMARY);
                long celsiusTemp = convertFahreheitToTemperature(Double.parseDouble(weatherTemperature));
                //city.setText(sCity);

                temperature.setText(Long.toString(celsiusTemp) + DEGREE + "C");
                summary.setText(weatherSummary);

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                Date d = new Date();
                String dayOfTheWeek = sdf.format(d);
                sdf = new SimpleDateFormat("k");
                String hours = sdf.format(d);
                sdf = new SimpleDateFormat("m");
                String minutes = sdf.format(d);
                dateTime.setText(dayOfTheWeek + ", " + hours + ":" + minutes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
