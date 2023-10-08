package com.example.demo.service.geo;

import com.example.demo.model.Author;
import com.example.demo.model.GeoPoint;

public interface IGeoService {
    String getCountryName(Author author, GeoPoint geo);
}
