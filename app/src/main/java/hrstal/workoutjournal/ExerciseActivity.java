package hrstal.workoutjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hrstal.workoutjournal.adapters.ExerciseAdapter;
import hrstal.workoutjournal.data.DbContract;

import android.database.Cursor;
import android.os.Bundle;

/**
 * TODO: edit record
 * TODO: remove record
 * TODO: empty input check
 * TODO: Viewer animation
 * TODO: EditText color and behavior
*/

public class ExerciseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ExerciseAdapter exerciseAdapter;
    private static final int EXERCISE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //set default animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        exerciseAdapter = new ExerciseAdapter();

        recyclerView.setAdapter(exerciseAdapter);
        LoaderManager.getInstance(this).initLoader(EXERCISE_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == EXERCISE_LOADER_ID) {
            return exerciseLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        exerciseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        exerciseAdapter.swapCursor(null);
    }

    private Loader<Cursor> exerciseLoader() {
        return new CursorLoader(
                getApplicationContext(),
                DbContract.Exercise.CONTENT_URI,
                DbContract.Exercise.PROJECTION_ALL,
                null,
                null,
                DbContract.Exercise.SORT_NAME);
    }

}
