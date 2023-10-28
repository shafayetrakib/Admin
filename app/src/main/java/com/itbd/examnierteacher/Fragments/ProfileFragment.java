package com.itbd.examnierteacher.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.itbd.examnierteacher.ChangePasswordActivity;
import com.itbd.examnierteacher.R;
import com.itbd.examnierteacher.SignInActivity;
import com.itbd.examnierteacher.DataMoldes.TeacherDataModel;
import com.itbd.examnierteacher.PersonalInfoActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private static final String PREF_NAME = "ExaminerTeacher";
    private static final String U_DATA = "arg1";
    TeacherDataModel teacherDataModelData;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment getInstance(TeacherDataModel uData) {
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(U_DATA, uData);

        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, viewGroup, false);

        if (getArguments() != null) {
            teacherDataModelData = (TeacherDataModel) getArguments().getSerializable(U_DATA);
        }

        ImageButton btnLogOut = view.findViewById(R.id.btn_logout);
        ImageButton btnPersonalInfo = view.findViewById(R.id.personalinfo);
        ImageButton btnChangePassword = view.findViewById(R.id.changepassword);

        TextView teacherName = view.findViewById(R.id.teacher_name);

        teacherName.setText(teacherDataModelData.getFullName());

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog logOutDialog = new Dialog(getActivity());
                logOutDialog.setContentView(R.layout.dialog_logout);
                Objects.requireNonNull(logOutDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                logOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                logOutDialog.getWindow().setGravity(Gravity.BOTTOM);

                AppCompatButton btnLogOutDialogOk = logOutDialog.findViewById(R.id.btn_logout_dialog_ok);

                logOutDialog.show();
                btnLogOutDialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();

                        logOutDialog.dismiss();

                        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("userCheck", false);
                        editor.putString("uID", "");
                        editor.apply();

                        startActivity(new Intent(requireActivity(), SignInActivity.class));
                        requireActivity().finish();
                    }
                });
            }
        });
        btnPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), PersonalInfoActivity.class).putExtra("uData", teacherDataModelData));
            }
        });
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
            }
        });

        return view;
    }
}