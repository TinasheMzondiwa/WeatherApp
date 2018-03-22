package com.tinashe.weather.model;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({
        ViewState.LOADING,
        ViewState.SUCCESS,
        ViewState.ERROR
})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewState {
    int LOADING = 1;

    int SUCCESS = 2;

    int ERROR = 3; //No connection, default errors
}
