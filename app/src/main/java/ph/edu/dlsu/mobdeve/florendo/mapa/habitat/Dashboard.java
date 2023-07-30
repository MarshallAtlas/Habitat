package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ph.edu.dlsu.mobdeve.florendo.mapa.habitat.R;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private TextView CDisplayName;

    private TextView CGemCount;
    private TextView CFreezeCount;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    // RECYCLER VIEW STUFF
    private RecyclerView recyclerView; // recycler view

    private HabitAdapter HAdapter; // habait adapter
    private ArrayList<Habit> habitList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //TEST
    private ArrayList<Habit> generateStaticHabitList() {
        ArrayList<Habit> staticHabitList = new ArrayList<>();

        // Add some hardcoded habit names to the list
        staticHabitList.add(new Habit("Red", "Habit 1", 1, false, 0, "user_id_1"));
        staticHabitList.add(new Habit("Blue", "Habit 2", 0, true, 2, "user_id_1"));
        staticHabitList.add(new Habit("Green", "Habit 3", 2, false, 1, "user_id_2"));
        // Add more habits as needed...

        return staticHabitList;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        this.CDisplayName = this.findViewById(R.id.User_Welcome);
        this.CGemCount = this.findViewById(R.id.GemCount);
        this.CFreezeCount = this.findViewById(R.id.FreezeAmount);

        //RECYCLVER VIEW STUFF
        this.recyclerView = this.findViewById(R.id.Habit_List);
        this.recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // continue here

        // test we put habits into an array
        CollectionReference HabRefs = db.collection("habits");

        // Assuming you have a reference to the Firestore database


        habitList = new ArrayList<Habit>();
        //habitList = generateStaticHabitList();//
        HAdapter = new HabitAdapter(Dashboard.this, habitList);
        recyclerView.setAdapter(HAdapter);



        // Set the static habit list to the adapter

        eventChangeListener();



        // set name
        this.activityResultLauncher = this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) { // listen to callback

                    }
                }
        );
        FirebaseFirestore.setLoggingEnabled(true);
    }

    //get habits
    //TEST



    public void eventChangeListener() {
        // Listen for changes in the "habits" collection
        db.collection("habits")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(Dashboard.this, "RECYCLER FAIL", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if the value is not null and there are changes
                        if (value != null && !value.getDocumentChanges().isEmpty()) {
                            for (DocumentChange dc : value.getDocumentChanges()) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
//                                    Habit habit = dc.getDocument().toObject(Habit.class);
//                                    habitList.add(habit);
                                    //Toast.makeText(Dashboard.this, "INSIDE EVENT CHANGE", Toast.LENGTH_SHORT).show();
                                    habitList.add(dc.getDocument().toObject(Habit.class));


                                }
                            }

                            // Notify the adapter about the data change
                            HAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth cAuth = FirebaseAuth.getInstance();
        FirebaseUser cUser = cAuth.getCurrentUser();
        if (cUser != null) {
            //get user id
            String userId = cUser.getUid();
            //set display name to the user's display name
            this.CDisplayName.setText("Hello "+ cUser.getDisplayName());
            retrieve();//get gem count and freeze count
            //retrieveHabits(userId);
            //getHabitsFromFirestore(userId); // Retrieve habits from Firestore

        } else {
            this.finish();
        }



    }

    private void retrieveHabits(String userId) {
        db.collection("habits")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        habitList.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Habit habit = documentSnapshot.toObject(Habit.class);
                            habitList.add(habit);
                        }
                        HAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Dashboard.this, "Failed to retrieve habits", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // get gemcoutn and freeze count
    public void retrieve() {
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = cUser.getUid(); // Get the user ID

        //get from collections
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docref = db.collection("users").document(userId);

        // user desc retrieal
        docref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Long userGemCount = documentSnapshot.getLong("userGem");
                Long userFreezeCount = documentSnapshot.getLong("userFreeze");

                CGemCount.setText(String.valueOf(userGemCount));
                CFreezeCount.setText(String.valueOf(userFreezeCount));

            }
        });

    }

    public void logout (View CView){
        //terminates sessions
        FirebaseAuth.getInstance().signOut();
        this.finish();
    }
}