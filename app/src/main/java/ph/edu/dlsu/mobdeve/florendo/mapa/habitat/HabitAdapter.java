package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private static final String TAG = "HabitAdapter"; // Define TAG here
    Context context;
    ArrayList<Habit> HabitList;
    // constructor
    public HabitAdapter(Context context, ArrayList<Habit> habitList) {
        this.context = context;
        this.HabitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View CView = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false);

        return new HabitViewHolder(CView);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit Chabit = HabitList.get(position);
        holder.habit_name.setText(Chabit.getHabitName());
        Log.d(TAG, "onBindViewHolder: " + Chabit.getHabitName());

    }

    @Override
    public int getItemCount() {

        return HabitList.size();
    }


    public static class HabitViewHolder extends RecyclerView.ViewHolder{

        TextView habit_name;

        public HabitViewHolder(@NonNull View habitView) {
            super(habitView);

            habit_name = habitView.findViewById(R.id.tv_habit_name);

        }
    }
}
