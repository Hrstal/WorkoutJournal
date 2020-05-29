package hrstal.workoutjournal.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import hrstal.workoutjournal.R;
import hrstal.workoutjournal.models.Timer;
import hrstal.workoutjournal.utils.NotificationHelper;
import hrstal.workoutjournal.utils.PreferencesHelper;

import static hrstal.workoutjournal.utils.Constants.SINGLE_VIBE_DURATION;
import static hrstal.workoutjournal.utils.Constants.VIBRATION_PATTERN;

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();
    private static final int MAX_STREAMS = 2;

    private int repeatsTotal;
    private int repeatsCompleted;
    private boolean isReverse;
    private boolean shouldVibrate;
    private boolean shouldPlaySound;
    private boolean isRunning = false;

    private SoundPool soundPool;
    private int finishedSoundId;
    private Vibrator vibrator;

    private Timer timer;
    private CustomTimer countDownTimer;

    private CallBack callBack;
    private final IBinder binder = new LocalBinder();

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onProgressUpdate(long progress, long max);

        void onTimeUpdate(long secondsUntilFinished);

        void onFinish(int repeatsCompleted, int repeatsTotal);

        void onStartTimer(int repeatsCompleted, int repeatsTotal);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void TimeService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public TimerService getServiceInstance() {
            return TimerService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand() called!");

        timer = intent.getExtras().getParcelable("timer_parcelable");
        repeatsTotal = timer.getRepeatCount();
        shouldVibrate = timer.isVibrationEnabled();
        shouldPlaySound = timer.isSoundEnabled();
        int seconds = timer.getSeconds();

        repeatsCompleted = PreferencesHelper.INSTANCE.getRepeats();

        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setAudioAttributes(audioAttributes).setMaxStreams(MAX_STREAMS);
        this.soundPool = builder.build();
        finishedSoundId = soundPool.load(getApplicationContext(), R.raw.sound, 1);

        Notification notification = NotificationHelper
                .getInstance(this)
                .getNotification(getString(R.string.notification_title, seconds),getString(R.string.notification_text, 0));
        startForeground(NotificationHelper.NOTIFICATION_ID, notification);

        /* start while is already running */
        if(isRunning){
            if (countDownTimer != null)
                countDownTimer.cancel();
            repeatsCompleted++;
            PreferencesHelper.INSTANCE.setRepeats(repeatsCompleted);
        }

        isReverse = true;
        startTimer();
        PreferencesHelper.INSTANCE.setServiceRunning(true);
        isRunning = true;

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }


    public void startTimer() {
        isReverse = !isReverse;
        if (isReverse) {
            countDownTimer = new CustomTimer(timer.getReverseSeconds() * 1000, 50);
        } else {
            Log.d(TAG, "startTimer - repeatsCompleted: " + repeatsCompleted);
            NotificationHelper
                    .getInstance(getApplicationContext())
                    .setNotificationText(getString(R.string.notification_text, repeatsCompleted + 1));
            if (callBack != null) {
                callBack.onStartTimer(repeatsCompleted, repeatsTotal);
            }
            countDownTimer = new CustomTimer(timer.getSeconds() * 1000, 50);
        }
        countDownTimer.start();
    }


    public void resetTimer() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        isRunning = false;
        repeatsCompleted = 0;
        isReverse = false;
        PreferencesHelper.INSTANCE.setRepeats(repeatsCompleted);
        PreferencesHelper.INSTANCE.setTimerId(-1);
        PreferencesHelper.INSTANCE.setServiceRunning(isRunning);
        stopForeground(true);
        stopSelf();
    }


    public void countDownFinished() {
        if (!isReverse) {
            repeatsCompleted++;
            PreferencesHelper.INSTANCE.setRepeats(repeatsCompleted);
            if (callBack != null) {
                callBack.onFinish(repeatsCompleted, repeatsTotal);
            }
        }

        if (repeatsCompleted >= repeatsTotal) {
            NotificationHelper.getInstance(getApplicationContext()).setNotificationTitle(getString(R.string.done));
            NotificationHelper.getInstance(getApplicationContext()).addStartButton();
            if (shouldVibrate)
                vibrator.vibrate(VIBRATION_PATTERN, -1);
            if (shouldPlaySound)
                soundPool.play(finishedSoundId, 1, 1, 1, 0, 1f); // TODO: different shouldPlaySound on completion

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(callBack == null ? Service.STOP_FOREGROUND_DETACH : Service.STOP_FOREGROUND_REMOVE);
            } else {
                stopForeground(callBack == null);
            }
            PreferencesHelper.INSTANCE.setServiceRunning(false);
            isRunning = false;
            stopSelf();
            return;
        }

        if (shouldVibrate)
            vibrator.vibrate(SINGLE_VIBE_DURATION);
        if (shouldPlaySound)
            soundPool.play(finishedSoundId, 1, 1, 1, 0, 1f);

        startTimer();
    }


    public class CustomTimer extends CountDownTimer {

        private long secondsUntilFinished = 0;
        private long millisInFuture;

        public CustomTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.millisInFuture = millisInFuture;
            Log.d(TAG, String.format("new CustomTimer created (%s milliseconds)", millisInFuture));
        }


        @Override
        public void onTick(long millisUntilFinished) {
            if (secondsUntilFinished != millisUntilFinished / 1000) {
                secondsUntilFinished = (millisUntilFinished - 1) / 1000;

                Log.d(TAG, String.format("CustomTimer: %s seconds remaining", secondsUntilFinished + 1));
                NotificationHelper
                        .getInstance(getApplicationContext())
                        .setNotificationTitle(getString(R.string.notification_title, secondsUntilFinished + 1));
            }

            if (callBack != null) {
                callBack.onProgressUpdate(isReverse ? millisUntilFinished : millisInFuture - millisUntilFinished, millisInFuture);
                callBack.onTimeUpdate((millisUntilFinished - 1) / 1000 + 1);
            }
        }


        @Override
        public void onFinish() {
            if (callBack != null)
                callBack.onProgressUpdate(isReverse ? 0 : millisInFuture, millisInFuture);
            countDownFinished();
        }

    }


}
