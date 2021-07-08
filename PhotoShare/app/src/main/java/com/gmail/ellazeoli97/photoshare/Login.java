package com.gmail.ellazeoli97.photoshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login_button;
    TextView goToReg;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        email = findViewById( R.id.emailLog );
        password = findViewById( R.id.passwordLog );
        login_button = findViewById( R.id.buttonLog );
        goToReg = findViewById( R.id.gotoregistration );
        progressBar = findViewById( R.id.progressBarLog );

        fAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String password_string = password.getText().toString().trim(); //trim() rimuove gli spazi bianchi

                if(TextUtils.isEmpty( mail ))
                {
                    email.setError( "Email requested!" );
                    return;
                }
                if(TextUtils.isEmpty( password_string ))
                {
                    password.setError( "Password requested!" );
                    return;
                }
                if(password_string.length()<6)
                {
                    password.setError( "La password must have more than 5 characters" );
                    return;
                }
                progressBar.setVisibility( View.VISIBLE );

                // User authentication
                fAuth.signInWithEmailAndPassword( mail, password_string ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText( Login.this, "Login done.", Toast.LENGTH_SHORT ).show();
                            startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                        }else{
                            Toast.makeText( Login.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                            progressBar.setVisibility( View.INVISIBLE );
                        }
                    }
                });
            }
        } );

        goToReg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), Registration.class ) );
            }
        } );
    }
}
