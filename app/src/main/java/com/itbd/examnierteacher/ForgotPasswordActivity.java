package com.itbd.examnierteacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;



public class ForgotPasswordActivity extends AppCompatActivity {
    Button send;
    TextView backThree;
    EditText forgotEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        send =findViewById(R.id.btn_send);
        backThree = findViewById(R.id.backthree);
        forgotEmail =findViewById(R.id.forgot_email);

        getWindow().setStatusBarColor(ContextCompat.getColor(ForgotPasswordActivity.this,R.color.blue_pr));

        mAuth = FirebaseAuth.getInstance();

        backThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, SignInActivity.class));
            }
        });

    //send mail for forget password
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=forgotEmail.getText().toString().trim();
                if(email.isEmpty()){
                    forgotEmail.setError("Enter your Email");
                    forgotEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    forgotEmail.setError("Enter a valid Email");
                    forgotEmail.requestFocus();
                    return;
                }
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ForgotPasswordActivity.this, "Try again something wrong happened", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

}