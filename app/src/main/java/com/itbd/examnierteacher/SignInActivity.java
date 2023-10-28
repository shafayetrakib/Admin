package com.itbd.examnierteacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private static final String PREF_NAME = "ExaminerTeacher";
    EditText passwordSignin, emailSignin;
    TextView mainforgot, signuptext;
    ImageView visibility;
    Button signin;
    TextView backOne;
    private FirebaseAuth mAuth;
    CheckBox rememberMe;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailSignin = findViewById(R.id.email_signin);
        passwordSignin = findViewById(R.id.passwor_signin);
        visibility = findViewById(R.id.pass_invisiable);
        signin = findViewById(R.id.btn_Signin);
        mainforgot = findViewById(R.id.password_forgot);
        backOne = findViewById(R.id.backone);
        signuptext = findViewById(R.id.signuptext);
        rememberMe = findViewById(R.id.rememberme);
        progressBar = findViewById(R.id.progresss);

        getWindow().setStatusBarColor(ContextCompat.getColor(SignInActivity.this, R.color.blue_pr));

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        //Goto Sign Up Activity
        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });

        // back button for back activity
        backOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        //Forgot password
        mainforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });
        //Hide or show password
        visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordSignin.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    passwordSignin.setTransformationMethod(new PasswordTransformationMethod());
                    visibility.setImageResource(R.drawable.invisi_eye);
                } else {
                    passwordSignin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visibility.setImageResource(R.drawable.visi_eye);
                }
            }
        });
        // Work signIn Activity with signup button
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start rememberMe
                String Mail = emailSignin.getText().toString().trim();
                String signinpassword = passwordSignin.getText().toString().trim();

                if (rememberMe.isChecked()) {
                    editor.putBoolean("userCheck", true);
                }

                //End of remember Me

                String email = emailSignin.getText().toString().trim();
                String password = passwordSignin.getText().toString().trim();

                if (email.isEmpty()) {
                    emailSignin.setError("Enter a Email Address");
                    emailSignin.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailSignin.setError("Enter a valid Email Address");
                    emailSignin.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    passwordSignin.setError("Enter a password");
                    passwordSignin.requestFocus();
                    return;
                }
                if (password.length() < 8) {
                    passwordSignin.setError("Minimum eight digit password ");
                    passwordSignin.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            String uID = user.getUid();

                            editor.putString("uID", uID);
                            editor.apply();

                            startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "SignIn Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        editor.apply();
    }
}