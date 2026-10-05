package com.mycompany.webstore.domain.model;

/** A city together with its great-circle distance (km) from a reference city (read model). */
public record CityDistance(City city, double distanceKm) {}
