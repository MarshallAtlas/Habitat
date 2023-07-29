package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

public class Habit {
    private String color;
    private String habitName;
    private int habitFreeze;
    private boolean habitDone;
    private int streak;
    private String userId;

    // Default constructor (required for Firestore)
    public Habit() {
        // Default constructor is necessary for Firestore to map data to objects.
    }

    // Constructor with parameters
    public Habit(String color, String habitName, int habitFreeze, boolean habitDone, int streak, String userId) {
        this.color = color;
        this.habitName = habitName;
        this.habitFreeze = habitFreeze;
        this.habitDone = habitDone;
        this.streak = streak;
        this.userId = userId;
    }

    // Getters and Setters (required for Firestore)
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public int getHabitFreeze() {
        return habitFreeze;
    }

    public void setHabitFreeze(int habitFreeze) {
        this.habitFreeze = habitFreeze;
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
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

