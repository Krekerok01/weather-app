package com.example.weatherapp.dto.response;

import lombok.Value;

@Value
public class ExceptionDto {

    String message;
    int statusCode;
    String statusMessage;
}
