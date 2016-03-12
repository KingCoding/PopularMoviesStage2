package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by owner on 16/12/2015.
 */
public class CursorLoaderUpdater extends CursorLoader {

    final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;

    Cursor mCursor;

    MovieDetailsLoaderCallbacks<Cursor> movieDetailsLoaderCallbacks;

    private boolean updatingCursor;

    public CursorLoaderUpdater(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    public CursorLoaderUpdater(Context context, Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }

    /* Runs on a worker thread */
    @Override
    public Cursor loadInBackground() {

        Cursor cursor = null;
        if(updatingCursor){

            movieDetailsLoaderCallbacks.executeBackgroundUpdate();
        }
        else {
            cursor = getContext().getContentResolver().query(mUri, mProjection, mSelection,
                    mSelectionArgs, mSortOrder);
        }

        if (cursor != null) {
            // Ensure the cursor window is filled
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }
        return cursor;
    }

    public void setMovieDetailsLoaderCallbacks(MovieDetailsLoaderCallbacks<Cursor> movieCallbacks){

        movieDetailsLoaderCallbacks = movieCallbacks;
    }

    public void setUpdatingCursor(boolean updatingCursor){

        this.updatingCursor = updatingCursor;
    }

}
