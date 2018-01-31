package com.varshney.emailverification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MA";

    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseAuth mAuth;
    TextView tvResult;
    Button btnCreate;
    EditText etEmail;
    EditText etPass;
    Button btnSignIn;
    TextView tvVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        tvResult = findViewById(R.id.tvResult);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreate = findViewById(R.id.btnCreate);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        tvVerified = findViewById(R.id.tvVerified);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(etEmail.getText().toString(),etPass.getText().toString());
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(etEmail.getText().toString(),etPass.getText().toString());
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    sendVerificationEmail();
                } else {
                    // User is signed out

                }
                // ...
            }
        };

    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent


                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: Email Sent");
                            //startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            Log.d(TAG, "onComplete: Email Not Sent");
                            Toast.makeText(MainActivity.this, "Email Not Sent", Toast.LENGTH_SHORT).show();
                            //startActivity(getIntent());

                        }
                    }
                });
    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            //finish();
            Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(this, "Not Verified User", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            
            //restart this activity

        }
    }

    private void signIn(String email,String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            tvResult.setText(user.toString());
                            checkIfEmailVerified();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            //updateUI(null);
                            tvResult.setText("SignIn Failure");
                        }

                        // ...
                    }
                });
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            tvResult.setText(user.toString());
                            sendVerificationEmail();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                           // Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                             //       Toast.LENGTH_SHORT).show();
                            tvResult.setText("Sign Up Failed");
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }




}
