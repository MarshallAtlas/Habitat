package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateHabit extends AppCompatActivity {

    private EditText etHabitName;
    private RadioGroup rgColor;
    private Button createButton;
    private FirebaseFirestore db;
    private CollectionReference habitsCollection;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_habit);

        etHabitName = findViewById(R.id.add_habit_name);
        rgColor = findViewById(R.id.color_select);
        createButton = findViewById(R.id.createHabitButton);

        db = FirebaseFirestore.getInstance();
        habitsCollection = db.collection("habits");
        auth = FirebaseAuth.getInstance();

        createButton.setOnClickListener(view -> createHabit());


    }

    private void createHabit() {
        String habitName = etHabitName.getText().toString().trim();
        String selectedColor = getSelectedColor();

        if (habitName.isEmpty()) {
            etHabitName.setError("HABIT NAME MISSING");
            etHabitName.requestFocus();
            return;
        }

        if (selectedColor.isEmpty()) {
            Toast.makeText(this, "MISSING COLOR", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            int habitFreeze = 0;
            boolean habitDone = false;
            int streak = 0;
            String lastUpdate = "0";

            Habit habit = new Habit(selectedColor, habitName, habitFreeze, habitDone, streak, userId, lastUpdate);

            habitsCollection.add(habit)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Habit created successfully", Toast.LENGTH_SHORT).show();
                        returnToDash();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to create habit", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getSelectedColor() {
        int selectedColorId = rgColor.getCheckedRadioButtonId();
        if (selectedColorId == -1) {
            return "";
        }

        RadioButton radioButton = findViewById(selectedColorId);
        return radioButton.getText().toString();
    }

    public void returnToDash(){
        this.finish();
    }
}
