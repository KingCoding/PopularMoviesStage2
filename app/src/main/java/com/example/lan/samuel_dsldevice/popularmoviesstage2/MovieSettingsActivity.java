package com.example.lan.samuel_dsldevice.popularmoviesstage2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class MovieSettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = true;


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        //Dynamically set the number of columns list

        int defaultValue = MovieUtility.getIntSharedPreference(this, getString(R.string.def_num_cols_key), 2); //We assume the grid can always display 2 columns independently of the orientation
        int maxCols = MovieUtility.getIntSharedPreference(this, getString(R.string.max_num_cols_key), defaultValue);

        CharSequence[] entries = new CharSequence[maxCols];
        CharSequence[] entryValues = new CharSequence[maxCols];

        for(int i=0; i< maxCols; i++){
            entries[i] = ""+(i+1)+" columns";
            entryValues[i] = ""+(i+1);
        }

        ListPreference lp = (ListPreference)findPreference(getString(R.string.num_cols_key));
        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        //String defaultValue = MovieUtility.getSharedPreference(this, getString(R.string.def_num_cols_key), "2"); //We assume the grid could always display 2 columns independently of the orientation
        lp.setDefaultValue(defaultValue);

        lp.setTitle(getString(R.string.num_cols_title) + "\n" +getString(R.string.num_cols_title_warning));

        //Read the width and complete the initialization of the number picker
        int minSizeDp = getResources().getInteger(R.integer.smallest_img_dp_size);
        int stepCrementer = getResources().getInteger(R.integer.img_dp_size_variation_step);
        int maxSizePx = MovieUtility.getIntSharedPreference(this,
                (this).getResources().getString(R.string.gridView_width_key), 0);
        int maxSizeDp = (int)MovieUtility.convertPixelsToDp(maxSizePx, this);

        NumberPickerPreference npp = (NumberPickerPreference)findPreference(getString(R.string.min_img_dp_size_key));
        npp.setTitle(R.string.min_img_dp_size_title);

        npp.setAssociateListPreference(lp);
        npp.setPreferenceActivity(this);
        npp.setValues(maxSizeDp, minSizeDp, stepCrementer);

        /*
        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_data_sync);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_data_sync);
        */
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.num_cols_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.min_img_dp_size_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.nav_key)));
        //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                if(preference.getKey().equals(preference.getContext().getString(R.string.pref_sort_key)))
                {
                    preference.setSummary(
                            index >= 0
                                    ? preference.getContext().getString(R.string.pref_sort_pre_summary_message)+listPreference.getEntries()[index]
                                    : null);
                }
                else if(preference.getKey().equals(preference.getContext().getString(R.string.num_cols_key))){

                    preference.setSummary(
                            index >= 0
                                    ? preference.getContext().getString(R.string.num_cols_pre_summary)+listPreference.getEntries()[index]
                                    : null);
                }


            } /*else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

        } */else if(preference.getKey().equals(preference.getContext().getString(R.string.nav_key))) {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary((Boolean)value? preference.getContext().getString(R.string.nav_enable_message)
                                                    : preference.getContext().getString(R.string.nav_disable_message));
            }
            else if(preference.getKey().equals(preference.getContext().getString(R.string.min_img_dp_size_key))){
                preference.setSummary(preference.getContext().getString(R.string.min_img_dp_size_summary) + " "+stringValue);
            }
            else{
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if(preference instanceof CheckBoxPreference)
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), true));
        else if(preference instanceof NumberPickerPreference)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 0));
        else
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
    }



}
