package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ph.edu.dlsu.mobdeve.florendo.mapa.habitat.R;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Dashboard extends AppCompatActivity {

    private TextView CDisplayName;

    private TextView CGemCount;
    private TextView CFreezeCount;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.CDisplayName = this.findViewById(R.id.User_Welcome);
        this.CGemCount = this.findViewById(R.id.GemCount);
        this.CFreezeCount = this.findViewById(R.id.FreezeAmount);

        // set name

        this.activityResultLauncher = this.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) { // listen to callback

                    }
                }
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth cAuth = FirebaseAuth.getInstance();
        FirebaseUser cUser = cAuth.getCurrentUser();
        if (cUser != null) {
            //set display name to the user's display name
            this.CDisplayName.setText("Hello "+ cUser.getDisplayName());
            retrieve();//get gem count and freeze count
        } else {
            this.finish();
        }
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