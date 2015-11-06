package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by owner on 12/09/2015.
 */
public class MovieUtility {

    //URL variables initialized with default values
    private static String BASE_URL = "http://api.themoviedb.org/3/";
    private static String DISCOVER_MOVIE_ENDPOINT = "discover/movie?";
    private static String API_KEY = "fe9c0dca2dd71992c365ad658c83d421";
    private static String API_KEY_NAME = "api_key";
    private static String SORT_KEY_NAME = "sort_by";
    private static String SORT_POPULARITY_DESCENDING = "popularity.desc";
    private static String SORT_RATING_DESCENDING = "vote_average.desc";

    private static String MAIN_DISCOVER_MOVIE_LIST_NAME = "results";
    private static String MOVIE_POSTER_PATH_ATTRIBUTE = "poster_path";
    private static String POSTER_PATH_BASE_URL = "http://image.tmdb.org/t/p/";
    private static String MOVIE_ID_ATTRIBUTE = "id";
    private static String MOVIE_ENDPOINT = "movie/";

    private static String MOVIE_TITLE_ATTRIBUTE = "title";
    private static String MOVIE_RELEASE_DATE_ATTRIBUTE = "release_date";
    private static String MOVIE_DURATION_ATTRIBUTE = "runtime";
    private static String MOVIE_RATING_ATTRIBUTE = "vote_average";
    private static String MOVIE_SYPNOSIS_ATTRIBUTE = "overview";


    //private static String[] imageSizes = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};

    public static String getMOVIE_TITLE_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_TITLE_ATTRIBUTE);
    }

    public static String getMOVIE_RELEASE_DATE_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_RELEASE_DATE_ATTRIBUTE);
    }

    public static String getMOVIE_DURATION_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_DURATION_ATTRIBUTE);
    }

    public static String getMOVIE_RATING_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_RATING_ATTRIBUTE);
    }

    public static String getMOVIE_SYPNOSIS_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_SYPNOSIS_ATTRIBUTE);
    }

    public static String getBASE_URL(Context context){

        return context.getString(R.string.BASE_URL);
    }

    public static String getMOVIE_ENDPOINT(Context context){

        return context.getString(R.string.MOVIE_ENDPOINT);
    }

    public static String getPOSTER_PATH_BASE_URL(Context context){

        return context.getString(R.string.POSTER_PATH_BASE_URL);
    }

    public static String getMOVIE_POSTER_PATH_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_POSTER_PATH_ATTRIBUTE);
    }

    public static String getAPI_KEY(Context context){

        return context.getString(R.string.API_KEY);
    }

    public static String getAPI_KEY_NAME(Context context){

        return context.getString(R.string.API_KEY_NAME);
    }

    public static String getSORT_KEY_NAME(Context context){

        return context.getString(R.string.SORT_KEY_NAME);
    }


    public static String getSORT_POPULARITY_DESCENDING(Context context){

        return context.getString(R.string.SORT_POPULARITY_DESCENDING);
    }

    public static String getDISCOVER_MOVIE_ENDPOINT(Context context){

        return  context.getString(R.string.DISCOVER_MOVIE_ENDPOINT);
    }

    public static String getMAIN_DISCOVER_MOVIE_LIST_NAME(Context context){

        return context.getString(R.string.MAIN_DISCOVER_MOVIE_LIST_NAME);
    }

    public static String getMOVIE_ID_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_ID_ATTRIBUTE);
    }

    public static String getSORT_RATING_DESCENDING(Context context){

        return context.getString(R.string.SORT_RATING_DESCENDING);
    }


    public static String getImageSizeKey(int sizeRange, Context context){

        Resources res = context.getResources();
        String[] imageSizes = res.getStringArray(R.array.images_sizes);
        if(sizeRange < imageSizes.length && sizeRange >=0)
            return imageSizes[sizeRange];
        else
            return null;
    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static JSONArray extractArray(String jsonString, String listName)
    throws JSONException{

        JSONObject moviesJSONObject =  new JSONObject(jsonString);

        return moviesJSONObject.getJSONArray(listName);
    }

    public static LinkedList<String> extractAttributeList(JSONArray jsonArray, String attKey)
    throws JSONException{
        LinkedList<String> attList = new LinkedList<String>();

        for(int i=0; i<jsonArray.length(); i++)
            attList.add((jsonArray.getJSONObject(i)).getString(attKey));

        return attList;
    }

    public static JSONObject getJSONObject(String jsonObjectStr)
            throws JSONException{

        return new JSONObject(jsonObjectStr);
    }

    public static String extractAttribute(JSONObject jsonObject, String attKey)
            throws JSONException{

        return jsonObject.getString(attKey);
    }

    public static String getStringSharedPreference(Context context, String key, String defaultValue){

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key,defaultValue);
    }

    public static int getIntSharedPreference(Context context, String key, int defaultValue){

        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key,defaultValue);
    }


    public static  void putIntSharedPreference(Context context, String key, int value){

        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt(key, value);
        editor.commit(); //faster than commit
    }
}
