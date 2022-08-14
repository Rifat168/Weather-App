package com.tanvir.training.weatherappbatch2.network;

import com.tanvir.training.weatherappbatch2.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch2.forecast.ForecastResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {

    @GET
    Call<CurrentResponseModel> getCurrentData(@Url String endUrl);

    @GET
    Call<ForecastResponseModel> getForecastData(@Url String endUrl);
}
