package com.itbd.examnierteacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.itbd.examnierteacher.DataMoldes.ExamDataModel;


import java.util.List;
import java.util.Objects;

public class CustomAdapter extends ArrayAdapter<ExamDataModel> {
    private String key;
    private Activity activity;
    private List<ExamDataModel> examDataList;

    public CustomAdapter(Activity activity, List<ExamDataModel> examDataList) {

        super(activity, R.layout.list_item_exam, examDataList);
        this.activity = activity;
        this.examDataList = examDataList;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.list_item_exam, viewGroup, false);

        ExamDataModel examDataModel = examDataList.get(position);

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
                Intent intent = new Intent(CustomAdapter.this.getContext(), ExamSetActivity.class);

                intent.putExtra("identifyIntent", 2);
                intent.putExtra("examData", examDataModel);

                getContext().startActivity(intent);
            }
        });
        ExamDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = examDataModel.getExamId();

                Dialog examDeleteDialog = new Dialog(getContext());
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
                        FirebaseDatabase.getInstance().getReference("examSet").child(key)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        examDeleteDialog.dismiss();
                                        Toast.makeText(getContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
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

        return view;
    }

}
