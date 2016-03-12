package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPickerPreference extends DialogPreference {

    // allowed range
    public static int MAX_VALUE = 100;
    public static int MIN_VALUE = 0;
    // enable or disable the 'circular behavior'
    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private TextView dpSize;
    private Button incrementDpSize;
    private Button decrementDpSize;
    private MovieSettingsActivity movieSettingsActivity;
    private ListPreference listPreference;

    private final android.view.View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int currentValue = Integer.parseInt(dpSize.getText().toString());
            String valueToDisplay = null;
            if(v == incrementDpSize){
                if(currentValue + stepCrementer <= MAX_VALUE)
                {
                    valueToDisplay = ""+(currentValue + stepCrementer);
                    //dpSize.setText(""+(currentValue + stepCrementer));
                }else{
                    valueToDisplay = "" + (MIN_VALUE);
                    //dpSize.setText(""+MIN_VALUE);
                }
            }else if(v == decrementDpSize){

                if(currentValue - stepCrementer >= MIN_VALUE)
                {
                    valueToDisplay = "" + (currentValue - stepCrementer);
                    //dpSize.setText(""+(currentValue - stepCrementer));
                }else{
                    valueToDisplay = "" + (MAX_VALUE);
                    //dpSize.setText(""+MAX_VALUE);
                }
            }

            setValue(Integer.parseInt(valueToDisplay));
        }
    };

    private int stepCrementer;
    //private NumberPicker picker;
    private int value;


    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        int layoutDpSize = 150;
        int layoutPxSize = (int)MovieUtility.convertDpToPixel(layoutDpSize, getContext());
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                layoutPxSize, ViewGroup.LayoutParams.WRAP_CONTENT);

        //picker = new NumberPicker(getContext());
        //picker.setLayoutParams(layoutParams);

        LinearLayout dialogView = new LinearLayout(getContext());
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setLayoutParams(linearLayoutParams);

        incrementDpSize = new Button(getContext());
        incrementDpSize.setGravity(Gravity.CENTER);
        incrementDpSize.setText("+ 10dp");
        incrementDpSize.setOnClickListener(buttonListener);
        decrementDpSize = new Button(getContext());
        decrementDpSize.setText("- 10dp");
        decrementDpSize.setGravity(Gravity.CENTER);
        decrementDpSize.setOnClickListener(buttonListener);
        dpSize = new TextView(getContext());
        dpSize.setGravity(Gravity.CENTER);
        dpSize.setText(""+value);

        dialogView.addView(incrementDpSize,layoutParams);
        dialogView.addView(dpSize,layoutParams);
        dialogView.addView(decrementDpSize,layoutParams);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        //picker.setMinValue(MIN_VALUE);
        //picker.setMaxValue(MAX_VALUE);
        //picker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        //picker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //picker.clearFocus();
            int newValue = Integer.parseInt(dpSize.getText().toString());
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, MIN_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

        setValue(restorePersistedValue ?
                MovieUtility.getIntSharedPreference(getContext(),
                        ((Activity) getContext()).getResources().getString(R.string.min_img_dp_size_key), MIN_VALUE)
                :
                (Integer) defaultValue);
    }

    public void setValue(int value) {
        this.value = value;
        if(dpSize != null)
            dpSize.setText(""+value);
        persistInt(this.value);

        if(movieSettingsActivity != null)
            updateListPreference((int)(MovieUtility.convertDpToPixel(value,movieSettingsActivity)));
    }

    public int getValue() {
        return this.value;
    }

    public void setValues(int maxValue, int minValue, int stepCrementer){

        MAX_VALUE = maxValue;
        MIN_VALUE = minValue;
        this.stepCrementer = stepCrementer;

       /*
       //Adjust value to be in range
        if(value < MIN_VALUE)
           value = MIN_VALUE;

        if(value > MAX_VALUE)
           value = MAX_VALUE;
        */

        int defaultSize = MovieUtility.getIntSharedPreference(getContext(),
                ((Activity) getContext()).getResources().getString(R.string.min_img_dp_size_key), -1);

        if(defaultSize < 0)
               defaultSize =  movieSettingsActivity.getResources().getInteger(R.integer.default_img_dp_size);
        value = defaultSize;
        setValue(value);
    }

    public void setPreferenceActivity(MovieSettingsActivity movieSettingsActivity) {

        this.movieSettingsActivity = movieSettingsActivity;
    }

    public void setAssociateListPreference(ListPreference lp) {
        this.listPreference = lp;
    }


    private void updateListPreference(int minColWidthPixel){

        //int defaultValue = MovieUtility.getIntSharedPreference(movieSettingsActivity, movieSettingsActivity.getString(R.string.def_num_cols_key), 2); //We assume the grid can always display 2 columns independently of the orientation

        int width = MovieUtility.getIntSharedPreference(movieSettingsActivity, movieSettingsActivity.getString(R.string.gridView_width_key), 0);

        String currValStr = listPreference.getValue();

            int currentValue =currValStr==null ? 0 : Integer.parseInt(currValStr);
        //CharSequence[] values = listPreference.getEntryValues();
        int maxCols = width / minColWidthPixel;//MovieUtility.getIntSharedPreference(this, getString(R.string.max_num_cols_key), defaultValue);

        CharSequence[] entries = new CharSequence[maxCols];
        CharSequence[] entryValues = new CharSequence[maxCols];

        for(int i=0; i< maxCols; i++){
            entries[i] = ""+(i+1)+" columns";
            entryValues[i] = ""+(i+1);
        }

        ListPreference lp = (ListPreference)movieSettingsActivity.findPreference(movieSettingsActivity.getString(R.string.num_cols_key));
        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        //String defaultValue = MovieUtility.getSharedPreference(this, getString(R.string.def_num_cols_key), "2"); //We assume the grid could always display 2 columns independently of the orientation
        if(currentValue <= maxCols && currentValue != 0)
            lp.setDefaultValue(currentValue);

        lp.setTitle(movieSettingsActivity.getString(R.string.num_cols_title) + "\n" +movieSettingsActivity.getString(R.string.num_cols_title_warning));

        MovieSettingsActivity.bindPreferenceSummaryToValue(lp);
    }

}