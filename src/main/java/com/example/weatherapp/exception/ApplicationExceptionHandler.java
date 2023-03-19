package com.example.weatherapp.exception;


import com.example.weatherapp.dto.response.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({WeatherDataNotFoundException.class, InvalidDateException.class})
    public ResponseEntity<ExceptionDto> handleWeatherDataNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(
            new ExceptionDto(e.getMessage(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> parameterExceptionHandler(
        MethodArgumentNotValidException e) {

        BindingResult result = e.getBindingResult();
        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                return new ResponseEntity<>(
                    new ExceptionDto(error.getDefaultMessage(), HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase()),
                    HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>(
            new ExceptionDto("Argument validation failed", HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeatherAPIException.class)
    public void handleWeatherAPIException(WeatherAPIException e){
        log.error(e.getMessage());
    }

}
