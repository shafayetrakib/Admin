package com.itbd.examnierteacher;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itbd.examnierteacher.DataMoldes.ExamDataModel;
import com.itbd.examnierteacher.DataMoldes.QuestionModel;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExamSetActivity extends AppCompatActivity {
    Button btnAddQuestion, btnExamSave;
    TextView examSetUpDate, examSetUpTime;
    EditText examSetUpName, examSetUpSyllabus, examSetUpMark, examSetUpDuration;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    DatabaseReference mReference;
    String userCourse, examName, examSyllabus, examTime, totalMarks, examDate, duration;
    boolean isQuestionAdded = false, isDataValidate = false;
    List<QuestionModel> questionModelList = new ArrayList<>();
    ExamDataModel examDataModel;
    BaseAdapter listShowQuestionAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examset);
        getWindow().setStatusBarColor(ContextCompat.getColor(ExamSetActivity.this, R.color.blue_pr));

        mReference = FirebaseDatabase.getInstance().getReference();

        int identifyIntent = getIntent().getIntExtra("identifyIntent", -1);

        btnExamSave = findViewById(R.id.btn_save_exam);
        btnAddQuestion = findViewById(R.id.btn_addquestion);

        examSetUpName = findViewById(R.id.edt_exam_name);
        examSetUpSyllabus = findViewById(R.id.edt_exam_syllabus);
        examSetUpDate = findViewById(R.id.edt_exam_date);
        examSetUpTime = findViewById(R.id.edt_exam_time);
        examSetUpMark = findViewById(R.id.edt_exam_mark);
        examSetUpDuration = findViewById(R.id.edt_exam_duration);

        if (identifyIntent == 1) {
            // identifyIntent = 1, ExamSetActivity Open from create exam button
            userCourse = getIntent().getStringExtra("userCourse");
        } else if (identifyIntent == 2) {
            // identifyIntent = 2, ExamSetActivity Open from edit exam button
            examDataModel = (ExamDataModel) getIntent().getSerializableExtra("examData");

            assert examDataModel != null;
            userCourse = examDataModel.getCourse();

            examSetUpName.setText(examDataModel.getExamName());
            examSetUpSyllabus.setText(examDataModel.getExamSyllabus());
            examSetUpDate.setText(examDataModel.getExamDate());
            examSetUpTime.setText(examDataModel.getExamTime());
            examSetUpMark.setText(examDataModel.getTotalMarks());
            examSetUpDuration.setText(examDataModel.getDuration());

            questionModelList = examDataModel.getQuestionModelList();

            btnAddQuestion.setText("Show Question");
            btnExamSave.setText("Update");
        } else if (identifyIntent == 3) {
            // identifyIntent = 3, ExamSetActivity Open from save question button
            isQuestionAdded = getIntent().getBooleanExtra("isQuestionAdded", false);
            examDataModel = (ExamDataModel) getIntent().getSerializableExtra("examData");

            assert examDataModel != null;
            examSetUpName.setText(examDataModel.getExamName());
            examSetUpSyllabus.setText(examDataModel.getExamSyllabus());
            examSetUpDate.setText(examDataModel.getExamDate());
            examSetUpTime.setText(examDataModel.getExamTime());
            examSetUpMark.setText(examDataModel.getTotalMarks());
            examSetUpDuration.setText(examDataModel.getDuration());

            questionModelList = examDataModel.getQuestionModelList();
            userCourse = examDataModel.getCourse();

            btnAddQuestion.setText("Show Question");
        }

        //Date Picker for date select
        examSetUpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = new DatePicker(ExamSetActivity.this);
                int currentDay = datePicker.getDayOfMonth();
                int currentMonth = (datePicker.getMonth());
                int currentYear = datePicker.getYear();

                datePickerDialog = new DatePickerDialog(ExamSetActivity.this, R.style.custom_date_time_picker_style,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                examSetUpDate.setText(i2 + "/" + (i1 + 1) + "/" + i);

                            }
                        }, currentYear, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        // timepicker for time select
        examSetUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePicker timepicker = new TimePicker(ExamSetActivity.this);
                int currentHour = timepicker.getCurrentHour();
                int currentMinute = timepicker.getCurrentMinute();

                timePickerDialog = new TimePickerDialog(ExamSetActivity.this, R.style.custom_date_time_picker_style,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hours, int minute) {
                                Time time = new Time(hours, minute, 0);

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");

                                String s = simpleDateFormat.format(time);
                                examSetUpTime.setText(s);
                            }
                        }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (identifyIntent == 1) {
                    examDataValidator();
                    if (isDataValidate) {
                        Intent goQuestionActivity = new Intent(ExamSetActivity.this, QuestionSetActivity.class);

                        ExamDataModel newEdm = new ExamDataModel();
                        newEdm.setCourse(userCourse);
                        newEdm.setExamName(examName);
                        newEdm.setExamSyllabus(examSyllabus);
                        newEdm.setExamDate(examDate);
                        newEdm.setExamTime(examTime);
                        newEdm.setTotalMarks(totalMarks);
                        newEdm.setDuration(totalMarks);

                        goQuestionActivity.putExtra("examData", newEdm);

                        startActivity(goQuestionActivity);
                        finish();
                    }
                } else if (identifyIntent == 2 || identifyIntent == 3) {
                    BottomSheetDialog showQuestionDialog = new BottomSheetDialog(ExamSetActivity.this, R.style.bottom_sheet_dialog);
                    Objects.requireNonNull(showQuestionDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    showQuestionDialog.getBehavior().setSkipCollapsed(true);
                    showQuestionDialog.getBehavior().setState(STATE_EXPANDED);

                    showQuestionDialog.setCancelable(false);
                    showQuestionDialog.setCanceledOnTouchOutside(true);
                    showQuestionDialog.setContentView(R.layout.bottom_dialog_show_question);

                    ImageView imgBtnDialogAddQuestion = showQuestionDialog.findViewById(R.id.img_btn_dialog_add_question);
                    ListView listShowQuestion = showQuestionDialog.findViewById(R.id.list_dialog_show_question);
                    listShowQuestionAdapter = new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return questionModelList.size();
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
                                view = getLayoutInflater().inflate(R.layout.list_item_show_question, viewGroup, false);
                            }

                            TextView txtQuestionTitle = view.findViewById(R.id.txt_question_title);
                            TextView txtQuestionMark = view.findViewById(R.id.txt_question_mark);
                            TextView txtQuestionOptionOne = view.findViewById(R.id.txt_question_option_one);
                            TextView txtQuestionOptionTwo = view.findViewById(R.id.txt_question_option_two);
                            TextView txtQuestionOptionThree = view.findViewById(R.id.txt_question_option_three);
                            TextView txtQuestionOptionFour = view.findViewById(R.id.txt_question_option_four);
                            TextView txtQuestionOptionCorrect = view.findViewById(R.id.txt_question_option_correct);

                            ImageView imgBtnEditQuestion = view.findViewById(R.id.img_btn_edit_question);

                            QuestionModel questionModel = questionModelList.get(i);

                            txtQuestionTitle.setText((i + 1) + ". " + questionModel.getQuestion());
                            txtQuestionMark.setText(String.valueOf(questionModel.getQuestionMark()));
                            txtQuestionOptionOne.setText(questionModel.getOptionOne());
                            txtQuestionOptionTwo.setText(questionModel.getOptionTwo());
                            txtQuestionOptionThree.setText(questionModel.getOptionThree());
                            txtQuestionOptionFour.setText(questionModel.getOptionFour());
                            txtQuestionOptionCorrect.setText(questionModel.getCorrectOption());

                            imgBtnEditQuestion.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    editQuestionDialog(questionModel, i);
                                }
                            });

                            return view;
                        }
                    };
                    assert listShowQuestion != null;
                    listShowQuestion.setAdapter(listShowQuestionAdapter);

                    imgBtnDialogAddQuestion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addQuestionDialog();
                        }
                    });

                    showQuestionDialog.show();
                }
            }
        });

        // Button for saving and updating exam data
        btnExamSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                examDataValidator();
                if (identifyIntent == 1) {
                    if (isDataValidate) {
                        if (!isQuestionAdded) {
                            Toast.makeText(ExamSetActivity.this, "Please Add Question", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (identifyIntent == 2) {
                    DatabaseReference examUpdate = mReference.child("examSet").child(examDataModel.getExamId());
                    if (isDataValidate) {
                        if (!examName.equals(examDataModel.getExamName())) {
                            examUpdate.child("examName").setValue(examName);
                        }
                        if (!examSyllabus.equals(examDataModel.getExamSyllabus())) {
                            examUpdate.child("examSyllabus").setValue(examSyllabus);
                        }
                        if (!examDate.equals(examDataModel.getExamDate())) {
                            examUpdate.child("examDate").setValue(examDate);
                        }
                        if (!examTime.equals(examDataModel.getExamTime())) {
                            examUpdate.child("examTime").setValue(examTime);
                        }
                        if (!totalMarks.equals(examDataModel.getTotalMarks())) {
                            examUpdate.child("totalMarks").setValue(totalMarks);
                        }
                        if (!duration.equals(examDataModel.getDuration())) {
                            examUpdate.child("duration").setValue(duration);
                        }
                        mReference.child("examSet")
                                .child(examDataModel.getExamId())
                                .child("questionModelList")
                                .setValue(questionModelList);

                        Toast.makeText(ExamSetActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                } else if (identifyIntent == 3) {
                    String key = mReference.push().getKey();

                    examDataModel.setCourse(userCourse);

                    examDataModel.setExamId(key);

                    assert key != null;
                    mReference.child("examSet").child(key).setValue(examDataModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ExamSetActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            });
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void addQuestionDialog() {
        Dialog addQuestionDialog = new Dialog(ExamSetActivity.this);
        addQuestionDialog.setContentView(R.layout.dialog_question_edit_add);

        Objects.requireNonNull(addQuestionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        addQuestionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addQuestionDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txtQuestionDialogTitle = addQuestionDialog.findViewById(R.id.txt_question_dialog_title);

        EditText edtDialogQuestion = addQuestionDialog.findViewById(R.id.edt_dialog_question);

        EditText edtDialogOptionOne = addQuestionDialog.findViewById(R.id.edt_dialog_option_one);
        EditText edtDialogOptionTwo = addQuestionDialog.findViewById(R.id.edt_dialog_option_two);
        EditText edtDialogOptionThree = addQuestionDialog.findViewById(R.id.edt_dialog_option_three);
        EditText edtDialogOptionFour = addQuestionDialog.findViewById(R.id.edt_dialog_option_four);

        RadioGroup radioDialogAndGrp = addQuestionDialog.findViewById(R.id.radio_dialog_ans_grp);
        RadioGroup radioDialogMarkGrp = addQuestionDialog.findViewById(R.id.radio_dialog_mark_grp);

        RadioButton dialogCheckboxOne = addQuestionDialog.findViewById(R.id.dialog_checkbox_one);
        RadioButton dialogCheckboxTwo = addQuestionDialog.findViewById(R.id.dialog_checkbox_two);
        RadioButton dialogCheckboxThree = addQuestionDialog.findViewById(R.id.dialog_checkbox_three);
        RadioButton dialogCheckboxFour = addQuestionDialog.findViewById(R.id.dialog_checkbox_four);

        RadioButton dialogMarkOne = addQuestionDialog.findViewById(R.id.q_dialog_mark_one);
        RadioButton dialogMarkTwo = addQuestionDialog.findViewById(R.id.q_dialog_mark_two);
        RadioButton dialogMarkThree = addQuestionDialog.findViewById(R.id.q_dialog_mark_three);

        AppCompatButton btnDialogSaveQuestion = addQuestionDialog.findViewById(R.id.btn_dialog_save_question);

        txtQuestionDialogTitle.setText("Add Question");

        btnDialogSaveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correctAns = "";
                int questionMark = 0;

                String question = edtDialogQuestion.getText().toString().trim();
                String optionOne = edtDialogOptionOne.getText().toString().trim();
                String optionTwo = edtDialogOptionTwo.getText().toString().trim();
                String optionThree = edtDialogOptionThree.getText().toString().trim();
                String optionFour = edtDialogOptionFour.getText().toString().trim();

                int radioCheckId = radioDialogAndGrp.getCheckedRadioButtonId();
                int radioMarkId = radioDialogMarkGrp.getCheckedRadioButtonId();

                if (question.isEmpty()) {
                    edtValidator(edtDialogQuestion, "Please, Enter question.");
                    return;
                }
                if (optionOne.isEmpty()) {
                    edtValidator(edtDialogOptionOne, "Option Can't be Empty");
                    return;
                }
                if (optionTwo.isEmpty()) {
                    edtValidator(edtDialogOptionTwo, "Option Can't be Empty");
                    return;
                }
                if (optionThree.isEmpty()) {
                    edtValidator(edtDialogOptionThree, "Option Can't be Empty");
                    return;
                }
                if (optionFour.isEmpty()) {
                    edtValidator(edtDialogOptionFour, "Option Can't be Empty");
                    return;
                }

                boolean isCheckedOne = dialogCheckboxOne.isChecked();
                boolean isCheckedTwo = dialogCheckboxTwo.isChecked();
                boolean isCheckedThree = dialogCheckboxThree.isChecked();
                boolean isCheckedFour = dialogCheckboxFour.isChecked();

                if (!isCheckedOne && !isCheckedTwo && !isCheckedThree && !isCheckedFour) {
                    Toast.makeText(ExamSetActivity.this, "Select Correct Option", Toast.LENGTH_SHORT).show();
                    radioDialogAndGrp.requestFocus();
                } else {
                    if (radioCheckId == R.id.dialog_checkbox_one) {
                        correctAns = optionOne;
                    } else if (radioCheckId == R.id.dialog_checkbox_two) {
                        correctAns = optionTwo;
                    } else if (radioCheckId == R.id.dialog_checkbox_three) {
                        correctAns = optionThree;
                    } else if (radioCheckId == R.id.dialog_checkbox_four) {
                        correctAns = optionFour;
                    }
                }

                if (radioMarkId == R.id.q_dialog_mark_one) {
                    questionMark = Integer.parseInt(dialogMarkOne.getText().toString().trim());
                } else if (radioMarkId == R.id.q_dialog_mark_two) {
                    questionMark = Integer.parseInt(dialogMarkTwo.getText().toString().trim());
                } else if (radioMarkId == R.id.q_dialog_mark_three) {
                    questionMark = Integer.parseInt(dialogMarkThree.getText().toString().trim());
                }

                QuestionModel questionModel = new QuestionModel(question,
                        optionOne,
                        optionTwo,
                        optionThree,
                        optionFour,
                        correctAns,
                        questionMark);

                QuestionModel lastQuestion = questionModelList.get(questionModelList.size() - 1);

                for (int i = 0; i < questionModelList.size(); i++) {
                    QuestionModel newQuestionModel = questionModelList.get(i);
                    questionModelList.set(i, questionModel);
                    questionModel = newQuestionModel;
                }

                questionModelList.add(lastQuestion);

                listShowQuestionAdapter.notifyDataSetChanged();

                addQuestionDialog.dismiss();
            }
        });

        addQuestionDialog.show();
    }

    private void editQuestionDialog(QuestionModel questionModel, int position) {
        Dialog editQuestionDialog = new Dialog(ExamSetActivity.this);
        editQuestionDialog.setContentView(R.layout.dialog_question_edit_add);

        Objects.requireNonNull(editQuestionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        editQuestionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editQuestionDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txtQuestionDialogTitle = editQuestionDialog.findViewById(R.id.txt_question_dialog_title);

        EditText edtDialogQuestion = editQuestionDialog.findViewById(R.id.edt_dialog_question);

        EditText edtDialogOptionOne = editQuestionDialog.findViewById(R.id.edt_dialog_option_one);
        EditText edtDialogOptionTwo = editQuestionDialog.findViewById(R.id.edt_dialog_option_two);
        EditText edtDialogOptionThree = editQuestionDialog.findViewById(R.id.edt_dialog_option_three);
        EditText edtDialogOptionFour = editQuestionDialog.findViewById(R.id.edt_dialog_option_four);

        RadioGroup radioDialogAndGrp = editQuestionDialog.findViewById(R.id.radio_dialog_ans_grp);

        RadioButton dialogCheckboxOne = editQuestionDialog.findViewById(R.id.dialog_checkbox_one);
        RadioButton dialogCheckboxTwo = editQuestionDialog.findViewById(R.id.dialog_checkbox_two);
        RadioButton dialogCheckboxThree = editQuestionDialog.findViewById(R.id.dialog_checkbox_three);
        RadioButton dialogCheckboxFour = editQuestionDialog.findViewById(R.id.dialog_checkbox_four);

        AppCompatButton btnDialogSaveQuestion = editQuestionDialog.findViewById(R.id.btn_dialog_save_question);

        LinearLayout layoutMarkSetter = editQuestionDialog.findViewById(R.id.layout_mark_setter);
        layoutMarkSetter.setVisibility(View.GONE);

        txtQuestionDialogTitle.append("" + (position + 1));

        edtDialogQuestion.setText(questionModel.getQuestion());

        edtDialogOptionOne.setText(questionModel.getOptionOne());
        edtDialogOptionTwo.setText(questionModel.getOptionTwo());
        edtDialogOptionThree.setText(questionModel.getOptionThree());
        edtDialogOptionFour.setText(questionModel.getOptionFour());

        String correctOption = questionModel.getCorrectOption();

        if (correctOption.equals(questionModel.getOptionOne())) {
            dialogCheckboxOne.setChecked(true);
        } else if (correctOption.equals(questionModel.getOptionTwo())) {
            dialogCheckboxTwo.setChecked(true);
        } else if (correctOption.equals(questionModel.getOptionThree())) {
            dialogCheckboxThree.setChecked(true);
        } else if (correctOption.equals(questionModel.getOptionFour())) {
            dialogCheckboxFour.setChecked(true);
        } else {
            Toast.makeText(ExamSetActivity.this, "Something Went Wrong, Please try again.", Toast.LENGTH_SHORT).show();
        }

        editQuestionDialog.show();

        btnDialogSaveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correctAns = "";

                String question = edtDialogQuestion.getText().toString().trim();
                String optionOne = edtDialogOptionOne.getText().toString().trim();
                String optionTwo = edtDialogOptionTwo.getText().toString().trim();
                String optionThree = edtDialogOptionThree.getText().toString().trim();
                String optionFour = edtDialogOptionFour.getText().toString().trim();

                int radioMarkId = radioDialogAndGrp.getCheckedRadioButtonId();

                if (question.isEmpty()) {
                    edtValidator(edtDialogQuestion, "Please, Enter question.");
                    return;
                }
                if (optionOne.isEmpty()) {
                    edtValidator(edtDialogOptionOne, "Option Can't be Empty");
                    return;
                }
                if (optionTwo.isEmpty()) {
                    edtValidator(edtDialogOptionTwo, "Option Can't be Empty");
                    return;
                }
                if (optionThree.isEmpty()) {
                    edtValidator(edtDialogOptionThree, "Option Can't be Empty");
                    return;
                }
                if (optionFour.isEmpty()) {
                    edtValidator(edtDialogOptionFour, "Option Can't be Empty");
                    return;
                }

                if (radioMarkId == R.id.dialog_checkbox_one) {
                    correctAns = optionOne;
                } else if (radioMarkId == R.id.dialog_checkbox_two) {
                    correctAns = optionTwo;
                } else if (radioMarkId == R.id.dialog_checkbox_three) {
                    correctAns = optionThree;
                } else if (radioMarkId == R.id.dialog_checkbox_four) {
                    correctAns = optionFour;
                }

                questionModel.setQuestion(question);
                questionModel.setOptionOne(optionOne);
                questionModel.setOptionTwo(optionTwo);
                questionModel.setOptionThree(optionThree);
                questionModel.setOptionFour(optionFour);
                questionModel.setCorrectOption(correctAns);

                questionModelList.set(position, questionModel);

                listShowQuestionAdapter.notifyDataSetChanged();

                editQuestionDialog.dismiss();
            }
        });
    }

    // Validating exam data
    public void examDataValidator() {
        examName = examSetUpName.getText().toString().trim();
        examSyllabus = examSetUpSyllabus.getText().toString().trim();
        examDate = examSetUpDate.getText().toString().trim();
        examTime = examSetUpTime.getText().toString().trim();
        totalMarks = examSetUpMark.getText().toString().trim();
        duration = examSetUpDuration.getText().toString().trim();

        if (examName.isEmpty()) {
            edtValidator(examSetUpName, "Please, Enter Title");
            return;
        }
        if (examSyllabus.isEmpty()) {
            edtValidator(examSetUpSyllabus, "Please, Enter Syllabus");
            return;
        }
        if (examDate.isEmpty()) {
            examSetUpDate.setError("Please, Enter Date");
            examSetUpDate.requestFocus();
            return;
        }
        if (examTime.isEmpty()) {
            examSetUpTime.setError("Please, Enter Time");
            examSetUpTime.requestFocus();
            return;
        }
        if (totalMarks.isEmpty()) {
            edtValidator(examSetUpMark, "Please, Enter Marks");
            return;
        }
        if (duration.isEmpty()) {
            edtValidator(examSetUpDuration, "Please, Enter Duration");
            return;
        }
        isDataValidate = true;
    }

    // Error setter on EditText
    private void edtValidator(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }
}