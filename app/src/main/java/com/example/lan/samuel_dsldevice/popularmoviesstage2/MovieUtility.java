package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

    public static String getSORT_FAVORITE(Context context){

        return context.getString(R.string.favorite);
    }

    public static String getMAIN_VIDEO_MOVIE_LIST_NAME(Context context){

        return context.getString(R.string.MAIN_VIDEO_MOVIE_LIST_NAME);
    }

    public static String getMAIN_REVIEW_MOVIE_LIST_NAME(Context context){

        return context.getString(R.string.MAIN_REVIEW_MOVIE_LIST_NAME);
    }

    public static String getMOVIE_REVIEW_CONTENT_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_REVIEW_CONTENT_ATTRIBUTE);
    }

    public static String getMOVIE_REVIEW_AUTHOR_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_REVIEW_AUTHOR_ATTRIBUTE);
    }

    public static String getMOVIE_TRAILER_KEY_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_TRAILER_KEY_ATTRIBUTE);
    }

    public static String getMOVIE_TRAILER_NAME_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_TRAILER_NAME_ATTRIBUTE);
    }

    public static String getMOVIE_TRAILER_SITE_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_TRAILER_SITE_ATTRIBUTE);
    }

    public static String getMOVIE_TRAILER_SIZE_ATTRIBUTE(Context context){

        return context.getString(R.string.MOVIE_TRAILER_SIZE_ATTRIBUTE);
    }

    public static String getREVIEW_TITLE_TEXT(Context context){

        return context.getString(R.string.review_title_text);
    }

    public static String getTRAILER_TITLE_TEXT(Context context){

        return context.getString(R.string.trailer_title_text);
    }

    public static String getEVEN_LIST_ITEM_COLOR_VALUE(Context context){

        return context.getString(R.string.even_list_item_color_value);
    }

    public static String getGRID_FAVORITE_ITEM_BACKGROUND_COLOR_VALUE(Context context){

        return context.getString(R.string.grid_favorite_item_background_color_value);
    }

    public static String getGRID_ITEM_BACKGROUND_COLOR_VALUE(Context context){

        return context.getString(R.string.grid_item_background_color_value);
    }

    public static String getGRID_SELECTED_ITEM_BACKGROUND_COLOR_VALUE(Context context){
        return context.getString(R.string.grid_selected_item_background_color_value);
    }

    public static String getODD_LIST_ITEM_COLOR_VALUE(Context context){

        return context.getString(R.string.odd_list_item_color_value);
    }

    public static String getMARK_FAVORITE_BUTTON_TEXT(Context context){

        return context.getString(R.string.mark_favorite_button_text);
    }

    public static String getREMOVE_FAVORITE_BUTTON_TEXT(Context context){

        return context.getString(R.string.remove_favorite_button_text);
    }

    public static String getMOVIE_FAVORITE_ICON_KEY(Context context){

        return context.getString(R.string.MOVIE_FAVORITE_ICON_KEY);
    }


    public static String getImageSizeKey(int sizeRange, Context context){

        Resources res = context.getResources();
        String[] imageSizes = res.getStringArray(R.array.images_sizes);
        if(sizeRange < imageSizes.length && sizeRange >=0)
            return imageSizes[sizeRange];
        else
            return null;
    }



    public static Drawable getSizedDrawable(int imageResource, int dpImageXSize, int dpImageYSize, Context context){

        Drawable img = context.getResources().getDrawable( imageResource );

        //int dpImageSize = 10;
        float pxImageXSize = convertDpToPixel(dpImageXSize, context);
        float pxImageYSize = convertDpToPixel(dpImageYSize, context);
        img.setBounds( 0, 0, (int)pxImageXSize, (int)pxImageYSize );
        return img;
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



    public interface MovieDetailInitiator{

        public static int REPLACE_GRID_WITH_DETAIL_OR_REPLACE_REVIEW_WITH_DETAIL = 1;

        public static int REPLACE_DETAIL_WITH_REVIEW = 2;

        public void sendIntent(int transactionCode, Bundle arguments);
    }

}
