
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lan.samuel_dsldevice.popularmoviesstage2.favoriteData;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class FavoriteContract {

    /*
    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
    */


    /*
        Inner class that defines the table contents of the location table
        Students: This is where you will add the strings.  (Similar to what has been
        done for WeatherEntry)

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

    }
    */

    //A name for the entire content provider referred as the content authority
    public static final String CONTENT_AUTHORITY = "com.example.lan.samuel_dsldevice.popularmoviesstage2.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible paths. The only path refers to the table name
    public static final String PATH_FAVORITE = "favorite";


    /* Inner class that defines the table contents of the favoriteDetail table */
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

                        public static final String CONTENT_TYPE =
                                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
                public static final String CONTENT_ITEM_TYPE =
                                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_FIELD_SEPARATOR = new StringBuilder("column field separator").reverse().toString();

        // Column with the movie id of the detail movie.
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // Date, stored as String
        public static final String COLUMN_DATE = "movie_date";
        // Duration of the detail movie
        public static final String COLUMN_Duration = "movie_duration";

        // Rating of the detail movie
        public static final String COLUMN_RATING = "movie_rating";

        // Synopsis of the detail movie
        public static final String COLUMN_SYNOPSIS = "movie_synopsis";

        // Reviews of the details movie; saved in the same string with a separator
        public static final String COLUMN_REVIEWS = "movie_reviews";

        // Trailers for the detail movie; saved in the same string with a separator
        public static final String COLUMN_TRAILERS = "movie_trailers";

        // Path to the movie image
        public static final String COLUMN_IMAGE_PATH = "movie_image_path";

        //Title of the detail movie
        public static final String COLUMN_TITLE = "movie_title";

        public static Uri buildWeatherUri(long id){

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


}
