package com.dryfire.unifiedhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    private TextInputLayout usernameinput,passwordinput,aadhar_input;
    private TextInputEditText usernameedit,passwordedit,aadhar_edit;
    private MaterialButton next,cancel,signup,exit,cancel_dialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private ProgressDialog logindialog;
    private AlertDialog.Builder dialogbuilder;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        logindialog = new ProgressDialog(this);
        dialogbuilder = new AlertDialog.Builder(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if(mUser != null){
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = getSharedPreferences("aadharNum", MODE_PRIVATE).edit();
                    editor.putString("aadhar", aadhar_edit.getText().toString().trim());
                    editor.apply();
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();
                }else{
                    Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };



        initialised();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(usernameedit.getText().toString()) && !TextUtils.isEmpty(passwordedit.getText().toString())){

                    logindialog.setMessage("Signing In...");
                    logindialog.show();
                    logindialog.setCancelable(false);
                    String email = usernameedit.getText().toString();
                    String pwd = passwordedit.getText().toString();

                    login(email,pwd);
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.exit_layout,null);

                dialogbuilder.setView(view);
                dialog = dialogbuilder.create();
                dialog.show();
                exit = view.findViewById(R.id.layout_exit);
                cancel_dialog = view.findViewById(R.id.layout_cancel);
                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);

                    }
                });
                cancel_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            dialog.dismiss();
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
            }
        });





    }

    private void login(String email, String pwd) {

        mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    finish();
                    logindialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initialised() {

        usernameinput = findViewById(R.id.uh_username_input);
        usernameedit = findViewById(R.id.uh_username_edit);

        passwordinput = findViewById(R.id.uh_password_input);
        passwordedit = findViewById(R.id.uh_password_edit);

        aadhar_input = findViewById(R.id.uh_aadhar_input);
        aadhar_edit = findViewById(R.id.uh_aadhar_edit);

        next = findViewById(R.id.uh_next_button);

        cancel = findViewById(R.id.uh_cancel_button);

        signup = findViewById(R.id.uh_signup_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
