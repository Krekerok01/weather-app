package com.example.weatherapp.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Value;


@Value
public class DateIntervalDto {

    @NotBlank(message = "Field cannot be empty")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Date format must be: dd-MM-yyyy")
    String from;

    @NotBlank(message = "Field cannot be empty")
    @Pattern(regexp = "\\d{2}-\\d{2}-\\d{4}", message = "Date format must be: dd-MM-yyyy")
    String to;
}
