package co.rpoint.www.rpoint;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();



    }
    protected void onLoginClick(View view) {
        // Gets the Email and Password fields and retrieves the text inside
        EditText MyEmail = (EditText) findViewById(R.id.email);
        String email = MyEmail.getText().toString();
        EditText MyPassword = (EditText)findViewById((R.id.password));
        String password = MyPassword.getText().toString();
        // Gets a new instance of Firebase auth to use the sign in
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Attempts the sign in with a listener for the completion of the function
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User is logged in successfully.
                    // Forward user to MainActivity (Home page)
                    Intent Main = new Intent(Login.this, MainActivity.class);
                    startActivity(Main);

                } else {
                    // User did not log in.
                    // Show error that login was unsuccessful.
                    AlertDialog.Builder Error = new AlertDialog.Builder(Login.this);
                    Error.setTitle("Error");
                    Error.setMessage("The log in has failed. Please try again.");
                    Error.setCancelable(true);
                    Error.show();


                }
            }
            });
        }


}
