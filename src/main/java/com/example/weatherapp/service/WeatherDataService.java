package com.example.weatherapp.service;

import com.example.weatherapp.dto.request.DateIntervalDto;
import com.example.weatherapp.dto.response.AverageDailyTemperatureDto;
import com.example.weatherapp.dto.response.WeatherReadDto;

public interface WeatherDataService {

    WeatherReadDto getCurrentWeather();

    AverageDailyTemperatureDto getAverageDailyTemperature();

    AverageDailyTemperatureDto getAverageDailyTemperatureBetweenDates(DateIntervalDto dateIntervalDto);
}
