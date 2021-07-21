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

public class Registration extends AppCompatActivity {

    EditText fullname, email, password;
    TextView goToLogin;
    ProgressBar progress_bar;
    FirebaseAuth mAuth;
    Button reg_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        fullname = findViewById( R.id.fullname );
        email = findViewById( R.id.emailLog );
        password = findViewById( R.id.passwordReg );
        goToLogin = findViewById( R.id.gotologin );
        progress_bar = findViewById( R.id.progressBarReg );
        reg_button = findViewById( R.id.buttonReg );

        mAuth = FirebaseAuth.getInstance();

        //se l'utente è già registrato
        if(mAuth.getCurrentUser() != null)
        {
            startActivity( new Intent( getApplicationContext(), MainActivity.class ));
            finish();
        }

        reg_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim(); //trim() rimuove gli spazi bianchi
                String password_string = password.getText().toString().trim();

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
                    password.setError( "Password must have more than 5 characters" );
                    return;
                }
                progress_bar.setVisibility( View.VISIBLE );

                //Registriamo l'utente nella firebase
                mAuth.createUserWithEmailAndPassword(mail, password_string).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()) {
                            Toast.makeText(Registration.this, "User created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class ) );
                        }else{
                            Toast.makeText(Registration.this, "Error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                            progress_bar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        } );

        goToLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( getApplicationContext(), Login.class ) );
            }
        } );
    }
}
