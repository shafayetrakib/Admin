package com.itbd.examineradmin;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examineradmin.Adapter.CustomAdapter;
import com.itbd.examineradmin.DataMoldes.ExamDataModel;
import com.itbd.examineradmin.DataMoldes.TeacherDataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    ProgressBar mainProgressBar, dialogProgressBar;
    ListView listMainActivity, listCourse;
    List<ExamDataModel> examDataList = new ArrayList<>();
    List<TeacherDataModel> teacherDataList = new ArrayList<>();
    List<String> courseListData = new ArrayList<>();

    TextView txtSelectCourse, txtTitle;
    int identifyIntent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.blue_pr));

        identifyIntent = getIntent().getIntExtra("identifyIntent", -1);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mainProgressBar = findViewById(R.id.exam_progress_bar);

        txtSelectCourse = findViewById(R.id.txt_select_course_teacher);
        txtTitle = findViewById(R.id.txt_title);

        listMainActivity = findViewById(R.id.list_main_activity);

        switch (identifyIntent) {
            case 0:
                txtTitle.setText("Teachers");
                loadTeachers();
                break;
            case 1:
                txtTitle.setText("Students");
                break;
            case 2:
                txtTitle.setText("Exams");
                loadExamSet();
                break;
            case 3:
                txtTitle.setText("Courses");
                txtSelectCourse.setVisibility(View.GONE);
                break;
        }

        txtSelectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDialog();
            }
        });

    }

    private void showCourseDialog() {
        BottomSheetDialog courseSelectDialog = new BottomSheetDialog(MainActivity.this, R.style.bottom_sheet_dialog);
        Objects.requireNonNull(courseSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        courseSelectDialog.getBehavior().setSkipCollapsed(true);
        courseSelectDialog.getBehavior().setState(STATE_EXPANDED);
        courseSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

        dialogProgressBar = courseSelectDialog.findViewById(R.id.progress_bar);
        listCourse = courseSelectDialog.findViewById(R.id.course_list);
        loadCourseList(listCourse);

        listCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtSelectCourse.setText(courseListData.get(i));

                switch (identifyIntent) {
                    case 0:
                        if (txtSelectCourse.getText().toString().equals("All")) {
                            loadTeachers();
                        } else {
                            mainProgressBar.setVisibility(View.GONE);
                            loadSpecificTeachers(txtSelectCourse.getText().toString());
                        }
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "BONK1", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        if (txtSelectCourse.getText().toString().equals("All")) {
                            loadExamSet();
                        } else {
                            mainProgressBar.setVisibility(View.VISIBLE);
                            loadSpecificExamSet(txtSelectCourse.getText().toString());
                        }
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "BONK3", Toast.LENGTH_SHORT).show();
                        break;
                }

                courseSelectDialog.dismiss();
            }
        });
        courseSelectDialog.show();
    }

    private void loadSpecificTeachers(String course) {
        databaseReference
                .child("Teacher")
                .orderByChild("course")
                .equalTo(course)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teacherDataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            TeacherDataModel teacherDataModel = dataSnapshot.getValue(TeacherDataModel.class);

                            teacherDataList.add(teacherDataModel);
                        }
                        mainProgressBar.setVisibility(View.GONE);
                        listMainActivity.setVisibility(View.VISIBLE);

                        CustomAdapter teacherListAdapter = new CustomAdapter(MainActivity.this, teacherDataList.size(), R.layout.item_list_teacher);
                        teacherListAdapter.setTeacherDataModelList(teacherDataList);

                        listMainActivity.setAdapter(teacherListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTeachers() {
        databaseReference.child("Teacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TeacherDataModel teacherDataModel = dataSnapshot.getValue(TeacherDataModel.class);

                    teacherDataList.add(teacherDataModel);
                }
                mainProgressBar.setVisibility(View.GONE);
                listMainActivity.setVisibility(View.VISIBLE);

                CustomAdapter teacherListAdapter = new CustomAdapter(MainActivity.this, teacherDataList.size(), R.layout.item_list_teacher);
                teacherListAdapter.setTeacherDataModelList(teacherDataList);

                listMainActivity.setAdapter(teacherListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCourseList(ListView listView) {
        databaseReference
                .child("courseList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseListData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            courseListData.add(dataSnapshot.getValue(String.class));
                        }
                        dialogProgressBar.setVisibility(View.GONE);

                        listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
                                R.layout.list_item_course,
                                R.id.txt_list_item, courseListData));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadExamSet() {
        databaseReference
                .child("examSet")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        examDataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ExamDataModel examDataModel = dataSnapshot.getValue(ExamDataModel.class);
                            examDataList.add(examDataModel);
                        }

                        mainProgressBar.setVisibility(View.GONE);
                        listMainActivity.setVisibility(View.VISIBLE);

                        CustomAdapter examListAdapter = new CustomAdapter(MainActivity.this, examDataList.size(), R.layout.list_item_exam);
                        examListAdapter.setExamDataModelList(examDataList);

                        listMainActivity.setAdapter(examListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadSpecificExamSet(String course) {
        databaseReference
                .child("examSet")
                .orderByChild("course")
                .equalTo(course)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        examDataList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ExamDataModel examDataModel = dataSnapshot.getValue(ExamDataModel.class);
                            examDataList.add(examDataModel);
                        }
                        mainProgressBar.setVisibility(View.GONE);
                        listMainActivity.setVisibility(View.VISIBLE);

                        CustomAdapter examListAdapter = new CustomAdapter(MainActivity.this, examDataList.size(), R.layout.list_item_exam);
                        examListAdapter.setExamDataModelList(examDataList);

                        listMainActivity.setAdapter(examListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}