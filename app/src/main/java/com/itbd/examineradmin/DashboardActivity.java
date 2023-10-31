package com.itbd.examineradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examineradmin.DataMoldes.AdminDataModel;
import com.itbd.examineradmin.DataMoldes.TeacherDataModel;
import com.itbd.examineradmin.Fragments.DashFragment;
import com.itbd.examineradmin.Fragments.ProfileFragment;
import com.itbd.examineradmin.Fragments.ResourceFragment;
import com.itbd.examineradmin.Fragments.ResultFragment;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity {
    private static final String PREF_NAME = "ExaminerAdmin";
    BottomNavigationView bottomNav;
    AdminDataModel adminDataModel;
    String uID, uName;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPreferences;

    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().setStatusBarColor(ContextCompat.getColor(DashboardActivity.this, R.color.blue_pr));

        loadingDialog = new Dialog(DashboardActivity.this);

        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.dialog_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        uID = sharedPreferences.getString("uID", "");

        bottomNav = findViewById(R.id.botton_navigationbar);

        loadUserData();

        bottomNav.getMenu().findItem(R.id.dashboard).setChecked(true);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.profile) {
                    loadFrag(ProfileFragment.getInstance(adminDataModel), 1);
                } else if (item.getItemId() == R.id.dashboard) {
                    loadFrag(DashFragment.getInstance(adminDataModel), 1);
                } else if (item.getItemId() == R.id.result) {
                    loadFrag(new ResultFragment(), 1);
                } else if (item.getItemId() == R.id.resource) {
                    loadFrag(ResourceFragment.getInstance(uID, uName), 1);
                }

                return true;
            }
        });
    }

    public void loadFrag(Fragment fragment, int flag) {

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragManager.beginTransaction();

        if (flag == 0) {
            fragTrans.add(R.id.frame_Layout, fragment);
        } else {
            fragTrans.replace(R.id.frame_Layout, fragment);
        }

        fragTrans.commit();
    }

    public void loadUserData() {
        databaseReference.child("Admin").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminDataModel = snapshot.getValue(AdminDataModel.class);

                assert adminDataModel != null;
                uName = adminDataModel.getName();

                loadFrag(DashFragment.getInstance(adminDataModel), 0);
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}