package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by owner on 10/09/2015.
 *
 * Class that will be used to execute all the tasks of the application
 */
public class MovieTask extends AsyncTask<URL, Void, HashMap<String, Object>>{

    private MovieTaskCallBackInstance taskCallBackInstance;

    private String movieJsonStr;

    public MovieTask(MovieTaskCallBackInstance taskCallBackInstance){
        super();
        this.taskCallBackInstance = taskCallBackInstance;
    }

    @Override
    protected HashMap<String, Object> doInBackground(URL... urls){

        HashMap<String, Object> result = null;
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        //String movieJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = urls[0];//new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                movieJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                movieJsonStr = null;
            }
            movieJsonStr = buffer.toString();

            result = parse(movieJsonStr);
        }
        catch (JSONException jsexp){

        }
        catch (IOException e) {

            movieJsonStr = null;
        }
        catch(Exception exp){

        }
        finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }


        return result;


    }

    /*
    Helper method of the doInBackground method that will finalize things by doing the operation intense parsing work
    in the background task
     */
    private HashMap<String, Object> parse(String movieJsonStr) throws JSONException{

        //Parse the JSON string to extract useful information and set them on the taskCallBack instance

        String mainListName = taskCallBackInstance.getMainListName();

        if(mainListName != null) {
            JSONArray jsonArray = MovieUtility.extractArray(movieJsonStr, mainListName);
            LinkedList<String> attNames = taskCallBackInstance.getAttributesNames();
            HashMap<String, Object> attLists = new HashMap<String, Object>();

            for (String attName : attNames)
                attLists.put(attName, MovieUtility.extractAttributeList(jsonArray, attName));
            //taskCallBackInstance.setAttributesLists(attLists);
            return attLists;
        }
        else{ //We assume that the current json String is for a JSONObject and not for a JSONArray
              //We then get the attribute names and build the list of values
            LinkedList<String> attNames = taskCallBackInstance.getAttributesNames(); //These attributes names relates to a single JSON object
            HashMap<String, Object> attList = new HashMap<String, Object>();
            JSONObject jsonObject = MovieUtility.getJSONObject(movieJsonStr);
            for (String attName : attNames)
                attList.put(attName,MovieUtility.extractAttribute(jsonObject, attName));

            //taskCallBackInstance.setAttributesList(attList);
            return attList;
        }


    }

    @Override
    /*
    This method is used so that the final operations are performed in the main thread
    */
    protected void onPostExecute(HashMap<String, Object> result) {

        taskCallBackInstance.setAttributes(result);
    }

    /*
    This method returns an URL with the following format:
    http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]
     */
    public static URL buildURL(String baseURL, String endPoint, HashMap<String, String> urlParameters ){

        final String MOVIE_BASE_URL = baseURL + endPoint;
        Uri.Builder uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon();

        if(urlParameters != null) {
            Set<String> urlParametersKeys = urlParameters.keySet();
            for (String key : urlParametersKeys) {

                uriBuilder = uriBuilder.appendQueryParameter(key, urlParameters.get(key));
            }
        }

        Uri builtUri = uriBuilder.build();
        URL url = null;

        try {

            url = new URL(builtUri.toString());
        }
        catch(MalformedURLException muex){

            Log.e("From "+ MovieTask.class.getSimpleName()+ ".buildURL: ", "Error creating URL", muex);
        }
        return url;
    }

    /*
    This is a non optimized interface used for communication between the background task and the main thread
     */
    public interface MovieTaskCallBackInstance{
        public String getMainListName();
        public LinkedList<String> getAttributesNames();
        public void setAttributes(HashMap<String,Object> attributes);
    }
}
