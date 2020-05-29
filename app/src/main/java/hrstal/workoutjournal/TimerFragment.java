package hrstal.workoutjournal;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hrstal.workoutjournal.services.TimerService;
import hrstal.workoutjournal.adapters.TimerAdapter;
import hrstal.workoutjournal.data.DbContract;
import hrstal.workoutjournal.models.Timer;
import hrstal.workoutjournal.utils.Constants;
import hrstal.workoutjournal.utils.PreferencesHelper;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.BIND_AUTO_CREATE;


/**
 * TODO: keyboard position
 * TODO: RecyclerView animation
 * TODO: FAB animation
 * TODO: Favourite / recent timers
 */
public class TimerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TimerService.CallBack, ServiceConnection {

    private static final String TAG = TimerFragment.class.getSimpleName();
    private static final int TIMERS_LOADER_ID = 1;
//    private Button startButton;
    private TimerAdapter timerAdapter;
    private TextView remainingTimeText;
    private TextView counterText;
    private ProgressBar progressBar;
    View rootView;

    private TimerService timerService;
    private long secondsUntilFinished = 0;

    private long lastPressTime;

    public TimerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        timerAdapter = new TimerAdapter();
        timerAdapter.setOnItemClickListener(new TimerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Cursor cursor = timerAdapter.getItem(position);
//                long id = cursor.getLong(cursor.getColumnIndex(DbContract.Timers._ID));
                populateInput(cursor);
            }
        });

        recyclerView.setAdapter(timerAdapter);

        LoaderManager.getInstance(this).initLoader(TIMERS_LOADER_ID, null, this);

        remainingTimeText = rootView.findViewById(R.id.text_remaining_time);

        counterText = rootView.findViewById(R.id.textView_counter);
        progressBar = rootView.findViewById(R.id.progress_bar);

        final Button startButton = rootView.findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timer timer = makeTimerFromInput();

                if(timer == null){
                    Toast.makeText(getContext(), R.string.cant_create_timer,Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri uri = getActivity().getContentResolver().insert(DbContract.Timers.CONTENT_URI, timer.toContentValues());

                long id = Long.valueOf(DbContract.Timers.getTimerId(uri));

                PreferencesHelper.INSTANCE.setTimerId(id);

                progressBar.setMax(timer.getSeconds()*1000);
                progressBar.setProgress(0);

                Intent intent = new Intent(getContext(), TimerService.class);
                intent.putExtra("timer_parcelable", timer);
                ContextCompat.startForegroundService(getContext(),intent);

                startButton.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startButton.setEnabled(true);
                    }
                }, Constants.PRESS_RESTRICTION_INTERVAL);
            }
        });

        Button resetButton = rootView.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long pressTime = System.currentTimeMillis();
                if(pressTime - lastPressTime < Constants.DOUBLE_PRESS_INTERVAL){
                    Log.d(TAG, "Reset button pressed");
                    timerService.resetTimer();
                    remainingTimeText.setText("--");
                    ((TextView)getView().findViewById(R.id.textView_counter)).setText("--");
                    progressBar.setProgress(0);
                } else {
                    Toast.makeText(getContext(), R.string.press_again_to_reset, Toast.LENGTH_SHORT).show();
                }
                lastPressTime = pressTime;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intentData = new Intent(getContext(), TimerService.class);
        getContext().bindService(intentData, this, BIND_AUTO_CREATE);

        Log.d(TAG, "timerId: " + PreferencesHelper.INSTANCE.getTimerId() + " repeats: " + PreferencesHelper.INSTANCE.getRepeats() + " service: " + PreferencesHelper.INSTANCE.getServiceRunning());

        if (PreferencesHelper.INSTANCE.getTimerId() != -1){
            Cursor cursor = getContext().getContentResolver().query(
                    DbContract.Timers.CONTENT_URI,
                    DbContract.Timers.PROJECTION_ALL,
                    DbContract.Timers.SELECTION_SEARCH_ID,
                    new String[]{String.valueOf(PreferencesHelper.INSTANCE.getTimerId())},
                    null);

            if ((cursor != null) && (cursor.getCount() > 0)) {
                cursor.moveToFirst();
                populateInput(cursor);
            }
        }

        int repeats = PreferencesHelper.INSTANCE.getRepeats();
        /* timer is running */
        if (PreferencesHelper.INSTANCE.getServiceRunning()) {
            counterText.setText(String.valueOf(repeats + 1));
        } else {
            /* timer has finished */
            if (repeats > 0) {
                counterText.setText(String.valueOf(repeats));
                remainingTimeText.setText(R.string.done);
                progressBar.setProgress(progressBar.getMax());
            /* reset */
            } else {
                counterText.setText("--");
                remainingTimeText.setText("--");
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        remainingTimeText.setText("--");
        if (timerService != null) {
            timerService.setCallBack(null);
            getContext().unbindService(this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader()");
        if (id == TIMERS_LOADER_ID) {
            Log.d(TAG, "return timersLoader");
            return timersLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        timerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        timerAdapter.swapCursor(null);
    }

    private Loader<Cursor> timersLoader() {
        return new CursorLoader(
                getActivity().getApplicationContext(),
                DbContract.Timers.CONTENT_URI,
                DbContract.Timers.PROJECTION_ALL,
                null,
                null,
                DbContract.Timers.SORT_DATE);
    }

    private Timer makeTimerFromInput(){
        int seconds = parseInt((EditText)getView().findViewById(R.id.edit_timer));
        if (seconds > 0){
            CheckBox repeatCheckBox = getView().findViewById(R.id.check_repeat);
            CheckBox vibrateCheckBox = getView().findViewById(R.id.check_vibrate);
            CheckBox soundCheckBox = getView().findViewById(R.id.check_sound);
            int reverseSeconds = parseInt((EditText)getView().findViewById(R.id.edit_reverse_timer));
            int repeats = repeatCheckBox.isChecked() ? parseInt((EditText)getView().findViewById(R.id.edit_repeats)) : 0;
            return new Timer(
                    seconds,
                    reverseSeconds,
                    repeats,
                    vibrateCheckBox.isChecked(),
                    soundCheckBox.isChecked());
        }
        return null;
    };

    private int parseInt(EditText editText){
        return editText.getText().toString().length() > 0 ? Integer.parseInt(editText.getText().toString()) : 0;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void populateInput(Cursor cursor){
        int seconds = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_TIMER_SECONDS));
        int reverseSeconds = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_REVERSE_TIMER_SECONDS));
        int repeats = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_REPEAT_COUNT));
        boolean vibrate = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_VIBRATE)) == 1;
        boolean sound = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_SOUND)) == 1;

        ((EditText)getView().findViewById(R.id.edit_timer)).setText(String.valueOf(seconds));
        ((EditText)getView().findViewById(R.id.edit_reverse_timer)).setText(reverseSeconds > 0 ? String.valueOf(reverseSeconds) : "");
        ((CheckBox) getView().findViewById(R.id.check_repeat)).setChecked(repeats > 0);
        ((EditText) getView().findViewById(R.id.edit_repeats)).setText(repeats > 0 ? String.valueOf(repeats) : "");
        ((CheckBox) getView().findViewById(R.id.check_vibrate)).setChecked(vibrate);
        ((CheckBox) getView().findViewById(R.id.check_sound)).setChecked(sound);
    }

    @Override
    public void onProgressUpdate(long progress, long max) {
        if (progressBar.getMax() != max)
            progressBar.setMax((int)max);
        progressBar.setProgress((int)progress);
    }

    @Override
    public void onTimeUpdate(long secondsUntilFinished) {
        remainingTimeText.setText(String.valueOf(secondsUntilFinished));
    }

    @Override
    public void onFinish(int repeatsCompleted, int repeatsTotal) {
//        ((TextView)getView().findViewById(R.id.textView_counter)).setText(String.valueOf(repeatsCompleted));
        if (repeatsCompleted >= repeatsTotal){
            remainingTimeText.setText(R.string.done);
        }
    }

    @Override
    public void onStartTimer(int repeatsCompleted, int repeatsTotal) {
        ((TextView)getView().findViewById(R.id.textView_counter)).setText(String.valueOf(repeatsCompleted + 1));
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected");
        timerService = ((TimerService.LocalBinder)iBinder).getServiceInstance();
        timerService.setCallBack(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}
