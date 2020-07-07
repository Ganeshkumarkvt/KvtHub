package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

public class SignupActivity extends AppCompatActivity {

    private ToggleButton pass;
    private FirebaseAuth mauth;
    private EditText edtName, edtDOB, edtPhone, edtEmail, edtPassword;
    private Button signupbtn;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign Up");

        database = FirebaseDatabase.getInstance();

        mauth = FirebaseAuth.getInstance();

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

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("signing up, please wait");
            progressDialog.show();

            mauth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            DatabaseReference userref = database.getReference().child("my_users").child(task.getResult().getUser().getUid());

                            if (task.isSuccessful()) {
                                Map<String, String> users = new HashMap<>();
                                users.put("Name", edtName.getText().toString());
                                users.put("Date_Of_Birth", edtDOB.getText().toString());
                                users.put("Mobile_Number", edtDOB.getText().toString());
                                users.put("E-mail", edtEmail.getText().toString());

                                userref.setValue(users);

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(edtName.getText().toString())
                                        .build();
                                mauth.getCurrentUser().updateProfile(userProfileChangeRequest)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SignupActivity.this, "Display name updated", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                FancyToast.makeText(SignupActivity.this, "Hi, " + edtName.getText().toString() + " signup successful \nnow verify your Email before Login",
                                        FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                mauth.signOut();
                                finish();
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