package ph.edu.dlsu.mobdeve.florendo.mapa.habitat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText CEmail;
    private EditText CPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        this.CEmail = this.findViewById(R.id.Input_Uname);
        this.CPassword = this.findViewById(R.id.Input_Pass);



    }
    public void loginValidate(View CView){
        String email = CEmail.getText().toString().trim();
        String password = CPassword.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(MainActivity.this, "Please Fill in all fields", Toast.LENGTH_SHORT).show();
        }else{
            login();
        }

    }

    public void login() {

        FirebaseAuth cAuth = FirebaseAuth.getInstance();

        String StrEmail = this.CEmail.getText().toString().trim();
        String StrPassword = this.CPassword.getText().toString().trim();

        cAuth.signInWithEmailAndPassword(StrEmail, StrPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //this CUser is the current session
                            FirebaseUser CUser = cAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
                            launchDashboard();

                        }else{
                            Toast.makeText(MainActivity.this, "ERROR LOGGING IN", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        // if success then we open intent

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth cAuth = FirebaseAuth.getInstance();
        FirebaseUser cUser = cAuth.getCurrentUser();
        if(cUser != null){
            // if cUser == null then there is no currently logged in user
            // if there is currently a log in it goes directly to screeen instead of log in
            this.launchDashboard();
        }
    }

    public void launchDashboard(){
        Intent CIntent = new Intent(MainActivity.this, Dashboard.class);
        this.startActivity(CIntent);
    }

    public void launchSignUp(View CView) {
        Intent CIntent = new Intent(MainActivity.this, SignUp.class);
        this.startActivity(CIntent);
    }
}