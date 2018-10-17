package com.tinashe.weather.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef({TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemperatureUnit {

    String CELSIUS = "celsius";

    String FAHRENHEIT = "fahrenheit";
}
