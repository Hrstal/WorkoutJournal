package hrstal.workoutjournal;

import android.app.Application;
import android.content.ContentResolver;

public class WorkoutJournalApp extends Application {

    private static WorkoutJournalApp application;

    public WorkoutJournalApp() {
        application = this;
    }

    public static WorkoutJournalApp getApplication() {
        return application;
    }

    public static ContentResolver getResolver() {
        return application.getContentResolver();
    }

}
