package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class PM1_MainActivity extends ActionBarActivity implements MoviesGridFragment.MovieDetailInitiator{

    private String DETAIL_EXTRA = "MOVIE_ID";
    private Fragment moviesGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pm1__main);
        moviesGridFragment = getSupportFragmentManager().findFragmentByTag("movieFragmentTag");

        if (savedInstanceState == null) {
            if(moviesGridFragment == null)
            moviesGridFragment = new MoviesGridFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, moviesGridFragment, "movieFragmentTag")
                    //.addToBackStack("movieFragmentTag")
                    .commit();

        }
        else{

            /*
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, moviesGridFragment, "movieFragmentTag")
                    .addToBackStack("movieFragmentTag")
                    .commit();

            getSupportFragmentManager().beginTransaction().
                    remove(getSupportFragmentManager().findFragmentByTag("movieFragmentTag")).commit();
            getSupportFragmentManager().popBackStack();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, moviesGridFragment, "movieFragmentTag")
                    .commit();
            */
            //ViewGroup layout = (ViewGroup) findViewById(R.id.container);

            //layout.requestLayout();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pm1__main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, MovieSettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void sendIntent(String... intentData){

        Intent intent = new Intent(this, MovieDetailActivity.class).putExtra(Intent.EXTRA_TEXT, intentData[0]);
        startActivity(intent);
    }

}
