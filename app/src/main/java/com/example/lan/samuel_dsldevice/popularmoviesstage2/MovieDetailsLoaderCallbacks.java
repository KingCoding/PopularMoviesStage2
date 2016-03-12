package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.support.v4.app.LoaderManager;

/**
 * Created by owner on 16/12/2015.
 */
public interface MovieDetailsLoaderCallbacks<T> extends LoaderManager.LoaderCallbacks<T> {

    public boolean isUpdate();

    public void executeBackgroundUpdate();
}
