package com.example.weatherapp.service;

import com.example.weatherapp.dto.request.DateIntervalDto;
import com.example.weatherapp.dto.response.AverageDailyTemperatureDto;
import com.example.weatherapp.dto.response.WeatherReadDto;
import com.example.weatherapp.entity.WeatherData;
import com.example.weatherapp.exception.InvalidDateException;
import com.example.weatherapp.exception.WeatherAPIException;
import com.example.weatherapp.exception.WeatherDataNotFoundException;
import com.example.weatherapp.repository.WeatherDataRepository;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class WeatherDataServiceImpl implements WeatherDataService {

    private WeatherDataRepository weatherDataRepository;

    @Autowired
    public WeatherDataServiceImpl(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }


    @Override
    public WeatherReadDto getCurrentWeather() {
        WeatherData weatherData = weatherDataRepository.findFirstByOrderByCreatedAtDesc()
            .orElseThrow(() -> new WeatherDataNotFoundException("There are no weather records in the database."));
        return new WeatherReadDto(weatherData.getLocation(),
            weatherData.getTemperature(),
            weatherData.getWindMetersPerHour(),
            weatherData.getPressureMB(),
            weatherData.getHumidity(),
            weatherData.getWeatherCondition());
    }

    @Override
    public AverageDailyTemperatureDto getAverageDailyTemperature() {

        LocalDate today = LocalDate.now();
        Instant from = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = today.atStartOfDay()
            .toInstant(ZoneOffset.UTC).plus(Duration.ofDays(1));

        return getAverageDailyTemperatureDto(from, to);
    }



    @Override
    public AverageDailyTemperatureDto getAverageDailyTemperatureBetweenDates(
        DateIntervalDto dateIntervalDto) {

        String firstDateString = dateIntervalDto.getFrom();
        String secondDateString = dateIntervalDto.getTo();

        Instant from = getLocalDateFromString(firstDateString).atStartOfDay()
            .toInstant(ZoneOffset.UTC);
        Instant to = getLocalDateFromString(secondDateString).atStartOfDay()
            .toInstant(ZoneOffset.UTC).plus(Duration.ofDays(1));

        return getAverageDailyTemperatureDto(from, to);
    }

    private AverageDailyTemperatureDto getAverageDailyTemperatureDto(Instant from, Instant to) {
        List<WeatherData> weatherDataList = weatherDataRepository.findByCreatedAtBetween(from, to);

        int averageTemperature = (int) Math.round(
            weatherDataList.stream()
                .mapToInt(WeatherData::getTemperature)
                .average()
                .orElseThrow(() -> new WeatherDataNotFoundException("This time interval is not in the database"))
        );
        return new AverageDailyTemperatureDto(averageTemperature);
    }


    private LocalDate getLocalDateFromString(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate;
        } catch (Exception e){
            throw new InvalidDateException("Incorrect data entered");
        }
    }



    @Scheduled(fixedDelayString = "${fixed.rate.milliseconds}")
    private void createWeatherDataRecord() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q=Minsk"))
                .header("X-RapidAPI-Key", "3822c89bd8msha434f782a2855a8p15e769jsn610c9e2a5979")
                .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = Json.createReader(new StringReader(response.body()))
                .readObject();
            saveNewWeatherData(jsonObject);

        } catch (IOException | InterruptedException e) {
            throw new WeatherAPIException(e);
        }
    }

    private void saveNewWeatherData(JsonObject jsonObject) {
        String location = jsonObject.getJsonObject("location").getString("region");
        int temperature = jsonObject.getJsonObject("current").getJsonNumber("temp_c").intValue();
        double windSpeed = jsonObject.getJsonObject("current").getJsonNumber("wind_kph").doubleValue();
        double pressure = jsonObject.getJsonObject("current").getJsonNumber("pressure_mb").doubleValue();
        double humidity = jsonObject.getJsonObject("current").getJsonNumber("humidity").doubleValue();
        String condition = jsonObject.getJsonObject("current").getJsonObject("condition").getString("text");

        WeatherData weatherData = WeatherData.builder()
            .location(location)
            .temperature(temperature)
            .windMetersPerHour(windSpeed * 1000)
            .pressureMB(pressure)
            .humidity(humidity)
            .weatherCondition(condition)
            .createdAt(Instant.now())
            .build();
        weatherDataRepository.save(weatherData);
    }
}
