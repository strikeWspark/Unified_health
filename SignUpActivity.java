package com.dryfire.unifiedhealth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private AppCompatImageView profilepic;
    private Toolbar signup_toolbar;
    private TextInputLayout aadhar_input,name_input,username_input,password_input;
    private TextInputLayout age_input,mobile_input,town_input;
    private TextInputEditText aadhar_edit,name_edit,username_edit,password_edit;
    private TextInputEditText age_edit,mobile_edit,town_edit;
    private MaterialButton signup;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabse;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth mAuth;
    private Uri resulturi = null;
    private final static int GALLERY_CODE = 1;
    private ProgressDialog signupdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupdialog = new ProgressDialog(this);
        mDatabse = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabse.getReference();

        //mDatabaseReference = mDatabse.getReference().child("MUsers");


        mAuth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MUsers_profiles");

        signup_toolbar = findViewById(R.id.uh_signup_toolbar);
        setSupportActionBar(signup_toolbar);

        signup_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                finish();
            }
        });

        initialization();

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        final  String name = name_edit.getText().toString().trim();
        final String email = username_edit.getText().toString().trim();
        String password = password_edit.getText().toString().trim();



        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(aadhar_edit.getText().toString()) && !TextUtils.isEmpty(age_edit.getText().toString())
        && !TextUtils.isEmpty(mobile_edit.getText().toString()) && !TextUtils.isEmpty(town_edit.getText().toString())){

            signupdialog.setMessage("Signing Up...");
            signupdialog.show();
            signupdialog.setCancelable(false);
            if(!isPasswordValid(password_edit.getText())){
                password_input.setError(getString(R.string.password_error));

            }else{
                password_input.setError(null);
            }

            mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if(authResult != null){

                        StorageReference imagePath = mFirebaseStorage.child("Muser_profile_pics")
                                .child(resulturi.getLastPathSegment());

                        imagePath.putFile(resulturi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String userid = mAuth.getCurrentUser().getUid();
                                //String aadharr=aadhar_edit.getText().toString().trim()
                                String anum=aadhar_edit.getText().toString().trim();
                                //DatabaseReference currenUserDb = mDatabaseReference.child(userid);
                                DatabaseReference currenUserDb = mDatabaseReference.child(anum);
                                currenUserDb.child("name").setValue(name);
                                currenUserDb.child("username").setValue(email);
                                currenUserDb.child("image").setValue(resulturi.toString());
                                currenUserDb.child("aadhar_no").setValue(anum);
                                currenUserDb.child("town").setValue(town_edit.getText().toString().trim());
                                currenUserDb.child("mobile").setValue(mobile_edit.getText().toString().trim());
                                currenUserDb.child("age").setValue(age_edit.getText().toString().trim());
                                currenUserDb.child("haemoglobin").setValue("none");
                                currenUserDb.child("heartrate").setValue("none");
                                currenUserDb.child("BMI").setValue("none");

                                SharedPreferences.Editor editor = getSharedPreferences("aadharNum", MODE_PRIVATE).edit();
                                editor.putString("aadhar", anum);
                                editor.apply();
                                Intent i=new Intent(SignUpActivity.this,HomeActivity.class);
                                startActivity(i);
                                finish();
                                signupdialog.dismiss();


                            }
                        });

                    }





                }
            });
        }else{
            aadhar_input.setError(getString(R.string.error_message));
            name_input.setError(getString(R.string.error_message));
            age_input.setError(getString(R.string.error_message));
            mobile_input.setError(getString(R.string.error_message));
            name_input.setError(getString(R.string.error_message));
            username_input.setError(getString(R.string.error_message));
            town_input.setError(getString(R.string.error_message));

        }
        password_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(isPasswordValid(password_edit.getText())){
                    password_input.setError(null);
                }
                return false;
            }
        });
    }


    private void initialization() {
        profilepic = findViewById(R.id.uh_profilepic);
        aadhar_input = findViewById(R.id.uh_signup_aadhar_no_input);
        aadhar_edit = findViewById(R.id.uh_signup_aadhar_no_edit);

        name_input = findViewById(R.id.uh_signup_name_input);
        name_edit = findViewById(R.id.uh_signup_name_edit);

        username_input = findViewById(R.id.uh_signup_username_input);
        username_edit = findViewById(R.id.uh_signup_username_edit);

        password_input = findViewById(R.id.uh_signup_password_input);
        password_edit = findViewById(R.id.uh_signup_password_edit);

        age_input = findViewById(R.id.uh_signup_age_input);
        age_edit = findViewById(R.id.uh_signup_age_edit);

        mobile_input = findViewById(R.id.uh_signup_mobile_input);
        mobile_edit = findViewById(R.id.uh_signup_mobile_edit);

        town_input = findViewById(R.id.uh_signup_town_input);
        town_edit = findViewById(R.id.uh_signup_town_edit);

        signup = findViewById(R.id.uh_signup);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Uri mImageUri = data.getData();
            resulturi=mImageUri;
            profilepic.setImageURI(mImageUri);
        }
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text != null && text.length() >= 8;
    }

}
