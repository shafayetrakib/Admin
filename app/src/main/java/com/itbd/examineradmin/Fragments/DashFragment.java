package com.itbd.examineradmin.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.itbd.examineradmin.Adapter.CustomAdapter;
import com.itbd.examineradmin.DataMoldes.AdminDataModel;
import com.itbd.examineradmin.DataMoldes.DashButtonModel;

import com.itbd.examineradmin.DataMoldes.AdminDataModel;
import com.itbd.examineradmin.ExamSetActivity;
import com.itbd.examineradmin.MainActivity;
import com.itbd.examineradmin.R;

import java.util.ArrayList;
import java.util.List;


public class DashFragment extends Fragment {
    private static final String U_DATA = "arg1";
    TextView txtUserName, btnExamCreate;
    GridView gridViewBtnList;
    AdminDataModel adminDataModelData;

    List<DashButtonModel> dashButtonModelList = new ArrayList<>();

    public DashFragment() {

    }

    public static DashFragment getInstance(AdminDataModel userData) {
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

        if (getArguments() != null) {
            adminDataModelData = (AdminDataModel) getArguments().getSerializable(U_DATA);
        }

        txtUserName = view.findViewById(R.id.txt_user_name);
        btnExamCreate = view.findViewById(R.id.exam_creatfirst);
        gridViewBtnList = view.findViewById(R.id.grid_view_btn_list);

        txtUserName.setText(adminDataModelData.getName());

        btnExamCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goExamSet = new Intent(requireActivity(), ExamSetActivity.class);
                goExamSet.putExtra("identifyIntent", 1);
                startActivity(goExamSet);
            }
        });

        dashButtonModelList.add(new DashButtonModel("Teachers", R.drawable.ic_teacher));
        dashButtonModelList.add(new DashButtonModel("Students", R.drawable.ic_student));
        dashButtonModelList.add(new DashButtonModel("Exams", R.drawable.ic_exam));
        dashButtonModelList.add(new DashButtonModel("Courses", R.drawable.ic_course));

        CustomAdapter gridBtnAdapter = new CustomAdapter(requireContext(), dashButtonModelList.size(), R.layout.list_item_btns);
        gridBtnAdapter.setDashButtonModelList(dashButtonModelList);

        gridViewBtnList.setAdapter(gridBtnAdapter);

        gridViewBtnList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(requireActivity(), MainActivity.class).putExtra("identifyIntent", i));
            }
        });

        return view;
    }
}