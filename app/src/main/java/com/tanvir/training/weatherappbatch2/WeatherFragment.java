package com.tanvir.training.weatherappbatch2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.tanvir.training.weatherappbatch2.adapters.ForecastAdapter;
import com.tanvir.training.weatherappbatch2.current.CurrentResponseModel;
import com.tanvir.training.weatherappbatch2.databinding.FragmentWeatherBinding;
import com.tanvir.training.weatherappbatch2.forecast.ForecastResponseModel;
import com.tanvir.training.weatherappbatch2.permissions.LocationPermission;
import com.tanvir.training.weatherappbatch2.prefs.WeatherPreference;
import com.tanvir.training.weatherappbatch2.utils.Constants;
import com.tanvir.training.weatherappbatch2.viewmodels.WeatherViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherFragment extends Fragment {
    private WeatherViewModel viewModel;
    private WeatherPreference preference;
    private FusedLocationProviderClient providerClient;
    private FragmentWeatherBinding binding;
    private ActivityResultLauncher<String> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            detectUserLocation();
                        } else {
                            // show a dialog and explain user why you need
                            //this permission
                        }
                    });

    @SuppressLint("MissingPermission")
    private void detectUserLocation() {
        providerClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) return;
                    viewModel.setLocation(location);
                    viewModel.loadData();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.e("WeatherApp", "lat: "+latitude+",lon: "+longitude);
                });
    }

    public WeatherFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater);
        preference=new WeatherPreference(getActivity());
        viewModel = new ViewModelProvider(requireActivity())
                .get(WeatherViewModel.class);
        providerClient = LocationServices
                .getFusedLocationProviderClient(getActivity());
        binding.tempUnitSwitch.setChecked(preference.getTempStatus());
        viewModel.setUnit(preference.getTempStatus());

        final ForecastAdapter adapter = new ForecastAdapter();
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);
        binding.forecastRV.setLayoutManager(llm);
        binding.forecastRV.setAdapter(adapter);
        if (LocationPermission.isLocationPermissionGranted(getActivity())) {
            detectUserLocation();
        }else {
            LocationPermission.requestLocationPermission(launcher);
        }

        viewModel.getCurrentLiveData().observe(getViewLifecycleOwner(),
                current -> {
            binding.currentTempTV.setText(
                    String.format("%.0f\u00B0",
                            current.getMain().getTemp()));

            binding.currentFeelsLikeTV.setText(
                            String.format("feels like %.0f\u00B0",
                                    current.getMain().getFeelsLike()));

            binding.currentMaxMinTV.setText(
                            String.format("Max: %.0f\u00B0 Min: %.0f\u00B0",
                                    current.getMain().getTempMax(),
                                    current.getMain().getTempMin()));

            binding.currentDateTV.setText(
                    new SimpleDateFormat("MMM dd, yyyy")
                    .format(new Date(current.getDt() * 1000L))
            );

            binding.currentAddressTV.setText(current.getName()+","+
                    current.getSys().getCountry());

            final String iconUrl = Constants.ICON_PREFIX+
                    current.getWeather().get(0).getIcon()+
                    Constants.ICON_SUFFIX;
            Picasso.get().load(iconUrl).into(binding.currentIconIV);

            binding.currentConditionTV.setText(
                    current.getWeather().get(0).getDescription());

            binding.currentHumidityTV.setText("Humidity "+
                    current.getMain().getHumidity()+"%");

            binding.currentPressureTV.setText("Pressure "+
                            current.getMain().getPressure()+"hPa");

        });

        viewModel.getForecastLiveData().observe(getViewLifecycleOwner(),
                forecast -> {
            adapter.submitList(forecast.getList());
        });
        binding.tempUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preference.setTempStatus(isChecked);
                viewModel.setUnit(isChecked);
                viewModel.loadData();

            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}