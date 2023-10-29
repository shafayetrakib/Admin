package com.itbd.examineradmin.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.itbd.examineradmin.DataMoldes.DashButtonModel;
import com.itbd.examineradmin.DataMoldes.ExamDataModel;
import com.itbd.examineradmin.DataMoldes.ExamResultModel;
import com.itbd.examineradmin.DataMoldes.TeacherDataModel;
import com.itbd.examineradmin.ExamSetActivity;
import com.itbd.examineradmin.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomAdapter extends BaseAdapter {

    private String examKey;
    private Context context;
    private int length, layout;
    private List<ExamDataModel> examDataModelList = new ArrayList<>();
    private List<DashButtonModel> dashButtonModelList = new ArrayList<>();
    private List<ExamResultModel> examResultModelList = new ArrayList<>();
    private List<TeacherDataModel> teacherDataModelList = new ArrayList<>();

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

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
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
                            FirebaseDatabase.getInstance().getReference("examSet").child(examKey)
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                resultPassFail.setTextColor(R.color.red_pr);
            }
        }

        if (layout == R.layout.item_list_teacher) {
            TeacherDataModel teacherDataModel = teacherDataModelList.get(i);

            TextView txtTeacherName = view.findViewById(R.id.txt_teacher_name);
            TextView txtTeacherPosition = view.findViewById(R.id.txt_teacher_position);
            TextView txtTeacherPhone = view.findViewById(R.id.txt_teacher_phone);
            TextView txtTeacherEmail = view.findViewById(R.id.txt_teacher_email);
            TextView txtTeacherCourse = view.findViewById(R.id.txt_teacher_course);

            txtTeacherName.setText(teacherDataModel.getFullName());
            txtTeacherPosition.setText(teacherDataModel.getPosition());
            txtTeacherPhone.setText("Phone : " + teacherDataModel.getPhone());
            txtTeacherEmail.setText("Email : " + teacherDataModel.getEmail());
            txtTeacherCourse.setText("Course : " + teacherDataModel.getCourse());
        }

        return view;
    }
}
