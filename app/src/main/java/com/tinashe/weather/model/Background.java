package com.tinashe.weather.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({Background.CLEAR_DAY, Background.CLEAR_NIGHT, Background.CLEAR_DAY, Background.CLOUD_NIGHT,
        Background.CLOUD_DAY, Background.RAIN_DAY, Background.RAIN_NIGHT, Background.LIGHTNING_DAY, Background.LIGHTNING_NIGHT,
        Background.SNOW_DAY, Background.SNOW_NIGHT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Background {

    String CLEAR_DAY = "";
    String CLEAR_NIGHT = "";

    String CLOUD_DAY = "";
    String CLOUD_NIGHT = "";

    String RAIN_DAY = "";
    String RAIN_NIGHT = "";

    String LIGHTNING_DAY = "";
    String LIGHTNING_NIGHT = "";

    String SNOW_DAY = "";
    String SNOW_NIGHT = "";
}
