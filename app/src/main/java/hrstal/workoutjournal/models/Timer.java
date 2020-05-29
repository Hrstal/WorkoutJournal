package hrstal.workoutjournal.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import hrstal.workoutjournal.data.DbContract;

public class Timer implements Parcelable {

    private static final String TAG = Timer.class.getSimpleName();

    private long id;
    private int seconds;
    private int reverseSeconds;
    private int repeatCount;
    private boolean vibrationEnabled;
    private boolean soundEnabled;

    public Timer(int seconds, int reverseSeconds, int repeatCount, boolean vibrationEnabled, boolean soundEnabled) {
        this.id = -1;
        this.seconds = seconds;
        this.reverseSeconds = reverseSeconds;
        this.repeatCount = repeatCount;
        this.vibrationEnabled = vibrationEnabled;
        this.soundEnabled = soundEnabled;
        Log.d(TAG, "new timer created: " + toString());
    }

    public Timer(Cursor cursor) {
        this.id = cursor.getLong(cursor.getColumnIndex(DbContract.Timers._ID));
        this.seconds = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_TIMER_SECONDS));
        this.reverseSeconds = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_REVERSE_TIMER_SECONDS));
        this.repeatCount = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_REPEAT_COUNT));
        this.vibrationEnabled = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_VIBRATE)) > 0;
        this.soundEnabled = cursor.getInt(cursor.getColumnIndex(DbContract.Timers.COLUMN_SOUND)) > 0;
        Log.d(TAG, "new timer created: " + toString());
    }


    public long getId() {
        return id;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getReverseSeconds() {
        return reverseSeconds;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbContract.Timers.COLUMN_TIMER_SECONDS, seconds);
        cv.put(DbContract.Timers.COLUMN_REVERSE_TIMER_SECONDS, reverseSeconds);
        cv.put(DbContract.Timers.COLUMN_REPEAT_COUNT, repeatCount);
        cv.put(DbContract.Timers.COLUMN_VIBRATE, vibrationEnabled ? 1 : 0);
        cv.put(DbContract.Timers.COLUMN_SOUND, soundEnabled ? 1 : 0);
        return cv;
    }

    @NonNull
    @Override
    public String toString() {
        return seconds
                + (reverseSeconds > 0 ? "/" + reverseSeconds : "")
                + (repeatCount > 0 ? "x" + repeatCount : "")
                + (vibrationEnabled ? "+V" : "")
                + (soundEnabled ? "+S" : "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeInt(this.seconds);
        parcel.writeInt(this.reverseSeconds);
        parcel.writeInt(this.repeatCount);
        parcel.writeInt(this.vibrationEnabled ? 1 : 0);
        parcel.writeInt(this.soundEnabled ? 1 : 0);
    }

    protected Timer(Parcel in) {
        this.id = in.readLong();
        this.seconds = in.readInt();
        this.reverseSeconds = in.readInt();
        this.repeatCount = in.readInt();
        this.vibrationEnabled = in.readInt() != 0;
        this.soundEnabled = in.readInt() != 0;
    }

    public static final Parcelable.Creator<Timer> CREATOR = new Parcelable.Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel source) {
            return new Timer(source);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

}
