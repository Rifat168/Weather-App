package com.tanvir.training.weatherappbatch2.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class WeatherPreference {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private WeatherPreference preference;
    public WeatherPreference(Context context){
        preferences=context.getSharedPreferences("weather_pref",Context.MODE_PRIVATE);
        editor=preferences.edit();
    }
    public void setTempStatus(boolean status){
        editor.putBoolean("status",status);
        editor.commit();
    }
    public boolean getTempStatus(){
        return preferences.getBoolean("status",false);
    }
}
