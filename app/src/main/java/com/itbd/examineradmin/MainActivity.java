package com.itbd.examineradmin;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examineradmin.Adapter.CustomAdapter;
import com.itbd.examineradmin.DataMoldes.CourseDataModel;
import com.itbd.examineradmin.DataMoldes.ExamDataModel;
import com.itbd.examineradmin.DataMoldes.StudentDataModel;
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
    List<StudentDataModel> studentDataList = new ArrayList<>();
    List<CourseDataModel> courseListData = new ArrayList<>();

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
                loadStudents();
                break;
            case 2:
                txtTitle.setText("Exams");
                loadExamSet();
                break;
            case 3:
                txtTitle.setText("Courses");
                txtSelectCourse.setText("Add Course");
                loadCourse();
                break;
        }

        if (identifyIntent == 3) {
            txtSelectCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog addCourseDialog = new Dialog(MainActivity.this);
                    addCourseDialog.setContentView(R.layout.dialog_add_course);
                    Objects.requireNonNull(addCourseDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    addCourseDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    addCourseDialog.getWindow().setGravity(Gravity.BOTTOM);

                    EditText edtCourseName = addCourseDialog.findViewById(R.id.edt_course_name);
                    AppCompatButton btnAddCourse = addCourseDialog.findViewById(R.id.btn_add_course);

                    addCourseDialog.show();

                    btnAddCourse.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String courseName = edtCourseName.getText().toString().trim();

                            if (courseName.isEmpty()) {
                                edtCourseName.setError("Please enter course name");
                                return;
                            }
                            if (courseName.matches(".*[^a-zA-Z/\\-& ].*")) {
                                edtCourseName.setError("Please remove special character");
                                return;
                            }

                            String courseKey = databaseReference.push().getKey();
                            databaseReference.child("courseList").child(courseKey).setValue(new CourseDataModel(courseName, courseKey)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                        addCourseDialog.dismiss();
                                    } else {
                                        Toast.makeText(MainActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        addCourseDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        } else {
            txtSelectCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCourseDialog();
                }
            });
        }

    }

    // Load courses for adding and delete
    private void loadCourse() {
        databaseReference.child("courseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseListData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    courseListData.add(dataSnapshot.getValue(CourseDataModel.class));
                }

                courseListData.removeIf(courseDataModel -> courseDataModel.getCourseName().equals("All"));

                mainProgressBar.setVisibility(View.GONE);
                listMainActivity.setVisibility(View.VISIBLE);
                listMainActivity.setDividerHeight(15);

                CustomAdapter courseListAdapter = new CustomAdapter(MainActivity.this, courseListData.size(), R.layout.item_list_course_delete);
                courseListAdapter.setCourseList(courseListData);

                listMainActivity.setAdapter(courseListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load specific courses student
    private void loadSpecificStudents(String course) {
        databaseReference.child("student").orderByChild("course").equalTo(course).addValueEventListener(new ValueEventListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StudentDataModel studentDataModel = dataSnapshot.getValue(StudentDataModel.class);

                    studentDataList.add(studentDataModel);
                }
                mainProgressBar.setVisibility(View.GONE);
                listMainActivity.setVisibility(View.VISIBLE);

                CustomAdapter studentListAdapter = new CustomAdapter(MainActivity.this, studentDataList.size(), R.layout.item_list_student);
                studentListAdapter.setStudentDataModelList(studentDataList);

                listMainActivity.setAdapter(studentListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load all courses student
    private void loadStudents() {
        databaseReference.child("student").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StudentDataModel studentDataModel = dataSnapshot.getValue(StudentDataModel.class);

                    studentDataList.add(studentDataModel);
                }
                mainProgressBar.setVisibility(View.GONE);
                listMainActivity.setVisibility(View.VISIBLE);

                CustomAdapter studentListAdapter = new CustomAdapter(MainActivity.this, studentDataList.size(), R.layout.item_list_student);
                studentListAdapter.setStudentDataModelList(studentDataList);

                listMainActivity.setAdapter(studentListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load specific course teachers
    private void loadSpecificTeachers(String course) {
        databaseReference.child("Teacher").orderByChild("course").equalTo(course).addValueEventListener(new ValueEventListener() {
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

    // Load all teachers
    private void loadTeachers() {
        databaseReference.child("Teacher").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ClickableViewAccessibility")
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

    // Showing bottom dialog of course list
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
                txtSelectCourse.setText(courseListData.get(i).getCourseName());

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
                        if (txtSelectCourse.getText().toString().equals("All")) {
                            loadStudents();
                        } else {
                            mainProgressBar.setVisibility(View.GONE);
                            loadSpecificStudents(txtSelectCourse.getText().toString());
                        }
                        break;
                    case 2:
                        if (txtSelectCourse.getText().toString().equals("All")) {
                            loadExamSet();
                        } else {
                            mainProgressBar.setVisibility(View.VISIBLE);
                            loadSpecificExamSet(txtSelectCourse.getText().toString());
                        }
                        break;
                }

                courseSelectDialog.dismiss();
            }
        });
        courseSelectDialog.show();
    }

    // Load course list with (all) value
    private void loadCourseList(ListView listView) {
        databaseReference.child("courseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseListData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    courseListData.add(dataSnapshot.getValue(CourseDataModel.class));
                }
                dialogProgressBar.setVisibility(View.GONE);

                CustomAdapter courseListAdapter = new CustomAdapter(MainActivity.this, courseListData.size(), R.layout.list_item_course);
                courseListAdapter.setCourseList(courseListData);
                listView.setAdapter(courseListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load all exam
    private void loadExamSet() {
        databaseReference.child("examSet").addValueEventListener(new ValueEventListener() {
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

    // Load specific exam
    private void loadSpecificExamSet(String course) {
        databaseReference.child("examSet").orderByChild("course").equalTo(course).addValueEventListener(new ValueEventListener() {
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