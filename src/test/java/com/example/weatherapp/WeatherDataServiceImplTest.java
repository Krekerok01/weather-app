package com.example.weatherapp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.weatherapp.dto.response.AverageDailyTemperatureDto;
import com.example.weatherapp.dto.response.WeatherReadDto;
import com.example.weatherapp.entity.WeatherData;
import com.example.weatherapp.repository.WeatherDataRepository;
import com.example.weatherapp.service.WeatherDataServiceImpl;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
public class WeatherDataServiceImplTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @InjectMocks
    private WeatherDataServiceImpl weatherDataService;

    @Test
    public void testGetCurrentWeather() {
        WeatherData weatherData = WeatherData.builder()
            .location("Minsk").temperature(6).windMPH(10.0).pressureMB(1013.0).humidity(65.0).weatherCondition("Sunny")
            .createdAt(Instant.now())
            .build();

        Mockito.when(weatherDataRepository.findFirstByOrderByCreatedAtDesc())
            .thenReturn(Optional.of(weatherData));

        WeatherReadDto expected =
            new WeatherReadDto("Minsk", 6, 10.0, 1013.0, 65.0, "Sunny");
        WeatherReadDto actual = weatherDataService.getCurrentWeather();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAverageDailyTemperature() {
        Instant now = Instant.now();

        WeatherData weatherData1 = WeatherData.builder().location("Minsk").temperature(6).windMPH(9.0)
            .pressureMB(1017.0).humidity(66.0).weatherCondition("Sunny").createdAt( now.minus(Duration.ofDays(1)))
            .build();

        WeatherData weatherData2 = WeatherData.builder().location("Minsk").temperature(8).windMPH(6.0)
            .pressureMB(1009.0).humidity(69.0).weatherCondition("Cloudy").createdAt(now)
            .build();

        Mockito.when(weatherDataRepository.findByCreatedAtBetween(Mockito.any(Instant.class),
            Mockito.any(Instant.class))).thenReturn(Arrays.asList(weatherData1, weatherData2));

        AverageDailyTemperatureDto expected = new AverageDailyTemperatureDto(7);
        AverageDailyTemperatureDto actual = weatherDataService.getAverageDailyTemperature();

        assertEquals(expected, actual);
    }

}
