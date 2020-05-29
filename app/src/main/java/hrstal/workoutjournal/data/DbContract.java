package hrstal.workoutjournal.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public final class DbContract {

    public static final String CONTENT_AUTHORITY = "hrstal.workoutjournal";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TIMERS = "timers";
    public static final String PATH_BODY_WEIGHT = "body_weight";
    public static final String PATH_EXERCISE = "exercises";
    public static final String PATH_WORKOUT = "workouts";
    public static final String PATH_ROUTINE = "routine";
    public static final String PATH_ROUTINE_EXERCISE = "routine_exercise";

    private DbContract() {
    }

    public static class Timers implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMERS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMERS;
        public static final String TABLE_NAME = "timers";
        public static final String COLUMN_TIMER_SECONDS = "timer_seconds";
        public static final String COLUMN_REVERSE_TIMER_SECONDS = "reverse_timer_seconds";
        public static final String COLUMN_REPEAT_COUNT = "repeat_count";
        public static final String COLUMN_VIBRATE = "vibrate";
        public static final String COLUMN_SOUND = "sound";
        public static final String COLUMN_USAGE_COUNT = "count";
        public static final String COLUMN_LAST_USAGE_DATE = "last_usage_date";

        public static final String[] PROJECTION_ALL = new String[]{_ID,
                COLUMN_TIMER_SECONDS, COLUMN_REVERSE_TIMER_SECONDS, COLUMN_REPEAT_COUNT,
                COLUMN_VIBRATE, COLUMN_SOUND, COLUMN_USAGE_COUNT, COLUMN_LAST_USAGE_DATE};

        public static final String SELECTION_SEARCH = Timers.COLUMN_TIMER_SECONDS + "=? AND "
                + Timers.COLUMN_REVERSE_TIMER_SECONDS + "=? AND "
                + Timers.COLUMN_REPEAT_COUNT + "=? AND "
                + Timers.COLUMN_VIBRATE + "=? AND "
                + Timers.COLUMN_SOUND + "=?";

        public static final String SELECTION_SEARCH_ID = _ID + "=? ";

        public static final String SORT_FAVOURITE = COLUMN_USAGE_COUNT + " DESC";
        public static final String SORT_DATE = COLUMN_LAST_USAGE_DATE + " DESC";

        public static Uri buildTimerUri(long timerId) {
            return ContentUris.withAppendedId(CONTENT_URI, timerId);
        }

        public static String getTimerId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class BodyWeight implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BODY_WEIGHT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BODY_WEIGHT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BODY_WEIGHT;
        public static final String TABLE_NAME = "body_weight";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String SORT_DATE = COLUMN_DATE + " DESC";

        public static Uri buildWeightUri(long weightId) {
            return ContentUris.withAppendedId(CONTENT_URI, weightId);
        }

        public static String getWeightId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class Exercise implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME = "name";
        public static final String[] PROJECTION_ALL = new String[]{_ID, COLUMN_NAME};
        public static final String SORT_NAME = COLUMN_NAME + " ASC";

        public static Uri buildExerciseUri(long exerciseId) {
            return ContentUris.withAppendedId(CONTENT_URI, exerciseId);
        }

        public static String getExerciseId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class Workout implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;
        public static final String TABLE_NAME = "workout";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_DIFFICULTY = "difficulty";

        public static Uri buildWorkoutUri(long workoutId) {
            return ContentUris.withAppendedId(CONTENT_URI, workoutId);
        }

        public static String getWorkoutId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class Routine implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTINE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTINE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTINE;
        public static final String TABLE_NAME = "routine";
        public static final String COLUMN_NAME = "name";
        public static final String[] PROJECTION_ALL = new String[]{_ID, COLUMN_NAME};
        public static final String SORT_NAME = COLUMN_NAME + " ASC";

        public static Uri buildRoutineUri(long routineId) {
            return ContentUris.withAppendedId(CONTENT_URI, routineId);
        }

        public static String getRoutineId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static class RoutineExercise implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTINE_EXERCISE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTINE_EXERCISE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ROUTINE_EXERCISE;
        public static final String TABLE_NAME = "routine_exercise";
        public static final String COLUMN_ROUTINE_ID = "routine_id";
        public static final String COLUMN_EXERCISE_ID = "exercise_id";
        public static final String COLUMN_SET_ID = "set_id";
        public static final String COLUMN_REPETITIONS = "repetitions";
        public static final String COLUMN_WEIGHT = "weight";

        public static Uri buildRoutineExerciseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getRoutineExerciseId(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
