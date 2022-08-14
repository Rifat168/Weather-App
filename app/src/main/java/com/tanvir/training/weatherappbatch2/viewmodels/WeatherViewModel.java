package com.tanvir.training.weatherappbatch2.viewmodels;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tanvir.training.weatherappbatch2.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch2.forecast.ForecastResponseModel;
import com.tanvir.training.weatherappbatch2.network.WeatherService;
import com.tanvir.training.weatherappbatch2.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private Location location;
    private MutableLiveData<CurrentResponseModel> currentLiveData =
            new MutableLiveData<>();
    private MutableLiveData<ForecastResponseModel> forecastLiveData =
            new MutableLiveData<>();
    private String units=Constants.TempUnit.CELCIOUS;

    public void setUnit(boolean status){
        units=status?Constants.TempUnit.FAHRENHEIT:Constants.TempUnit.CELCIOUS;

    }
    public void loadData() {
        fetchCurrentData();
        fetchForecastData();
    }

    private void fetchCurrentData() {
        final String endUrl =
                String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(), location.getLongitude(),
                        units, Constants.WEATHER_API_KEY);
        WeatherService.getService().getCurrentData(endUrl)
                .enqueue(new Callback<CurrentResponseModel>() {
                    @Override
                    public void onResponse(Call<CurrentResponseModel> call, Response<CurrentResponseModel> response) {
                        if (response.code() == 200) {
                            currentLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentResponseModel> call, Throwable t) {

                    }
                });
    }

    private void fetchForecastData() {
        final String endUrl =
                String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",
                        location.getLatitude(), location.getLongitude(),
                        units, Constants.WEATHER_API_KEY);
        WeatherService.getService().getForecastData(endUrl)
                .enqueue(new Callback<ForecastResponseModel>() {
                    @Override
                    public void onResponse(Call<ForecastResponseModel> call, Response<ForecastResponseModel> response) {
                        if (response.code() == 200) {
                            forecastLiveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponseModel> call, Throwable t) {

                    }
                });
    }


    public MutableLiveData<CurrentResponseModel> getCurrentLiveData() {
        return currentLiveData;
    }

    public MutableLiveData<ForecastResponseModel> getForecastLiveData() {
        return forecastLiveData;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
