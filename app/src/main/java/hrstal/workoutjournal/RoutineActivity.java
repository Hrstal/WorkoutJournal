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
import hrstal.workoutjournal.adapters.ExercisePlanAdapter;
import hrstal.workoutjournal.data.DbContract;

import android.database.Cursor;
import android.os.Bundle;

public class RoutineActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ExercisePlanAdapter routineAdapter;
    private static final int ROUTINE_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //set default animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        routineAdapter = new ExercisePlanAdapter();

        recyclerView.setAdapter(routineAdapter);
        LoaderManager.getInstance(this).initLoader(ROUTINE_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == ROUTINE_LOADER_ID) {
            return routineLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        routineAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        routineAdapter.swapCursor(null);
    }

    private Loader<Cursor> routineLoader() {
        return new CursorLoader(
                getApplicationContext(),
                DbContract.Routine.CONTENT_URI,
                DbContract.Routine.PROJECTION_ALL,
                null,
                null,
                DbContract.Routine.SORT_NAME);
    }

}
