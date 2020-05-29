package hrstal.workoutjournal.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import androidx.core.content.ContextCompat;
import hrstal.workoutjournal.services.TimerService;
import hrstal.workoutjournal.data.DbContract;
import hrstal.workoutjournal.models.Timer;
import hrstal.workoutjournal.utils.PreferencesHelper;

import static hrstal.workoutjournal.data.DbContract.Timers;

public class StartTimerReceiver extends BroadcastReceiver {

    private static final String TAG = StartTimerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        long id = PreferencesHelper.INSTANCE.getTimerId();

        Cursor cursor = context.getContentResolver().query(
                Timers.CONTENT_URI,
                Timers.PROJECTION_ALL,
                Timers.SELECTION_SEARCH_ID,
                new String[]{String.valueOf(id)},
                null);
        if ((cursor == null) || (cursor.getCount() <= 0)) {
            return;
        }

        cursor.moveToFirst();
        Timer timer = new Timer(cursor);
        context.getContentResolver().insert(DbContract.Timers.CONTENT_URI, timer.toContentValues());

        Intent serviceIntent = new Intent(context, TimerService.class);
        serviceIntent.putExtra("timer_parcelable", timer);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
