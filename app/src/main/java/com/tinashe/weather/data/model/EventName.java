package com.tinashe.weather.data.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({EventName.GET_FORECAST, EventName.GET_FORECAST_DETAIL})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventName {
    String GET_FORECAST = "GET_FORECAST";
    String GET_FORECAST_DETAIL = "GET_FORECAST_DETAIL";
}
