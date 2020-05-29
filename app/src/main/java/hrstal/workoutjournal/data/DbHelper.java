package hrstal.workoutjournal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static hrstal.workoutjournal.data.DbContract.Timers;
import static hrstal.workoutjournal.data.DbContract.BodyWeight;
import static hrstal.workoutjournal.data.DbContract.Exercise;
import static hrstal.workoutjournal.data.DbContract.Workout;
import static hrstal.workoutjournal.data.DbContract.Routine;
import static hrstal.workoutjournal.data.DbContract.RoutineExercise;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WorkoutJournalDatabase.db";
    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_TIMER_TABLE =
            "CREATE TABLE " + Timers.TABLE_NAME + " (" +
                    Timers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Timers.COLUMN_TIMER_SECONDS + " INTEGER not null," +
                    Timers.COLUMN_REVERSE_TIMER_SECONDS + " INTEGER not null," +
                    Timers.COLUMN_REPEAT_COUNT + " INTEGER not null," +
                    Timers.COLUMN_VIBRATE + " INTEGER not null," +
                    Timers.COLUMN_SOUND + " INTEGER not null," +
                    Timers.COLUMN_USAGE_COUNT + " INTEGER DEFAULT 1," +
                    Timers.COLUMN_LAST_USAGE_DATE + " INTEGER not null)";

    private static final String CREATE_BODY_WEIGHT_TABLE =
            "CREATE TABLE " + BodyWeight.TABLE_NAME + " (" +
                    BodyWeight._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BodyWeight.COLUMN_DATE + " INTEGER not null," +
                    BodyWeight.COLUMN_WEIGHT + " REAL not null)";

    private static final String CREATE_EXERCISE_TABLE =
            "CREATE TABLE " + Exercise.TABLE_NAME + " (" +
                    Exercise._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Exercise.COLUMN_NAME + " TEXT not null)";

    private static final String CREATE_WORKOUT_TABLE =
            "CREATE TABLE " + Workout.TABLE_NAME + " (" +
                    Workout._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Workout.COLUMN_DATE + " INTEGER not null," +
                    Workout.COLUMN_EXERCISE_ID + " INTEGER not null," +
                    Workout.COLUMN_WEIGHT + " REAL not null," +
                    Workout.COLUMN_DIFFICULTY + " INTEGER not null)";

    private static final String CREATE_ROUTINE_TABLE =
            "CREATE TABLE " + Routine.TABLE_NAME + " (" +
                    Routine._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Routine.COLUMN_NAME + " TEXT not null)";

    private static final String CREATE_ROUTINE_EXERCISE_TABLE =
            "CREATE TABLE " + RoutineExercise.TABLE_NAME + " (" +
                    RoutineExercise._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RoutineExercise.COLUMN_ROUTINE_ID + " INTEGER not null," +
                    RoutineExercise.COLUMN_EXERCISE_ID + " INTEGER not null," +
                    RoutineExercise.COLUMN_SET_ID + " INTEGER not null," +
                    RoutineExercise.COLUMN_REPETITIONS + " INTEGER not null," +
                    RoutineExercise.COLUMN_WEIGHT + " REAL not null)";


    private static final String DELETE_TIMER_TABLE =
            "DROP TABLE IF EXISTS " + Timers.TABLE_NAME;

    private static final String DELETE_BODY_WEIGHT_TABLE =
            "DROP TABLE IF EXISTS " + BodyWeight.TABLE_NAME;

    private static final String DELETE_EXERCISE_TABLE =
            "DROP TABLE IF EXISTS " + Exercise.TABLE_NAME;

    private static final String DELETE_WORKOUT_TABLE =
            "DROP TABLE IF EXISTS " + Workout.TABLE_NAME;

    private static final String DELETE_ROUTINE_TABLE =
            "DROP TABLE IF EXISTS " + Routine.TABLE_NAME;

    private static final String DELETE_ROUTINE_EXERCISE_TABLE =
            "DROP TABLE IF EXISTS " + RoutineExercise.TABLE_NAME;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TIMER_TABLE);
        db.execSQL(CREATE_BODY_WEIGHT_TABLE);
        db.execSQL(CREATE_EXERCISE_TABLE);
        db.execSQL(CREATE_WORKOUT_TABLE);
        db.execSQL(CREATE_ROUTINE_TABLE);
        db.execSQL(CREATE_ROUTINE_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DELETE_BODY_WEIGHT_TABLE);
            db.execSQL(CREATE_BODY_WEIGHT_TABLE);
            db.execSQL(CREATE_EXERCISE_TABLE);
            db.execSQL(CREATE_WORKOUT_TABLE);
            db.execSQL(CREATE_ROUTINE_TABLE);
            db.execSQL(CREATE_ROUTINE_EXERCISE_TABLE);
        }
        if (oldVersion < 3) {
//            db.execSQL("ALTER TABLE " + RoutineExercise.TABLE_NAME
//                    + " ADD COLUMN " + RoutineExercise.COLUMN_SET_ID + " INTEGER not null");
//            db.execSQL("ALTER TABLE " + RoutineExercise.TABLE_NAME
//                    + " ADD COLUMN " + RoutineExercise.COLUMN_REPETITIONS + " INTEGER not null");
//            db.execSQL("ALTER TABLE " + RoutineExercise.TABLE_NAME
//                    + " ADD COLUMN " + RoutineExercise.COLUMN_WEIGHT + " REAL not null");
            db.execSQL(DELETE_ROUTINE_EXERCISE_TABLE);
            db.execSQL(CREATE_ROUTINE_EXERCISE_TABLE);
        }
    }

}
