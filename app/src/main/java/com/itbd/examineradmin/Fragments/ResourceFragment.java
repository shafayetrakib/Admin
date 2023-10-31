package com.itbd.examineradmin.Fragments;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.itbd.examineradmin.Adapter.CustomAdapter;
import com.itbd.examineradmin.DataMoldes.CourseDataModel;
import com.itbd.examineradmin.DataMoldes.ResourceDataModel;
import com.itbd.examineradmin.MainActivity;
import com.itbd.examineradmin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;


public class ResourceFragment extends Fragment {

    private static final String U_NAME = "arg1";
    private static final String U_ID = "arg2";
    List<ResourceDataModel> resourceDataModelList = new ArrayList<>();
    List<CourseDataModel> courseListData = new ArrayList<>();

    String userID, userName, resCourse = "";

    ProgressBar resProgressBar, dialogProgressBar;
    ListView resList, listCourse;
    EditText edtMsg;

    public ResourceFragment() {
        // Required empty public constructor
    }

    public static ResourceFragment getInstance(String uId, String uName) {
        ResourceFragment resourceFragment = new ResourceFragment();

        Bundle bundle = new Bundle();

        bundle.putString(U_ID, uId);
        bundle.putString(U_NAME, uName);

        resourceFragment.setArguments(bundle);

        return resourceFragment;
    }

    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);

        edtMsg = view.findViewById(R.id.edt_msg);

        ImageView imgBtnMsgSend = view.findViewById(R.id.img_btn_msg_send);
        ImageView imgBtnSelectCourse = view.findViewById(R.id.img_btn_select_course);

        resProgressBar = view.findViewById(R.id.res_progress_bar);

        if (getArguments() != null) {
            userID = getArguments().getString(U_ID);
            userName = getArguments().getString(U_NAME);
        }

        resList = view.findViewById(R.id.res_list);
        loadRes(resList);

        imgBtnSelectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCourseDialog();
            }
        });

        imgBtnMsgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMsg.getText().toString().trim();
                String date = getPresentDate();
                String time = getPresentTime();

                if (resCourse.isEmpty()) {
                    Toast.makeText(requireActivity(), "Please select course", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (message.isEmpty()) {
                    return;
                }

                String msgKey = mReference.push().getKey();
                assert msgKey != null;
                mReference.child("resource").child(msgKey).setValue(new ResourceDataModel(message, date, time,
                        userName, resCourse, msgKey));

                edtMsg.setText("");
            }
        });

        return view;
    }

    // Showing bottom dialog of course list
    private void showCourseDialog() {
        BottomSheetDialog courseSelectDialog = new BottomSheetDialog(requireActivity(), R.style.bottom_sheet_dialog);
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
                edtMsg.setHint(courseListData.get(i).getCourseName());
                resCourse = courseListData.get(i).getCourseName();

                courseSelectDialog.dismiss();
            }
        });
        courseSelectDialog.show();
    }

    // Load course list with (all) value
    private void loadCourseList(ListView listView) {
        mReference
                .child("courseList")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courseListData.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            courseListData.add(dataSnapshot.getValue(CourseDataModel.class));
                        }
                        dialogProgressBar.setVisibility(View.GONE);

                        CustomAdapter courseListAdapter = new CustomAdapter(requireActivity(), courseListData.size(), R.layout.list_item_course);
                        courseListAdapter.setCourseList(courseListData);
                        listView.setAdapter(courseListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void loadRes(ListView listView) {
        mReference = FirebaseDatabase.getInstance().getReference();

        mReference.child("resource")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resourceDataModelList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ResourceDataModel resourceDataModel = dataSnapshot.getValue(ResourceDataModel.class);

                            resourceDataModelList.add(resourceDataModel);
                        }
                        showDataToListView(listView);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDataToListView(ListView listView) {
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