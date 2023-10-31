package com.itbd.examineradmin;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examineradmin.DataMoldes.AdminDataModel;
import com.itbd.examineradmin.DataMoldes.TeacherDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    EditText fullName, email, phone, autoPassword;
    TextView backTwo, signInText;
    Button signUp;
    ImageView visibilitySignUp;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignUpActivity.this, R.color.blue_pr));

        databaseReference = FirebaseDatabase.getInstance().getReference("Admin");

        mAuth = FirebaseAuth.getInstance();

        fullName = findViewById(R.id.edt_fullname);
        email = findViewById(R.id.edt_email);
        phone = findViewById(R.id.edt_phone);
        signUp = findViewById(R.id.btn_Signup);
        backTwo = findViewById(R.id.backtwo);
        signInText = findViewById(R.id.signintext);
        autoPassword = findViewById(R.id.auto_password);
        visibilitySignUp = findViewById(R.id.pass_invisi);

        //For auto Generated Password
        autoPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPassword.setText(randomPass(8));
            }
        });

        //go to Sign In activity
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //back button previous activity
        backTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Hide or show password
        visibilitySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autoPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    autoPassword.setTransformationMethod(new PasswordTransformationMethod());
                    visibilitySignUp.setImageResource(R.drawable.invisi_eye);
                } else {
                    autoPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visibilitySignUp.setImageResource(R.drawable.visi_eye);
                }
            }
        });


        //working on Signup Button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String FullName = fullName.getText().toString();
                String Phone = phone.getText().toString();
                String em = email.getText().toString().trim();
                String password = autoPassword.getText().toString().trim();

                if (em.isEmpty()) {
                    email.setError("Enter a Email Address");
                    email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                    email.setError("Enter a valid Email Address");
                    email.requestFocus();
                    return;
                }
                if (FullName.isEmpty()) {
                    fullName.setError("Enter a FullName");
                    fullName.requestFocus();
                    return;
                }
                if (Phone.isEmpty()) {
                    phone.setError("Enter a Phone Number");
                    phone.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(em, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            assert user != null;
                            String uId = user.getUid();

                            saveData(uId);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "User already Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

    }

    public void saveData(String uId) {
        String FullName = fullName.getText().toString().trim();
        String Email = email.getText().toString().trim();
        String Phone = phone.getText().toString().trim();

        if (TextUtils.isEmpty(FullName)) {
            fullName.setError("Full name is required");
            fullName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Email) && !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Phone)) {
            phone.setError("phone is required");
            phone.requestFocus();
            return;
        }

        AdminDataModel info = new AdminDataModel(FullName, Email, Phone, uId);
        databaseReference.child(uId).setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

    }

    public String randomPass(int length) {

        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*".toCharArray();
        Random r = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = chars[r.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}