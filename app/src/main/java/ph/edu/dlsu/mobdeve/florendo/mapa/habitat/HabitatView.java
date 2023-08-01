package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HabitatView extends AppCompatActivity {

    private TextView habit_name;
    private TextView streak;
    private TextView freeze;
    private Button doneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitat_view);

        Intent intent = getIntent();
        Habit habit = (Habit) intent.getSerializableExtra("habit");

        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        //connect the thingies to the xml
        // set string first
        String StrHabit_name;
        int intStreak;
        int intFreeze;
        this.habit_name = findViewById(R.id.Habit_Name_Display);
        this.streak = findViewById(R.id.streak_display);
        this.freeze = findViewById(R.id.freeze_display);
        this.doneButton = (findViewById(R.id.habit_done_button));


        this.habit_name.setText(habit.getHabitName());
        this.streak.setText(String.valueOf(habit.getStreak()));
        this.freeze.setText(String.valueOf(habit.getHabitFreeze()));


        if(habit.getLastUpdate().equals(currentDate)){
            doneButton.setText("You are done for today!");
            doneButton.setBackgroundTintList(ContextCompat.getColorStateList(HabitatView.this, R.color.button_done_color));
        }else{ // if its the next day
            // if false reset text and color
            doneButton.setText("Habit Done");
            doneButton.setBackgroundTintList(ContextCompat.getColorStateList(HabitatView.this, R.color.button_normal_color));

            //reset
            if(compareStrings(habit.getLastUpdate(), currentDate) == -1){ // if its been 2 days since last update
                if(compareStrings(habit.getLastUpdate(), "0") != 0) {
                    // if its the first time this was accessed then do not reset
                    Toast.makeText(this, "Just in time" + habit.getLastUpdate(), Toast.LENGTH_SHORT).show();
                }else{
                    // if the getLastUpdate +1  is less than currentdate
                    // then reset streak
                    int resetStreak = 0;
                    habit.setStreak((resetStreak));
                    Toast.makeText(HabitatView.this, "You missed a habit :(", Toast.LENGTH_SHORT).show();
                    updateStreakInFirestore(habit.getHabitName(), resetStreak, currentDate);
                    this.streak.setText(String.valueOf(habit.getStreak())); // update display
                }

            }

        }

        // check reset




        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //reset need the date
                String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());

                // Get the last clicked date from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);


                if (habit.getLastUpdate().equals(currentDate)) {
                    Toast.makeText(HabitatView.this, "COME BACK AND DO THIS TOMORROW", Toast.LENGTH_LONG).show();
                }else {
                    // Increment the streak by 1
                    int newStreak = habit.getStreak() + 1;
                    habit.setStreak(newStreak);
                    //Toast.makeText(HabitatView.this, "STREAK: "+ newStreak, Toast.LENGTH_SHORT).show();
                    // Set habitDone to true
                    habit.setHabitDone(true);
                    String lastUpdate = currentDate;


                    // changeButton
                    doneButton.setText("You are done for today!");

                    updateHabitInFirestore(habit.getHabitName(), newStreak, true, lastUpdate);
                    //update values
                    streak.setText(String.valueOf(habit.getStreak()));
                    doneButton.setBackgroundTintList(ContextCompat.getColorStateList(HabitatView.this, R.color.button_done_color));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.commit(); // Use commit() instead of apply()
                    habit.setLastUpdate(currentDate);
                }
            }
        });


    }

    // HABIT UPDATE

    // This method updates the "streak" and "habitDone" fields in Firestore
    private void updateHabitInFirestore(String habitName, int newStreak, boolean habitDone, String lastupdate) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference habitsRef = db.collection("habits");

        // Query for the specific habit using its name
        habitsRef.whereEqualTo("habitName", habitName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // There should be only one document that matches the query
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String habitDocumentId = document.getId();

                            // Get the current habit details from Firestore
                            Habit habit = document.toObject(Habit.class);

                            // Modify the streak and "habitDone"
                            habit.setStreak(newStreak);
                            habit.setHabitDone(habitDone);
                            habit.setLastUpdate(lastupdate);

                            // Update the habit document in Firestore
                            habitsRef.document(habitDocumentId)
                                    .set(habit) // Update the document with the modified habit object
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(HabitatView.this, "YOU DID A HABIT!", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HabitatView.this, "I DONT FEEL SOO GOD ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }


    //STREAK RESET
    void updateStreakInFirestore(String habitName, int streak, String currentdate){

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        CollectionReference habitsRef = db.collection("habits");


        habitsRef.whereEqualTo("habitName", habitName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // There should be only one document that matches the query
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String habitDocumentId = document.getId();

                            // Get the current habit details from Firestore
                            Habit habit = document.toObject(Habit.class);

                            // Modify the streak and "habitDone"
                            habit.setStreak(streak);
                            habit.setLastUpdate(currentdate);

                            // Update the habit document in Firestore
                            habitsRef.document(habitDocumentId)
                                    .set(habit) // Update the document with the modified habit object
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(HabitatView.this, "You missed a habit :(", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(HabitatView.this, "I DONT FEEL SOO GOD ", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

    }

    public int compareStrings(String str1, String str2) {
        int num1, num2;

        try {
            num1 = Integer.parseInt(str1); //last update
            num2 = Integer.parseInt(str2); // current date
            num1++;
        } catch (NumberFormatException e) {

            return 0;
        }

        // Compare the two integers
        if (num1 > num2) {
            // str1 has a higher value than str2
            return 1;
        } else if (num1 < num2) {
            // str2 has a higher value than str1
            return -1;
        } else {
            // Both are equal
            return 0;
        }
    }



}