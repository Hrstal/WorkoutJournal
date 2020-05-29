package hrstal.workoutjournal.utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import hrstal.workoutjournal.WorkoutJournalApp;

public enum PreferencesHelper {

    INSTANCE;

    public static final String REPEATS = "repeats";
    public static final String TIMER_ID = "timer_id";
    public static final String SERVICE_RUNNING = "service_running";

    private final SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(WorkoutJournalApp.getApplication().getApplicationContext());


    public void setRepeats(int repeats){
        preferences.edit().putInt(REPEATS, repeats).apply();
    }

    public int getRepeats(){
        return preferences.getInt(REPEATS, 0);
    }

    public void setTimerId(long timerId){
        preferences.edit().putLong(TIMER_ID, timerId).apply();
    }

    public long getTimerId(){
        return preferences.getLong(TIMER_ID, -1);
    }

    public void setServiceRunning(boolean isRunning){
        preferences.edit().putBoolean(SERVICE_RUNNING, isRunning).apply();
    }

    public boolean getServiceRunning(){
        return preferences.getBoolean(SERVICE_RUNNING, false);
    }

}
