package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignUp extends AppCompatActivity {

    private EditText CEmail;
    private EditText CUserName;
    private EditText CPassword;
    private EditText CConPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.CEmail = findViewById(R.id.Sign_Email);
        this.CUserName = findViewById(R.id.Sign_Uname);
        this.CPassword = findViewById(R.id.Sign_Pass);
        this.CConPass = findViewById(R.id.Sign_ConPassword);



    }

    public void returnToMain(){
        this.finish();
    }

    private void createUser(){
        FirebaseAuth cAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // get user input then turn to string
        String StrEmail = this.CEmail.getText().toString().trim();
        String StrUname = this.CUserName.getText().toString().trim();
        String StrPass = this.CPassword.getText().toString().trim();
        String StrConPass = this.CConPass.getText().toString().trim();
        int usergems = 0; // initialize empty
        int userfreeze = 0;


        cAuth.createUserWithEmailAndPassword(StrEmail, StrPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUp.this, "SUCCESS", Toast.LENGTH_LONG).show();
                            FirebaseUser cUser = cAuth.getCurrentUser();
                            String userId = cUser.getUid();

                            //set display name
                            UserProfileChangeRequest CProfileChange = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(StrUname)
                                    .build();
                            cUser.updateProfile(CProfileChange);

                            User newUser = new User(userId, StrUname);

                            //reference users collection
                            db.collection("users").document(userId)
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(SignUp.this, "Register SUCCESS", Toast.LENGTH_SHORT).show();
                                            returnToMain();
                                        }

                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUp.this, "Firestore Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(SignUp.this, "Email Already in use", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


    public void signupButton(View Cview) {
        String email = CEmail.getText().toString().trim();
        String username = CUserName.getText().toString().trim();
        String password = CPassword.getText().toString().trim();
        String conPassword = CConPass.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || conPassword.isEmpty()) {
            // Display a toast message if any of the fields are empty
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            // Display a toast message if the password is less than 8 characters
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(conPassword)) {
            // Display a toast message if password and confirm password do not match
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
        } else {
            // All fields are filled, password is at least 8 characters long, and password matches confirmation
            createUser();
        }
    }




}