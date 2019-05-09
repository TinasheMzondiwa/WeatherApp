package com.tinashe.weather.data.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tinashe on 2018/03/20.
 */

@StringDef({DateFormat.DAY, DateFormat.TIME, DateFormat.TIME_SHORT})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormat {

    String DAY = "EEEE, dd MMM";
    String TIME = "hh:mm a";
    String TIME_SHORT = "HH:MM";

}
