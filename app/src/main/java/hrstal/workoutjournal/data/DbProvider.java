package hrstal.workoutjournal.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DbProvider extends ContentProvider {

    private static final String TAG = DbProvider.class.getSimpleName();

    private DbHelper dbHelper;

    private static final int TIMERS = 100;
    private static final int TIMERS_ID = 101;
    private static final int BODY_WEIGHT = 300;
    private static final int BODY_WEIGHT_ID = 301;
    private static final int EXERCISE = 400;
    private static final int EXERCISE_ID = 401;
    private static final int WORKOUT = 500;
    private static final int WORKOUT_ID = 501;
    private static final int ROUTINE = 600;
    private static final int ROUTINE_ID = 601;
    private static final int ROUTINE_EXERCISE = 700;
    private static final int ROUTINE_EXERCISE_ID = 701;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_TIMERS, TIMERS);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_TIMERS + "/#", TIMERS_ID);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_BODY_WEIGHT, BODY_WEIGHT);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_BODY_WEIGHT + "/#", BODY_WEIGHT_ID);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_EXERCISE, EXERCISE);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_EXERCISE + "/#", EXERCISE_ID);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_WORKOUT, WORKOUT);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_WORKOUT + "/#", WORKOUT_ID);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ROUTINE, ROUTINE);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ROUTINE + "/#", ROUTINE_ID);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ROUTINE_EXERCISE, ROUTINE_EXERCISE);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_ROUTINE_EXERCISE + "/#", ROUTINE_EXERCISE_ID);
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        dbHelper = new DbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query uri:" + uri.toString() + ", projection:" + Arrays.toString(projection) + ", selection:" + selection + ", selectionArgs:" + Arrays.toString(selectionArgs) + ", sortOrder:" + sortOrder);
        String tableName;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TIMERS:
                tableName = DbContract.Timers.TABLE_NAME;
                break;
            case BODY_WEIGHT:
                tableName = DbContract.BodyWeight.TABLE_NAME;
                break;
            case EXERCISE:
                tableName = DbContract.Exercise.TABLE_NAME;
                break;
            case WORKOUT:
                tableName = DbContract.Workout.TABLE_NAME;
                break;
            case ROUTINE:
                tableName = DbContract.Routine.TABLE_NAME;
                break;
            case ROUTINE_EXERCISE:
                tableName = DbContract.RoutineExercise.TABLE_NAME;
                break;
            default: {
//                return null;
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

//            throw new IllegalArgumentException(
//                    "selectionArgs must be provided for the Uri: " + uri);

        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TIMERS:
                return DbContract.Timers.CONTENT_TYPE;
            case TIMERS_ID:
                return DbContract.Timers.CONTENT_ITEM_TYPE;
            case BODY_WEIGHT:
                return DbContract.BodyWeight.CONTENT_TYPE;
            case BODY_WEIGHT_ID:
                return DbContract.BodyWeight.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "insert uri:" + uri.toString());
        long id;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        switch (match) {
            case TIMERS:
                final String[] selectionArgs = {
                        contentValues.getAsString(DbContract.Timers.COLUMN_TIMER_SECONDS),
                        contentValues.getAsString(DbContract.Timers.COLUMN_REVERSE_TIMER_SECONDS),
                        contentValues.getAsString(DbContract.Timers.COLUMN_REPEAT_COUNT),
                        contentValues.getAsString(DbContract.Timers.COLUMN_VIBRATE),
                        contentValues.getAsString(DbContract.Timers.COLUMN_SOUND)};

                contentValues.put(DbContract.Timers.COLUMN_LAST_USAGE_DATE, System.currentTimeMillis());

                Cursor cursor = query(
                        DbContract.Timers.CONTENT_URI,
                        new String[]{DbContract.Timers._ID, DbContract.Timers.COLUMN_USAGE_COUNT},
                        DbContract.Timers.SELECTION_SEARCH,
                        selectionArgs,
                        null);

                if ((cursor != null) && (cursor.getCount() > 0)) {
                    cursor.moveToFirst();
                    id = cursor.getLong(cursor.getColumnIndex(DbContract.Timers._ID));
                    long count = cursor.getLong(cursor.getColumnIndex(DbContract.Timers.COLUMN_USAGE_COUNT));
                    contentValues.put(DbContract.Timers.COLUMN_USAGE_COUNT, count + 1);

                    update(uri, contentValues, DbContract.Timers._ID + "=?", new String[]{String.valueOf(id)});
                    return DbContract.Timers.buildTimerUri(id);
                } else {
                    id = db.insert(DbContract.Timers.TABLE_NAME, null, contentValues);
                }
                break;
            case BODY_WEIGHT:
                id = db.insert(DbContract.BodyWeight.TABLE_NAME, null, contentValues); //TODO: test if already exists
                break;
            case EXERCISE:
                id = db.insert(DbContract.Exercise.TABLE_NAME, null, contentValues); //TODO: test if already exists
                break;
            case WORKOUT:
                id = db.insert(DbContract.Workout.TABLE_NAME, null, contentValues); //TODO: test if already exists
                break;
            case ROUTINE:
                id = db.insert(DbContract.Routine.TABLE_NAME, null, contentValues);
                break;
            case ROUTINE_EXERCISE:
                id = db.insert(DbContract.RoutineExercise.TABLE_NAME, null, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (id > 0) {
            Uri result = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(result, null);
            return result;
        } else
            throw new android.database.SQLException("Error at inserting row in " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update uri:" + uri.toString());
        final int match = uriMatcher.match(uri);
        switch (match) {
            case TIMERS:
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                int i = db.update(DbContract.Timers.TABLE_NAME, contentValues, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                if (i > 0) {
                    return i;
                } else {
                    throw new android.database.SQLException("Error at updating row in " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
