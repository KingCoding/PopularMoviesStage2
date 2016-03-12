package com.example.lan.samuel_dsldevice.popularmoviesstage2.favoriteData;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by owner on 27/11/2015.
 */
public class FavoriteCursorAdapter extends CursorAdapter {


    public FavoriteCursorAdapter (Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
