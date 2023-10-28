package com.itbd.examnierteacher.Fragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import static java.util.Arrays.stream;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
import com.itbd.examnierteacher.R;
import com.itbd.examnierteacher.DataMoldes.ExamResultModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ResultFragment extends Fragment {

    LinearLayout progressLayout, listHeader;
    TextView txtSelectCourse, txtSelectExam, txtCourseExamIns;
    ListView courseList, examList, resultList;
    ProgressBar progressBar;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    List<String> courseListData = new ArrayList<>();
    List<String> examListData = new ArrayList<>();
    List<ExamResultModel> examResultModelList = new ArrayList<>();
    public ResultFragment() {
        // Required empty public constructor
    }
    public static ResultFragment newInstance() {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        txtCourseExamIns = view.findViewById(R.id.txt_course_exam_ins);
        txtSelectCourse = view.findViewById(R.id.txt_select_course_result);
        txtSelectExam = view.findViewById(R.id.txt_select_exam_result);
        progressLayout = view.findViewById(R.id.progress_layout);
        listHeader = view.findViewById(R.id.list_header);
        resultList = view.findViewById(R.id.lst_result_show);

        txtSelectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDialog();
            }
        });

        txtSelectExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExamDialog();
            }
        });


        return view;
    }
    private void showCourseDialog() {
        BottomSheetDialog courseSelectDialog = new BottomSheetDialog(requireActivity(), R.style.bottom_sheet_dialog);
        Objects.requireNonNull(courseSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        courseSelectDialog.getBehavior().setSkipCollapsed(true);
        courseSelectDialog.getBehavior().setState(STATE_EXPANDED);
        courseSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

        progressBar = courseSelectDialog.findViewById(R.id.progress_bar);
        courseList = courseSelectDialog.findViewById(R.id.course_list);
        loadCourseList(courseList);

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtSelectCourse.setText(courseListData.get(i));

                courseSelectDialog.dismiss();
            }
        });
        courseSelectDialog.show();
    }
    private void loadCourseList(ListView listView){
        mReference.child("courseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseListData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    courseListData.add(dataSnapshot.getValue(String.class));
                }
                courseListData.remove("All");
                progressBar.setVisibility(View.GONE);

                listView.setAdapter(new ArrayAdapter<>(requireActivity(),
                        R.layout.list_item_course,
                        R.id.txt_list_item, courseListData));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showExamDialog() {
        BottomSheetDialog examSelectDialog = new BottomSheetDialog(requireActivity(), R.style.bottom_sheet_dialog);
        Objects.requireNonNull(examSelectDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        examSelectDialog.getBehavior().setSkipCollapsed(true);
        examSelectDialog.getBehavior().setState(STATE_EXPANDED);
        examSelectDialog.setContentView(R.layout.bottom_dialog_course_select);

        TextView courseDialogTitle = examSelectDialog.findViewById(R.id.course_dialog_title);
        progressBar = examSelectDialog.findViewById(R.id.progress_bar);
        assert courseDialogTitle != null;
        courseDialogTitle.setText("Select Exam name");

        examList = examSelectDialog.findViewById(R.id.course_list);
        loadExam(examList);

        examList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtSelectExam.setText(examListData.get(i));
                txtCourseExamIns.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);

                loadResult(examListData.get(i));

                examSelectDialog.dismiss();
            }
        });

        examSelectDialog.show();
    }

    private void loadExam(ListView listView) {
        String course = txtSelectCourse.getText().toString().trim();
        mReference.child("result").orderByChild("userCourse").equalTo(course).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                examListData.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ExamResultModel examResultModel = dataSnapshot.getValue(ExamResultModel.class);
                    assert examResultModel != null;
                    examListData.add(examResultModel.getExamName());
                }
                examListData = examListData.stream().distinct().collect(Collectors.toList());
                progressBar.setVisibility(View.GONE);

                listView.setAdapter(new ArrayAdapter<>(requireActivity(),
                        R.layout.list_item_course,
                        R.id.txt_list_item, examListData));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadResult(String examName){
        mReference.child("result")
                .orderByChild("examName")
                .equalTo(examName)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                examResultModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ExamResultModel eRm = dataSnapshot.getValue(ExamResultModel.class);
                    examResultModelList.add(eRm);
                }
                progressLayout.setVisibility(View.GONE);
                resultList.setVisibility(View.VISIBLE);
                listHeader.setVisibility(View.VISIBLE);

                resultList.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return examResultModelList.size();
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
                        if (view == null){
                            view = getLayoutInflater().inflate(R.layout.list_item_result, viewGroup, false);
                        }

                        TextView stName = view.findViewById(R.id.txt_result_st_name);
                        TextView stTotalMarks = view.findViewById(R.id.txt_result_total_marks);
                        TextView stObtainMarks = view.findViewById(R.id.txt_result_obtain_marks);
                        TextView resultPassFail = view.findViewById(R.id.txt_result_pass_fail);

                        ExamResultModel newERM = examResultModelList.get(i);

                        stName.setText(newERM.getUserName());
                        stTotalMarks.setText(newERM.getExamTotalMarks());
                        stObtainMarks.setText(newERM.getExamResult());

                        boolean isPass;
                        if (Integer.parseInt(newERM.getExamResult()) >= Integer.parseInt(newERM.getExamTotalMarks()) * 0.7){
                            isPass = true;
                        } else {
                            isPass = false;
                        }

                        if (isPass){
                            resultPassFail.setText("Pass");
                        }else {
                            resultPassFail.setText("Fail");
                            resultPassFail.setTextColor(getResources().getColor(R.color.red_pr));
                        }

                        return view;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}