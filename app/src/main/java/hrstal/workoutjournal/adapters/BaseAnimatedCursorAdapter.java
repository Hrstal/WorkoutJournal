package hrstal.workoutjournal.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAnimatedCursorAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    private static final String TAG = BaseAnimatedCursorAdapter.class.getSimpleName();
    private Cursor cursor;
    private boolean dataValid;
    private int rowIDColumn;
    public abstract void onBindViewHolder(V holder, Cursor cursor);

    public BaseAnimatedCursorAdapter(Cursor c) {
        setHasStableIds(true);
        swapCursor(c);
    }


    @Override
    public void onBindViewHolder(V holder, int position) {
        if (!dataValid) {
            throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to bind view holder");
        }

//        holder.setIsRecyclable(false);

        onBindViewHolder(holder, cursor);

        FromRightToLeft(holder.itemView, position);


    }

    @Override
    public int getItemCount() {
        if (dataValid) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (!dataValid) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id");
        }
        return cursor.getLong(rowIDColumn);
    }

    public Cursor getItem(int position) {
        if (!dataValid) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id");
        }
        return cursor;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return;
        }
        if (newCursor != null) {
            cursor = newCursor;
            dataValid = true;
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            cursor = null;
            rowIDColumn = -1;
            dataValid = false;
        }
    }

    int OFFSET = 300;
    long DURATION = 500;
    long DELAY = 75;
    private boolean on_attach = true;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                Log.d(TAG, "onScrollStateChanged: Called " + newState);
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setAnimation(View itemView, int i) {
        if(!on_attach){
            i = -1;
        }
        boolean isNotFirstItem = i == -1;
        i++;
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 0.f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animator.setStartDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION / 3));
        animator.setDuration(500);
        animatorSet.play(animator);
        animator.start();
    }

    private void FromRightToLeft(View itemView, int i) {
        if(!on_attach){
            i = -1;
        }
        boolean not_first_item = i == -1;
        i = i + 1;
        Log.d(TAG, String.valueOf(i));
        itemView.setTranslationX(itemView.getX() + OFFSET);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getX() + OFFSET, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 0.f, 0.5f, 1.0f);
        animatorTranslateY.setStartDelay(i * DELAY);
        animatorTranslateY.setDuration(DURATION);
        animatorAlpha.setStartDelay(i * DELAY + 200);
        animatorAlpha.setDuration(DURATION);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }
    
}
