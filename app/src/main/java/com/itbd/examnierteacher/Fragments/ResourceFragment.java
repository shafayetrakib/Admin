package com.itbd.examnierteacher.Fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.itbd.examnierteacher.DataMoldes.ResourceDataModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class ResourceFragment extends Fragment {

    private static final String U_NAME = "arg1";
    private static final String U_ID = "arg2";
    private static final String U_COURSE = "arg3";

    List<ResourceDataModel> courseResourceDataModelList = new ArrayList<>();
    List<ResourceDataModel> allCourseResourceDataModelList = new ArrayList<>();
    List<ResourceDataModel> resourceDataModelList = new ArrayList<>();
    String allCourse = "All";

    String userID, userName, userCourse;

    ProgressBar resProgressBar;
    ListView resList;

    public ResourceFragment() {
        // Required empty public constructor
    }

    public static ResourceFragment getInstance(String uId, String uName, String uCourse) {
        ResourceFragment resourceFragment = new ResourceFragment();

        Bundle bundle = new Bundle();

        bundle.putString(U_ID, uId);
        bundle.putString(U_NAME, uName);
        bundle.putString(U_COURSE, uCourse);

        resourceFragment.setArguments(bundle);

        return resourceFragment;
    }

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        EditText edtMsg = view.findViewById(R.id.edt_msg);
        ImageView imgBtnMsgSend = view.findViewById(R.id.img_btn_msg_send);
        resProgressBar = view.findViewById(R.id.res_progress_bar);

        if (getArguments() != null) {
            userID = getArguments().getString(U_ID);
            userName = getArguments().getString(U_NAME);
            userCourse = getArguments().getString(U_COURSE);
        }

        resList = view.findViewById(R.id.res_list);
        loadRes(resList, userCourse);

        imgBtnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = edtMsg.getText().toString().trim();
                String date = getPresentDate();
                String time = getPresentTime();

                if (message.isEmpty()) {
                    return;
                }

                String msgKey = mReference.push().getKey();
                assert msgKey != null;
                mReference.child("resource").child(msgKey).setValue(new ResourceDataModel(message, date, time,
                        userName, userCourse, msgKey));

                edtMsg.setText("");
            }
        });

        return view;
    }

    private String getPresentDate() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        return sDateFormat.format(new Date());
    }

    // Get User's Device Time
    private String getPresentTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sTimeFormat = new SimpleDateFormat("hh:mm a");
        sTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        return sTimeFormat.format(new Date());
    }

    private void loadRes(ListView listView, String specificCourse) {
        mReference = FirebaseDatabase.getInstance().getReference();

        mReference.child("resource")
                .orderByChild("course")
                .equalTo(allCourse)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allCourseResourceDataModelList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ResourceDataModel resourceDataModel = dataSnapshot.getValue(ResourceDataModel.class);

                            allCourseResourceDataModelList.add(resourceDataModel);
                        }
                        mergeList(listView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mReference.child("resource")
                .orderByChild("course")
                .equalTo(specificCourse)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseResourceDataModelList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ResourceDataModel resourceDataModel = dataSnapshot.getValue(ResourceDataModel.class);

                            courseResourceDataModelList.add(resourceDataModel);
                        }
                        mergeList(listView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mergeList(ListView listView) {
        resourceDataModelList.clear();
        resourceDataModelList.addAll(courseResourceDataModelList);
        resourceDataModelList.addAll(allCourseResourceDataModelList);

        // Comparing based on date and time
        resourceDataModelList.sort(new Comparator<ResourceDataModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(ResourceDataModel rDm1, ResourceDataModel rDm2) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                int[] timeOneInt = normalToInt(rDm1.getTime());
                int[] timeTwoInt = normalToInt(rDm2.getTime());

                LocalTime timeOne = LocalTime.parse(timeValidation(timeOneInt[0], timeOneInt[1]));
                LocalTime timeTwo = LocalTime.parse(timeValidation(timeTwoInt[0], timeTwoInt[1]));

                try {
                    Date dateOne = dateTimeFormat.parse(rDm1.getDate() + " " + timeOne);
                    Date dateTwo = dateTimeFormat.parse(rDm2.getDate() + " " + timeTwo);

                    assert dateOne != null;
                    return dateOne.compareTo(dateTwo);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        resList.setVisibility(View.VISIBLE);
        resProgressBar.setVisibility(View.GONE);

        BaseAdapter resListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return resourceDataModelList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @SuppressLint({"SetTextI18n", "ViewHolder"})
            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                view = getLayoutInflater().inflate(R.layout.list_item_resource, viewGroup, false);

                ResourceDataModel resourceDataModel = resourceDataModelList.get(i);

                TextView txtShowMsgDate = view.findViewById(R.id.txt_show_msg_date);
                TextView txtShowMsgTime = view.findViewById(R.id.txt_show_msg_time);
                TextView txtMsgBox = view.findViewById(R.id.txt_msg_box);
                TextView txtShowAdminName = view.findViewById(R.id.txt_show_admin_name);

                LinearLayout resItemParent = view.findViewById(R.id.res_item_parent);

                txtShowMsgDate.setText(resourceDataModel.getDate());
                txtShowMsgTime.setText(resourceDataModel.getTime());
                txtMsgBox.setText(resourceDataModel.getResource());
                txtShowAdminName.setText("by " + resourceDataModel.getUser());

                ImageView imgBtnMsgDlt = view.findViewById(R.id.img_btn_msg_dlt);

                resItemParent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        imgBtnMsgDlt.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
                imgBtnMsgDlt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String dltMsgKey = resourceDataModel.getKey();

                        mReference.child("resource").orderByChild("key")
                                .equalTo(dltMsgKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().removeValue();
                                        }
                                        Toast.makeText(requireActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                return view;
            }
        };

        listView.setAdapter(resListAdapter);
    }

    private int[] normalToInt(String time) {
        String[] newTime = time.split(" ");
        String[] hrMin = newTime[0].split(":");

        int hr, min;

        if (newTime[1].equals("PM") || newTime[1].equals("Pm") || newTime[1].equals("pm")) {
            hr = Integer.parseInt(hrMin[0]);
            if (hr < 12) {
                hr += 12;
            }
            min = Integer.parseInt(hrMin[1]);

        } else {
            hr = Integer.parseInt(hrMin[0]);
            if (hr == 12) {
                hr = 0;
            }
            min = Integer.parseInt(hrMin[1]);
        }

        return new int[]{hr, min};
    }

    private String timeValidation(int hr, int min) {
        String newHr = hr < 10 ? "0" + hr : String.valueOf(hr);
        String newMin = min < 10 ? "0" + min : String.valueOf(min);
        return newHr + ":" + newMin;
    }
}