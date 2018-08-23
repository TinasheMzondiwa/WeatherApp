package com.tinashe.weather.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT})
@Retention(RetentionPolicy.RUNTIME)
public @interface TemperatureUnit {

    String CELSIUS = "celsius";

    String FAHRENHEIT = "fahrenheit";
}
