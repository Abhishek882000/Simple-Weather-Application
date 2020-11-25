package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    TextView textView,textView2,txt_temp,textView4
            ,textView5,txt_humidity,
            txt_weatherType,textView3,textView7,textView9,textView11,txt_windSpeed;
    RequestQueue requestQueue;

    AlertDialog.Builder builder;

    String URL = "https://api.openweathermap.org/data/2.5/weather?q=gurgaon,india&APPID=bb2b4c50f22afa6fc2f2c4e56dac0ada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        builder = new AlertDialog.Builder(this);

        requestQueue = Volley.newRequestQueue(this);

        initViews();

        //To check whether we have active internet connection or not
        if (CheckNetwork.isInternetAvailable(MainActivity.this)){
            fetchWeatherData();
        }else{
            showMessage();
            //Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    public void fetchWeatherData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject weather = array.getJSONObject(0);
                    String weatherType = String.valueOf(weather.get("main"));
                    String temp = String.valueOf(jsonObject.getDouble("temp"));
                    String humidity = String.valueOf(jsonObject.get("humidity"));

                    //for wind speed
                    JSONObject wind = response.getJSONObject("wind");
                    String windspeed = String.valueOf(wind.get("speed"));
                    Double windSpeed_int = Double.parseDouble(windspeed);
                    windSpeed_int = (windSpeed_int * 3.6);
                    BigDecimal windoOutput= new BigDecimal(windSpeed_int).setScale(0, RoundingMode.HALF_EVEN);


                    //converting the temprature from kelvin to celcius
                    Double temp_int = Double.parseDouble(temp);
                    Double celsius = temp_int - 273.15;
                    BigDecimal output= new BigDecimal(celsius).setScale(0, RoundingMode.HALF_EVEN);

                    //setting all the values to the textview
                    txt_temp.setText(String.valueOf(output));
                    txt_humidity.setText(humidity);
                    txt_weatherType.setText(weatherType);
                    txt_windSpeed.setText(String.valueOf(windoOutput));

                    Log.d("myapp", "The response is "+weather + " " +temp + " " + output + " " + humidity + "%" + " " +windspeed + " "+windoOutput);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went wrong "+error, Toast.LENGTH_SHORT).show();
                Log.d("myapp", "Error is" + error);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void initViews() {
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);
        txt_humidity = findViewById(R.id.txt_humidity);
        txt_temp = findViewById(R.id.txt_temp);
        txt_weatherType = findViewById(R.id.txt_weatherType);
        textView3 = findViewById(R.id.textView3);
        textView7 = findViewById(R.id.textView7);
        textView9 = findViewById(R.id.textView9);
        textView11 = findViewById(R.id.textView11);
        txt_windSpeed = findViewById(R.id.txt_windSpeed);
    }
    public void showMessage(){
        builder.setMessage("Do you want to close the application?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("No Internet Connection!");
        alert.show();
    }
}
