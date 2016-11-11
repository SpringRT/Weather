package com.a11.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a11.weather.data.API;
import com.a11.weather.data.models.APIWeather;
import com.a11.weather.data.models.Weather;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String baseUrl;
    public static String appId;
    private EditText edCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseUrl = getString(R.string.base_url);
        appId = getString(R.string.APP_ID);
        setContentView(R.layout.activity_main);
        edCity = (EditText) findViewById(R.id.etCity);
    }

    private void setScreen(Weather weather){
        String mask = "%.2f - %s";
        ImageView icon = (ImageView) findViewById(R.id.imageView);
        TextView city = (TextView) findViewById(R.id.tv_city);
        TextView state = (TextView) findViewById(R.id.textView3);

        Glide.with(this).load(weather.getImg()).crossFade().into(icon);
        city.setText(String.format(Locale.getDefault(), mask, weather.getTemp(), weather.getCity()));
        state.setText(weather.getState());
    }

    public void onClickOK(View v) {
        String city = edCity.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);

        Map<String, String> query = new HashMap<>();
        query.put("APPID", appId);
        query.put("q", city);

        api.getWeather(query).enqueue(new Callback<APIWeather>() {
            @Override
            public void onResponse(Call<APIWeather> call, Response<APIWeather> response) {
                Log.e("MyTag", call.request().toString());
                if(response.isSuccessful()) {
                    setScreen(Weather.parse(response.body()));
                }
                else {
                    Toast.makeText(MainActivity.this, "Error - wrong city", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIWeather> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error - have no connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
