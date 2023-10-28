package com.itbd.examnierteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itbd.examnierteacher.DataMoldes.ExamDataModel;
import com.itbd.examnierteacher.DataMoldes.QuestionModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionSetActivity extends AppCompatActivity {

    TextView txtQuestionNo, txtExamTotalMarks, txtQuestionTotalMarks;
    AppCompatButton btnAddQuestion, btnSaveQuestion;
    EditText edtQuestion,
            edtOptionOne, edtOptionTwo, edtOptionThree, edtOptionFour;
    RadioGroup radioAnsGrp, radioMarkGrp;

    RadioButton checkOne, checkTwo, checkThree, checkFour,
            markOne, markTwo, markThree;

    String question, optionOne, optionTwo, optionThree, optionFour, correctAns;
    int questionMark, totalQuestionMark, totalExamMark, questionNo = 1;

    ExamDataModel examDataModel;
    List<QuestionModel> questionModelList = new ArrayList<>();
    List<QuestionModel> markOneQuestionList = new ArrayList<>();
    List<QuestionModel> markTwoQuestionList = new ArrayList<>();
    List<QuestionModel> markThreeQuestionList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getWindow().setStatusBarColor(ContextCompat.getColor(QuestionSetActivity.this, R.color.blue_pr));

        examDataModel = (ExamDataModel) getIntent().getSerializableExtra("examData");

        txtQuestionNo = findViewById(R.id.txt_question_no);
        txtExamTotalMarks = findViewById(R.id.txt_exam_total_marks);
        txtQuestionTotalMarks = findViewById(R.id.txt_question_total_marks);

        edtQuestion = findViewById(R.id.edt_question);
        edtOptionOne = findViewById(R.id.edt_option_one);
        edtOptionTwo = findViewById(R.id.edt_option_two);
        edtOptionThree = findViewById(R.id.edt_option_three);
        edtOptionFour = findViewById(R.id.edt_option_four);

        radioAnsGrp = findViewById(R.id.radio_ans_grp);
        radioMarkGrp = findViewById(R.id.radio_mark_grp);

        checkOne = findViewById(R.id.checkbox_one);
        checkTwo = findViewById(R.id.checkbox_two);
        checkThree = findViewById(R.id.checkbox_three);
        checkFour = findViewById(R.id.checkbox_four);

        markOne = findViewById(R.id.q_mark_one);
        markTwo = findViewById(R.id.q_mark_two);
        markThree = findViewById(R.id.q_mark_three);

        btnAddQuestion = findViewById(R.id.btn_add_question);
        btnSaveQuestion = findViewById(R.id.btn_save_question);

        totalExamMark = Integer.parseInt(examDataModel.getTotalMarks());
        txtExamTotalMarks.setText(String.valueOf(totalExamMark));

        Dialog questionRulesDialog = new Dialog(QuestionSetActivity.this);
        questionRulesDialog.setContentView(R.layout.dialog_question_rules);
        Objects.requireNonNull(questionRulesDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        questionRulesDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        questionRulesDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView txtRulesThree = questionRulesDialog.findViewById(R.id.txt_question_rules_three);
        txtRulesThree.setText("03. You need to add at least " + totalExamMark + " question that has 1 mark.");

        AppCompatButton btnQuestionRulesOK = questionRulesDialog.findViewById(R.id.btn_question_rules_ok);
        btnQuestionRulesOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionRulesDialog.dismiss();
            }
        });

        questionRulesDialog.show();

        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestion();
            }
        });

        btnSaveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveQuestion();
            }
        });

    }

    private void saveQuestion() {
        addQuestion();

        if (markThreeQuestionList.size() < 1) {
            Toast.makeText(QuestionSetActivity.this, "You need to add at least one question that has 3 mark", Toast.LENGTH_SHORT).show();
            return;
        }

        if (markTwoQuestionList.size() < 1) {
            Toast.makeText(QuestionSetActivity.this, "You need to add at least one question that has 2 mark", Toast.LENGTH_SHORT).show();
            return;
        }

        if (markOneQuestionList.size() < totalExamMark) {
            int leftOneMarkQuestion = totalExamMark - markOneQuestionList.size();
            Toast.makeText(QuestionSetActivity.this, "You need to add " + leftOneMarkQuestion + " more question that has 1 mark", Toast.LENGTH_SHORT).show();
            return;
        }

        questionModelList.addAll(markOneQuestionList);
        questionModelList.addAll(markTwoQuestionList);
        questionModelList.addAll(markThreeQuestionList);

        examDataModel.setQuestionModelList(questionModelList);
        Intent goExamSet = new Intent(QuestionSetActivity.this, ExamSetActivity.class);

        goExamSet.putExtra("identifyIntent", 3);
        goExamSet.putExtra("examData", examDataModel);
        goExamSet.putExtra("isQuestionAdded", true);

        startActivity(goExamSet);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void addQuestion() {
        question = edtQuestion.getText().toString().trim();

        optionOne = edtOptionOne.getText().toString().trim();
        optionTwo = edtOptionTwo.getText().toString().trim();
        optionThree = edtOptionThree.getText().toString().trim();
        optionFour = edtOptionFour.getText().toString().trim();

        int radioMarkId = radioMarkGrp.getCheckedRadioButtonId();

        boolean isCheckedOne = checkOne.isChecked();
        boolean isCheckedTwo = checkTwo.isChecked();
        boolean isCheckedThree = checkThree.isChecked();
        boolean isCheckedFour = checkFour.isChecked();

        if (question.isEmpty()) {
            edtValidator(edtQuestion, "Please, Enter question.");
            return;
        }
        if (optionOne.isEmpty()) {
            edtValidator(edtOptionOne, "Option Can't be Empty");
            return;
        }
        if (optionTwo.isEmpty()) {
            edtValidator(edtOptionTwo, "Option Can't be Empty");
            return;
        }
        if (optionThree.isEmpty()) {
            edtValidator(edtOptionThree, "Option Can't be Empty");
            return;
        }
        if (optionFour.isEmpty()) {
            edtValidator(edtOptionFour, "Option Can't be Empty");
            return;
        }

        if (!isCheckedOne && !isCheckedTwo && !isCheckedThree && !isCheckedFour) {
            Toast.makeText(QuestionSetActivity.this, "Select Correct Option", Toast.LENGTH_SHORT).show();
            radioAnsGrp.requestFocus();
            return;
        } else {
            int radioAnsID = radioAnsGrp.getCheckedRadioButtonId();

            if (radioAnsID == R.id.checkbox_one) {
                correctAns = optionOne;
            } else if (radioAnsID == R.id.checkbox_two) {
                correctAns = optionTwo;
            } else if (radioAnsID == R.id.checkbox_three) {
                correctAns = optionThree;
            } else if (radioAnsID == R.id.checkbox_four) {
                correctAns = optionFour;
            }
        }

        if (radioMarkId == R.id.q_mark_one) {
            questionMark = Integer.parseInt(markOne.getText().toString());
        } else if (radioMarkId == R.id.q_mark_two) {
            questionMark = Integer.parseInt(markTwo.getText().toString());
        } else if (radioMarkId == R.id.q_mark_three) {
            questionMark = Integer.parseInt(markThree.getText().toString());
        }

        if (questionMark == 1) {
            markOneQuestionList.add(new QuestionModel(question, optionOne, optionTwo,
                    optionThree, optionFour, correctAns, questionMark));
        } else if (questionMark == 2) {
            markTwoQuestionList.add(new QuestionModel(question, optionOne, optionTwo,
                    optionThree, optionFour, correctAns, questionMark));
        } else if (questionMark == 3) {
            markThreeQuestionList.add(new QuestionModel(question, optionOne, optionTwo,
                    optionThree, optionFour, correctAns, questionMark));
        }

        totalQuestionMark += questionMark;
        if (totalQuestionMark < 10) {
            txtQuestionTotalMarks.setText("0" + totalQuestionMark);
        } else {
            txtQuestionTotalMarks.setText("" + totalQuestionMark);
        }

        questionNo++;
        if (questionNo < 10) {
            txtQuestionNo.setText("Question No : 0" + questionNo);
        } else {
            txtQuestionNo.setText("Question No : " + questionNo);
        }

        edtQuestion.setText("");
        edtOptionOne.setText("");
        edtOptionTwo.setText("");
        edtOptionThree.setText("");
        edtOptionFour.setText("");

        radioAnsGrp.clearCheck();
        markOne.setChecked(true);
    }

    private void edtValidator(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

}