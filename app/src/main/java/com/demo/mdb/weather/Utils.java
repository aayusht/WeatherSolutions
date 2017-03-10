package com.demo.mdb.weather;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hp on 3/9/2017.
 */

public class Utils {
    public static final String API_KEY = "ea7280da022ab1e4d51986fbdf5347bc";
    public static final String URL_BASE_STRING = "https://api.darksky.net/forecast/";
    public static final String LOG_TAG = "rip";

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static class RetrieveDataTask extends AsyncTask<Object, Void, JSONObject> {
        View view;
        @Override
        protected JSONObject doInBackground(Object... viewAndLocation) {
            view = (View) viewAndLocation[0];
            Location l = (Location) viewAndLocation[1];
            String urlString = URL_BASE_STRING + API_KEY + "/" + l.getLatitude() + "," + l.getLongitude();
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String response = convertStreamToString(in);
                JSONObject json = new JSONObject(response);
                return json;
            } catch (Exception e) {
                Log.e(LOG_TAG, urlString);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                ((TextView) view.findViewById(R.id.location)).setText(jsonObject.getJSONObject("currently").getString("temperature"));
                Log.e(LOG_TAG, jsonObject.getJSONObject("currently").getString("temperature"));
                JSONArray jsonArray = jsonObject.getJSONObject("minutely").getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(0).getInt("precipProbability") > 0) {
                        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
                        Date date = new Date(jsonArray.getJSONObject(0).getInt("time"));
                        String time = localDateFormat.format(date);
                        ((TextView) view.findViewById(R.id.raintime)).setText("It will start raining around " + time);
                        break;
                    }
                }
                ((TextView) view.findViewById(R.id.temperature)).setText(jsonObject.getJSONObject("currently").getString("summary"));

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            super.onPostExecute(jsonObject);
        }
    }
}
