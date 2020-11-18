package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class WeatherForecast extends AppCompatActivity {
    private final String ACTIVITY_NAME = "WeatherForecast";
    ProgressBar progressBar;
    ImageView imageView;
    TextView minTemp;
    TextView maxTemp;
    TextView currentTemp;
    List<String> cities;
    TextView cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        setTitle("Weather Tab");
        imageView = findViewById(R.id.weatherIcon);
        progressBar = findViewById(R.id.progressBar);
        minTemp = findViewById(R.id.minTemperature);
        maxTemp = findViewById(R.id.maxTemperature);
        currentTemp = findViewById(R.id.currentTemperature);
        cityName = findViewById(R.id.cityName);
        progressBar.setVisibility(View.VISIBLE);

        findCity();
    }

    public void findCity() {
        cities = Arrays.asList(getResources().getStringArray(R.array.cities));
        final Spinner citiesSpinner = findViewById(R.id.citySpinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(arrayAdapter);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.VISIBLE);
                new ForecastQuery(cities.get(i)).execute();
                cityName.setText(cities.get(i) + " Weather");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    public class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String min, max, current;
        private Bitmap icon;
        private String city;
        ForecastQuery(String city) {this.city = city;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("onPreExecute", " is called");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.openweathermap.org/" + "data/2.5/weather?q=" +
                        this.city  +"," +"ca&APPID=ff45bc3e8349dc3682e9372f32bda3ce&" +"mode=xml&units=metric");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);
                    int type;
                    while ((type = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        //Are you currently at a Start Tag?
                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("temperature")) {
                                current = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                min = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = parser.getAttributeValue(null, "max");
                                publishProgress(75);
                            } else if (parser.getName().equals("weather")) {
                                String iconName = parser.getAttributeValue(null, "icon");
                                String fileName = iconName + ".png";
                                Log.i(ACTIVITY_NAME, "Looking for file: " + fileName);
                                if (getBaseContext().getFileStreamPath(fileName).exists()) {
                                    FileInputStream fileInputStream = null;
                                    try {
                                        fileInputStream = openFileInput(fileName);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i(ACTIVITY_NAME, "Found the file locally");
                                    icon = BitmapFactory.decodeStream(fileInputStream);
                                } else {
                                    String iconUri = "https://openweathermap.org/img/w/" + fileName;
                                    icon = getImage(new URL(iconUri));
                                    FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                                    icon.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
                                    Log.i(ACTIVITY_NAME, "Downloaded the file from the Internet");
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                }
                                publishProgress(100);
                            }
                        }
                        // Go to the next XML event
                        parser.next();
                    }
                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    in.close();
                }
            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return "";
        };

        public Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;

            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String a) {
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(icon);
            currentTemp.setText(current + "C\u00b0");
            minTemp.setText(min + "C\u00b0");
            maxTemp.setText(max + "C\u00b0");

        }
    }
}