package hrstal.workoutjournal.adapters;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import hrstal.workoutjournal.R;
import hrstal.workoutjournal.models.Timer;

public class TimerAdapter extends BaseAnimatedCursorAdapter<TimerAdapter.TimerViewHolder> {

    private static final String TAG = TimerAdapter.class.getSimpleName();

    long DURATION = 500;
    private boolean on_attach = true;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TimerAdapter() {
        super(null);
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View formTimerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false);
        return new TimerViewHolder(formTimerView);
    }

    @Override
    public void onBindViewHolder(TimerViewHolder holder, Cursor cursor) {
        Timer timer = new Timer(cursor);
        holder.timerText.setText(timer.toString());
    }

    @Override
    public void swapCursor(Cursor newCursor) {
        Log.d(TAG, "Swap cursor");
        super.swapCursor(newCursor);
    }


    class TimerViewHolder extends RecyclerView.ViewHolder {

        TextView timerText;

        TimerViewHolder(final View itemView) {
            super(itemView);
            timerText = itemView.findViewById(R.id.text_timer);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

    }

}
