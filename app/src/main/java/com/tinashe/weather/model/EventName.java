package com.tinashe.weather.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

@StringDef({EventName.GET_FORECAST, EventName.GET_FORECAST_DETAIL})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventName {
    String GET_FORECAST = "GET_FORECAST";
    String GET_FORECAST_DETAIL = "GET_FORECAST_DETAIL";
}
