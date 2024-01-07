package com.codestates.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WeatherInfo {
    @JsonProperty("weather")
    private List<Weather> weather; // weather field
    @JsonProperty("dt_txt")
    private String dateTimeText; // 2024-01-06 18:00:00
    @Setter
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;
    }
}

