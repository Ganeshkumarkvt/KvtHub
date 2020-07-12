package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private ToggleButton pass;
    private FirebaseAuth mauth;
    private EditText edtName, edtDOB, edtPhone, edtEmail, edtPassword;
    private Button signupbtn;
    private FirebaseDatabase database;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign Up");

        database = FirebaseDatabase.getInstance();

        mauth = FirebaseAuth.getInstance();
        userData = new UserData();
        pass = findViewById(R.id.passbtn);
        edtName = (EditText)findViewById(R.id.edtName);
        edtDOB = (EditText)findViewById(R.id.edtDOB);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtpassword);

        signupbtn = findViewById(R.id.Signupbtn);

        pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pass.setButtonDrawable(R.drawable.ic_baseline_visibility_off_24);
                    edtPassword.setTransformationMethod(null);
                }else {
                    pass.setButtonDrawable(R.drawable.ic_baseline_visibility_24);
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupbtn();
            }
        });

    }

    private  void signupbtn() {
        if (edtName.getText().toString().equals("") && edtDOB.getText().toString().equals("")
                && edtPhone.getText().toString().equals("") && edtEmail.getText().toString().equals("") && edtPassword.getText().toString().equals("")) {
            FancyToast.makeText(SignupActivity.this, "Enter all required details", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();

        } else {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("signing up, please wait");
            progressDialog.show();

            mauth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            DatabaseReference userref = database.getReference().child("MyUsers").child(task.getResult().getUser().getUid());

                            if (task.isSuccessful()) {
                                userData.setProfile(edtName.getText().toString());
                                userData.setDateOfBirth(edtDOB.getText().toString());
                                userData.setEmail(edtEmail.getText().toString());
                                userData.setMobileNumber(edtPhone.getText().toString());

                                userref.setValue(userData);

                                Objects.requireNonNull(mauth.getCurrentUser()).sendEmailVerification();

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(edtName.getText().toString())
                                        .build();
                                mauth.getCurrentUser().updateProfile(userProfileChangeRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FancyToast.makeText(SignupActivity.this, "Hi, " + edtName.getText().toString() + " signup successful \nNow verify your Email before Login",
                                                            FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                                }
                                            }
                                        });
                                progressDialog.dismiss();
                                mauth.signOut();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {

                                FancyToast.makeText(SignupActivity.this, "Sign up failed, try again",
                                        FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                        }

                    });
        }
    }

    public void SignuplayoutTapped(View v){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}