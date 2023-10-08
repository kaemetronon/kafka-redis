package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GeoCountry {
    private final String name;
    @JsonProperty("long")
    private final Long lng;
    private final Long lat;
}
