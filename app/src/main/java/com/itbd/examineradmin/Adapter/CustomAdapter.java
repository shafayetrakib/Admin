package com.itbd.examineradmin.Adapter;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itbd.examineradmin.DataMoldes.CourseDataModel;
import com.itbd.examineradmin.DataMoldes.DashButtonModel;
import com.itbd.examineradmin.DataMoldes.ExamDataModel;
import com.itbd.examineradmin.DataMoldes.ExamResultModel;
import com.itbd.examineradmin.DataMoldes.StudentDataModel;
import com.itbd.examineradmin.DataMoldes.TeacherDataModel;
import com.itbd.examineradmin.ExamSetActivity;
import com.itbd.examineradmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomAdapter extends BaseAdapter {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ProgressBar dialogProgressBar;

    private String examKey;
    private Context context;
    private int length, layout;
    private List<ExamDataModel> examDataModelList = new ArrayList<>();
    private List<DashButtonModel> dashButtonModelList = new ArrayList<>();
    private List<ExamResultModel> examResultModelList = new ArrayList<>();
    private List<TeacherDataModel> teacherDataModelList = new ArrayList<>();
    private List<StudentDataModel> studentDataModelList = new ArrayList<>();
    private List<CourseDataModel> courseList = new ArrayList<>();

    public void setCourseList(List<CourseDataModel> courseList) {
        this.courseList = courseList;
    }

    public void setStudentDataModelList(List<StudentDataModel> studentDataModelList) {
        this.studentDataModelList = studentDataModelList;
    }

    public void setExamDataModelList(List<ExamDataModel> examDataModelList) {
        this.examDataModelList = examDataModelList;
    }

    public void setDashButtonModelList(List<DashButtonModel> dashButtonModelList) {
        this.dashButtonModelList = dashButtonModelList;
    }

    public void setExamResultModelList(List<ExamResultModel> examResultModelList) {
        this.examResultModelList = examResultModelList;
    }

    public void setTeacherDataModelList(List<TeacherDataModel> teacherDataModelList) {
        this.teacherDataModelList = teacherDataModelList;
    }

    public CustomAdapter() {
    }

    public CustomAdapter(Context context, int length, int layout) {
        this.context = context;
        this.length = length;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(layout, viewGroup, false);
        }

        if (layout == R.layout.list_item_exam) {
            ExamDataModel examDataModel = examDataModelList.get(i);

            TextView itemOne = view.findViewById(R.id.item_one);
            TextView itemTwo = view.findViewById(R.id.item_two);
            TextView itemThree = view.findViewById(R.id.item_three);
            TextView itemFour = view.findViewById(R.id.item_four);
            TextView itemFive = view.findViewById(R.id.item_five);

            ImageButton ExamDelete = view.findViewById(R.id.delete);
            ImageButton ExamEdit = view.findViewById(R.id.edit);

            itemOne.setText(examDataModel.getExamName());
            itemTwo.setText("Date : " + examDataModel.getExamDate());
            itemThree.setText("Time : " + examDataModel.getExamTime());
            itemFour.setText("Mark : " + examDataModel.getTotalMarks());
            itemFive.setText("Duration : " + examDataModel.getDuration() + " Minutes");
            ExamEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ExamSetActivity.class);

                    intent.putExtra("identifyIntent", 2);
                    intent.putExtra("examData", examDataModel);

                    context.startActivity(intent);
                }
            });
            ExamDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    examKey = examDataModel.getExamId();

                    Dialog examDeleteDialog = new Dialog(context);
                    examDeleteDialog.setContentView(R.layout.dialog_delete_notify);
                    Objects.requireNonNull(examDeleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    examDeleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    examDeleteDialog.getWindow().setGravity(Gravity.BOTTOM);

                    AppCompatButton btnDelete = examDeleteDialog.findViewById(R.id.btn_dlt_dialog_delete);
                    AppCompatButton btnCancel = examDeleteDialog.findViewById(R.id.btn_dlt_dialog_cancel);

                    examDeleteDialog.show();

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference("examSet").child(examKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    examDeleteDialog.dismiss();
                                    Toast.makeText(context, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            examDeleteDialog.dismiss();
                        }
                    });
                }
            });
        }

        if (layout == R.layout.list_item_btns) {
            DashButtonModel dashButtonModel = dashButtonModelList.get(i);

            TextView txtBtnText = view.findViewById(R.id.txt_btn_text);
            ImageView imgBtnIcon = view.findViewById(R.id.img_btn_icon);

            imgBtnIcon.setImageResource(dashButtonModel.getBtnIcon());
            txtBtnText.setText(dashButtonModel.getBtnTxt());
        }

        if (layout == R.layout.list_item_result) {
            ExamResultModel newERM = examResultModelList.get(i);

            TextView stName = view.findViewById(R.id.txt_result_st_name);
            TextView stTotalMarks = view.findViewById(R.id.txt_result_total_marks);
            TextView stObtainMarks = view.findViewById(R.id.txt_result_obtain_marks);
            TextView resultPassFail = view.findViewById(R.id.txt_result_pass_fail);

            stName.setText(newERM.getUserName());
            stTotalMarks.setText(newERM.getExamTotalMarks());
            stObtainMarks.setText(newERM.getExamResult());

            boolean isPass;
            isPass = Integer.parseInt(newERM.getExamResult()) >= Integer.parseInt(newERM.getExamTotalMarks()) * 0.7;

            if (isPass) {
                resultPassFail.setText("Pass");
            } else {
                resultPassFail.setText("Fail");
                resultPassFail.setTextColor(context.getColor(R.color.red_pr));
            }
        }

        if (layout == R.layout.item_list_teacher) {
            TeacherDataModel teacherDataModel = teacherDataModelList.get(i);

            TextView txtTeacherName = view.findViewById(R.id.txt_teacher_name);
            TextView txtTeacherPosition = view.findViewById(R.id.txt_teacher_position);
            TextView txtTeacherPhone = view.findViewById(R.id.txt_teacher_phone);
            TextView txtTeacherEmail = view.findViewById(R.id.txt_teacher_email);
            TextView txtTeacherCourse = view.findViewById(R.id.txt_teacher_course);

            ImageView imgBtnCallTeacher = view.findViewById(R.id.img_btn_call_teacher);
            ImageView imgBtnEmailTeacher = view.findViewById(R.id.img_btn_email_teacher);
            ImageView imgBtnCourseTeacher = view.findViewById(R.id.img_btn_course_teacher);

            txtTeacherName.setText(i + 1 + ". " + teacherDataModel.getFullName());
            txtTeacherPosition.setText(teacherDataModel.getPosition());
            txtTeacherPhone.setText("Phone : " + teacherDataModel.getPhone());
            txtTeacherEmail.setText("Email : " + teacherDataModel.getEmail());
            txtTeacherCourse.setText("Course : " + teacherDataModel.getCourse());

            imgBtnCallTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + teacherDataModel.getPhone())));
                }
            });

            imgBtnEmailTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openMail = new Intent(Intent.ACTION_SENDTO);
                    openMail.setData(Uri.parse("mailto:"));

                    openMail.putExtra(Intent.EXTRA_EMAIL, new String[]{teacherDataModel.getEmail()});

                    context.startActivity(Intent.createChooser(openMail, "Send Email"));
                }
            });

            imgBtnCourseTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog courseSelectDialog = new BottomSheetDialog(context, R.style.bottom_sheet_dialog);
                    Objects.requireNonNull(courseSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    courseSelectDialog.getBehavior().setSkipCollapsed(true);
                    courseSelectDialog.getBehavior().setState(STATE_EXPANDED);
                    courseSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

                    dialogProgressBar = courseSelectDialog.findViewById(R.id.progress_bar);
                    ListView listCourse = courseSelectDialog.findViewById(R.id.course_list);
                    loadCourseList(listCourse);

                    assert listCourse != null;
                    listCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            databaseReference
                                    .child("Teacher")
                                    .child(teacherDataModel.getuId())
                                    .child("course")
                                    .setValue(courseList.get(i).getCourseName())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                            courseSelectDialog.dismiss();
                                        }
                                    });
                        }
                    });

                    courseSelectDialog.show();
                }
            });
        }

        if (layout == R.layout.item_list_student) {
            StudentDataModel studentDataModel = studentDataModelList.get(i);

            TextView txtStudentName = view.findViewById(R.id.txt_student_name);
            TextView txtStudentPhone = view.findViewById(R.id.txt_student_phone);
            TextView txtStudentGrdPhone = view.findViewById(R.id.txt_student_grd_phone);
            TextView txtStudentEmail = view.findViewById(R.id.txt_student_email);
            TextView txtStudentCourse = view.findViewById(R.id.txt_student_course);

            ImageView imgBtnCallStudent = view.findViewById(R.id.img_btn_call_student);
            ImageView imgBtnCallGrdStudent = view.findViewById(R.id.img_btn_call_student_grd);
            ImageView imgBtnEmailStudent = view.findViewById(R.id.img_btn_email_student);
            ImageView imgBtnCourseStudent = view.findViewById(R.id.img_btn_course_student);

            txtStudentName.setText(i + 1 + ". " + studentDataModel.getName());
            txtStudentPhone.setText("Phone : " + studentDataModel.getPhone());
            txtStudentGrdPhone.setText("Guardian Phone : " + studentDataModel.getGuardianPhone());
            txtStudentEmail.setText("Email : " + studentDataModel.getEmail());
            txtStudentCourse.setText("Course : " + studentDataModel.getCourse());

            imgBtnCallStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + studentDataModel.getPhone())));
                }
            });

            imgBtnCallGrdStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + studentDataModel.getGuardianPhone())));
                }
            });

            imgBtnEmailStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openMail = new Intent(Intent.ACTION_SENDTO);
                    openMail.setData(Uri.parse("mailto:"));

                    openMail.putExtra(Intent.EXTRA_EMAIL, new String[]{studentDataModel.getEmail()});

                    context.startActivity(Intent.createChooser(openMail, "Send Email"));
                }
            });

            imgBtnCourseStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomSheetDialog courseSelectDialog = new BottomSheetDialog(context, R.style.bottom_sheet_dialog);
                    Objects.requireNonNull(courseSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    courseSelectDialog.getBehavior().setSkipCollapsed(true);
                    courseSelectDialog.getBehavior().setState(STATE_EXPANDED);
                    courseSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

                    dialogProgressBar = courseSelectDialog.findViewById(R.id.progress_bar);
                    ListView listCourse = courseSelectDialog.findViewById(R.id.course_list);
                    loadCourseList(listCourse);

                    assert listCourse != null;
                    listCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            databaseReference
                                    .child("student")
                                    .child(studentDataModel.getUserId())
                                    .child("course")
                                    .setValue(courseList.get(i).getCourseName())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                            courseSelectDialog.dismiss();
                                        }
                                    });
                        }
                    });

                    courseSelectDialog.show();
                }
            });
        }

        if (layout == R.layout.list_item_course) {
            CourseDataModel courseDataModel = courseList.get(i);

            TextView txtListItem = view.findViewById(R.id.txt_list_item);

            txtListItem.setText(courseDataModel.getCourseName());
        }

        if (layout == R.layout.item_list_course_delete) {
            CourseDataModel courseDataModel = courseList.get(i);

            TextView txtCourseName = view.findViewById(R.id.txt_course);
            ImageView imgBtnCourseDelete = view.findViewById(R.id.img_btn_delete_course);

            txtCourseName.setText(i + 1 + ". " + courseDataModel.getCourseName());
            imgBtnCourseDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog courseDeleteDialog = new Dialog(context);
                    courseDeleteDialog.setContentView(R.layout.dialog_delete_notify);
                    Objects.requireNonNull(courseDeleteDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    courseDeleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    courseDeleteDialog.getWindow().setGravity(Gravity.BOTTOM);

                    AppCompatButton btnDelete = courseDeleteDialog.findViewById(R.id.btn_dlt_dialog_delete);
                    AppCompatButton btnCancel = courseDeleteDialog.findViewById(R.id.btn_dlt_dialog_cancel);

                    courseDeleteDialog.show();

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            databaseReference
                                    .child("courseList")
                                    .child(courseDataModel.getCourseId())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            courseDeleteDialog.dismiss();
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            courseDeleteDialog.dismiss();
                        }
                    });
                }
            });
        }

        return view;
    }

    private void loadCourseList(ListView listView) {
        databaseReference
                .child("courseList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            courseList.add(dataSnapshot.getValue(CourseDataModel.class));
                        }

                        courseList.removeIf(courseDataModel -> courseDataModel.getCourseName().equals("All"));

                        dialogProgressBar.setVisibility(View.GONE);
                        listView.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return courseList.size();
                            }

                            @Override
                            public Object getItem(int i) {
                                return null;
                            }

                            @Override
                            public long getItemId(int i) {
                                return 0;
                            }

                            @Override
                            public View getView(int i, View view, ViewGroup viewGroup) {
                                if (view == null) {
                                    view = LayoutInflater.from(context).inflate(R.layout.list_item_course, viewGroup, false);
                                }
                                CourseDataModel courseDataModel = courseList.get(i);

                                TextView txtListItem = view.findViewById(R.id.txt_list_item);

                                txtListItem.setText(courseDataModel.getCourseName());
                                return view;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
