package hrstal.workoutjournal.adapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import hrstal.workoutjournal.R;
import hrstal.workoutjournal.WorkoutJournalApp;
import hrstal.workoutjournal.data.DbContract;


/*Adapter with footer*/
public class ExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private static final String TAG = ExerciseAdapter.class.getSimpleName();

    private boolean mDataValid;
    private int mRowIDColumn;

    private Cursor cursor;

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_exercise_name);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        EditText nameEdit;
        Button addButton;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            nameEdit = itemView.findViewById(R.id.edit_exercise_name);
            addButton = itemView.findViewById(R.id.button_add_exercise);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_exercise, parent, false));
            default:
            case VIEW_TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder position: " + position + ", holder item type: " + holder.getItemViewType());
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
                cursor.moveToPosition(position);
                ItemViewHolder itemHolder = (ItemViewHolder) holder;
                itemHolder.name.setText(cursor.getString(cursor.getColumnIndex(DbContract.Exercise.COLUMN_NAME)));
                break;

            case VIEW_TYPE_FOOTER:
                final FooterViewHolder footerHolder = (FooterViewHolder) holder;
                footerHolder.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = footerHolder.nameEdit.getText().toString();
                        //TODO: search if exists, not empty, toast
                        ContentValues cv = new ContentValues();
                        cv.put(DbContract.Exercise.COLUMN_NAME, name);
                        WorkoutJournalApp.getApplication().getContentResolver().insert(DbContract.Exercise.CONTENT_URI, cv);
                        footerHolder.nameEdit.setText("");
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mDataValid) {
            return cursor.getCount() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType position: " + position);
        if (mDataValid)
            return (position == cursor.getCount()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
        else return VIEW_TYPE_FOOTER;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return;
        }
        if (newCursor != null) {
            cursor = newCursor;
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            cursor = null;
            mRowIDColumn = -1;
            mDataValid = false;
        }
    }
}
