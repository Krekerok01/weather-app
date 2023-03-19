package com.example.weatherapp.controller;

import com.example.weatherapp.dto.request.DateIntervalDto;
import com.example.weatherapp.dto.response.AverageDailyTemperatureDto;
import com.example.weatherapp.dto.response.WeatherReadDto;
import com.example.weatherapp.service.WeatherDataService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/weather")
public class WeatherController {

    private WeatherDataService weatherDataService;

    @Autowired
    public WeatherController(WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    @GetMapping
    public ResponseEntity<WeatherReadDto> getCurrentWeather() {
        return new ResponseEntity<>(weatherDataService.getCurrentWeather(), HttpStatus.OK);
    }

    @GetMapping("/temperature")
    public ResponseEntity<AverageDailyTemperatureDto> getAverageDailyTemperature(@Valid @RequestBody(required = false)
        DateIntervalDto dateIntervalDto) {
        if (dateIntervalDto == null){
            return new ResponseEntity<>(
                weatherDataService.getAverageDailyTemperature(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                weatherDataService.getAverageDailyTemperatureBetweenDates(dateIntervalDto), HttpStatus.OK);
        }
    }
}
