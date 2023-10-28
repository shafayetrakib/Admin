package com.itbd.examnierteacher.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examnierteacher.R;
import com.itbd.examnierteacher.CustomAdapter;
import com.itbd.examnierteacher.DataMoldes.ExamDataModel;
import com.itbd.examnierteacher.DataMoldes.TeacherDataModel;
import com.itbd.examnierteacher.ExamSetActivity;
import com.itbd.examnierteacher.Show_TandS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DashFragment extends Fragment {
    private com.itbd.examnierteacher.CustomAdapter CustomAdapter;
    private static final String U_DATA = "arg1";
    ListView listExam;
    ProgressBar dashProgressBar;
    TextView txtUserName, btnExamCreate,btnTeacher,btnStudent,btnShowExam;
    DatabaseReference databaseReference;
    List<ExamDataModel> examDataList = new ArrayList<>();
    TeacherDataModel teacherDataModelData;

    public DashFragment() {

    }

    public static DashFragment getInstance(TeacherDataModel userData) {
        DashFragment dashFragment = new DashFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable(U_DATA, userData);

        dashFragment.setArguments(bundle);
        return dashFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (getArguments() != null) {
            teacherDataModelData = (TeacherDataModel) getArguments().getSerializable(U_DATA);
        }

        loadExamSet();

        //dashProgressBar = view.findViewById(R.id.dash_progress_bar);
        txtUserName = view.findViewById(R.id.txt_user_name);
        listExam = view.findViewById(R.id.list_exam);
        btnExamCreate = view.findViewById(R.id.exam_creatfirst);
        btnTeacher=view.findViewById(R.id.txt_Teacher);
        btnStudent=view.findViewById(R.id.student);
        btnShowExam=view.findViewById(R.id.txt_show_exam);

        txtUserName.setText(teacherDataModelData.getFullName());

        Collections.reverse(examDataList);
        CustomAdapter = new CustomAdapter(DashFragment.super.getActivity(), examDataList);

        btnExamCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goExamSet = new Intent(requireActivity(), ExamSetActivity.class);
                goExamSet.putExtra("identifyIntent", 1);
                goExamSet.putExtra("userCourse", teacherDataModelData.getCourse());
                startActivity(goExamSet);
            }
        });
        btnTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showActivity=new Intent(requireActivity(), Show_TandS.class);
                startActivity(showActivity);
            }
        });
        btnShowExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listExam.setVisibility(View.VISIBLE);
            }
        });

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Showactivity=new Intent(requireActivity(),Show_TandS.class);
                startActivity(Showactivity);
            }
        });

        return view;
    }

    private void loadExamSet() {
        databaseReference.child("examSet")
                .orderByChild("course")
                .equalTo(teacherDataModelData.getCourse()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        examDataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ExamDataModel examDataModel = dataSnapshot.getValue(ExamDataModel.class);
                            examDataList.add(examDataModel);
                        }
                        //dashProgressBar.setVisibility(View.GONE);
                        //listExam.setVisibility(View.VISIBLE);
                        listExam.setAdapter(CustomAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}