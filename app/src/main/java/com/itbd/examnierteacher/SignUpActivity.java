package com.itbd.examnierteacher;

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
import com.itbd.examnierteacher.DataMoldes.TeacherDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {
    EditText fullName, email, phone, autoPassword;
    TextView backTwo, signInText;
    Button signUp;
    ImageView visibilitySignUp;
    ProgressBar progressBar;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Variable for Course List
    private DatabaseReference mReference;
    List<String> courseListData = new ArrayList<>();
    TextView txtSelectCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignUpActivity.this, R.color.blue_pr));

        mReference = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("Teacher");

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

        loadCourseList();

        // Making The Dialog
        BottomSheetDialog courseSelectDialog = new BottomSheetDialog(this, R.style.bottom_sheet_dialog);
        Objects.requireNonNull(courseSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        courseSelectDialog.getBehavior().setSkipCollapsed(true);
        courseSelectDialog.getBehavior().setState(STATE_EXPANDED);
        courseSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

        RelativeLayout courseSelectorLayout = findViewById(R.id.course_selector_layout);
        txtSelectCourse = findViewById(R.id.txt_select_course);
        courseSelectorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseSelectDialog.show();
            }
        });

        // Making The List of Course
        ListView courseList = courseSelectDialog.findViewById(R.id.course_list);
        progressBar = courseSelectDialog.findViewById(R.id.progress_bar);
        assert courseList != null;
        courseList.setAdapter(new ArrayAdapter<>(SignUpActivity.this,
                R.layout.list_item_course,
                R.id.txt_list_item, courseListData));

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtSelectCourse.setText(courseListData.get(i));
                courseSelectDialog.dismiss();
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
        String Course = txtSelectCourse.getText().toString().trim();

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

        TeacherDataModel info = new TeacherDataModel(FullName, Email, Phone, Course, uId);
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

    private void loadCourseList() {
        mReference.child("courseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseListData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    courseListData.add(dataSnapshot.getValue(String.class));
                }
                courseListData.remove("All");
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUpActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}