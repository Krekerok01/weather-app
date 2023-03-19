package com.example.weatherapp.dto.response;

import lombok.Value;

@Value
public class WeatherReadDto {

    String location;
    Integer temperature;
    Double windMetersPerHour;
    Double pressureMB;
    Double humidity;
    String weatherCondition;
}