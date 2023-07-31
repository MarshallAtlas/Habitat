package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.util.Log;
import java.io.Serializable;

public class Habit implements Serializable{
    private String color;
    private String habit_name;
    private int habit_freeze;
    private boolean habitDone;
    private int streak;
    private String user_id;
    private String lastUpdate;
    private static final String TAG = "HabitAdapter"; // Define TAG here

    // Default constructor (required for Firestore)
    public Habit() {
        // Default constructor is necessary for Firestore to map data to objects.
    }

    // Constructor with parameters
    public Habit(String color, String habit_name, int habit_freeze, boolean habitDone, int streak, String user_id, String lastUpdate) {
        this.color = color;
        this.habit_name = habit_name;
        this.habit_freeze = habit_freeze;
        this.habitDone = habitDone;
        this.streak = streak;
        this.user_id = user_id;
        this.lastUpdate = lastUpdate;
    }

    // Getters and Setters (required for Firestore)
    public String getColor() {
        return color;
    }


    public void setColor(String color) {
        this.color = color;
    }

    public String getHabitName() {
        Log.d(TAG, "getHabitName: " + habit_name);
        return habit_name;
    }

    public void setHabitName(String habitName) {
        this.habit_name = habitName;
    }

    public int getHabitFreeze() {
        return habit_freeze;
    }

    public void setHabitFreeze(int habitFreeze) {
        this.habit_freeze = habitFreeze;
    }

    public boolean isHabitDone() {
        return habitDone;
    }

    public void setHabitDone(boolean habitDone) {
        this.habitDone = habitDone;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }
    public String getLastUpdate(){return lastUpdate;}
    public void setLastUpdate(String lastupdat){this.lastUpdate = lastupdat;}
}

