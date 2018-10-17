package com.tinashe.weather.model;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

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
