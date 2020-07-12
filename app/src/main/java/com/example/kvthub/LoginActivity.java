package com.example.kvthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final private String EMAIL_KEY = "USEREMAIL";
    private EditText edtemail, edtPassword;
    private Button btnsignup, btnsignin;
    private ToggleButton forpass;
    private ImageView Roll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            transitiontosocialmediaactivity();
        }
        edtemail = findViewById(R.id.edtemail);
        edtPassword = findViewById(R.id.edtpassword);
        Roll = findViewById(R.id.roll);
        if(getPreferences(MODE_PRIVATE) != null){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String e = sharedPreferences.getString(EMAIL_KEY, "");
            edtemail.setText(e);
        }

        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    signin();
                }

                return false;
            }
        });

        btnsignin = findViewById(R.id.btnsignin);
        btnsignup = findViewById(R.id.btnsignup);
        forpass = findViewById(R.id.hideorshow);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        forpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    forpass.setButtonDrawable(R.drawable.ic_baseline_visibility_off_24);
                    edtPassword.setTransformationMethod(null);
                }else {
                    forpass.setButtonDrawable(R.drawable.ic_baseline_visibility_24);
                    edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });



    }


    private  void signup(){

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);

    }

    private void signin(){
        if (edtemail.getText().toString().equals("") || edtPassword.getText().toString().equals("")){
            FancyToast.makeText(LoginActivity.this, "Enter all required details", FancyToast.LENGTH_LONG, FancyToast.INFO, false).show();
        }else {
            Roll.animate().rotation(-2160).setDuration(5000);
            mAuth.signInWithEmailAndPassword(edtemail.getText().toString(), edtPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SharedPreferences  sharedPreferences = getPreferences(MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(EMAIL_KEY, edtemail.getText().toString());
                                editor.apply();
                                transitiontosocialmediaactivity();
                                edtPassword.setText("");
                            } else {
                                FancyToast.makeText(LoginActivity.this, "Signin failed", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                            }
                        }
                    });
        }
    }

    private void transitiontosocialmediaactivity(){

        Intent intent = new Intent(LoginActivity.this, Social.class);
        startActivity(intent);

    }

    public void LoginlayoutTapped(View v){
        try{
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}