package com.example.assignment2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_KEY_PROBLEMS = "math_problems";
    private List<MathProblem> mathProblems;
    private int currentProblemIndex = 0;

    private TextView problemTextView;
    private RadioGroup solutionsRadioGroup;
    private Button checkButton;
    private TextView solutionTextView;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mathProblems = getMathProblemsFromSharedPreferences();

        problemTextView = findViewById(R.id.problemTextView);
        solutionsRadioGroup = findViewById(R.id.choicesRadioGroup);
        checkButton = findViewById(R.id.checkButton);
        solutionTextView = findViewById(R.id.solutionTextView);
        nextButton = findViewById(R.id.nextButton);

        showProblem();
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextProblem();
            }
        });
    }

    private List<MathProblem> getMathProblemsFromSharedPreferences() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        List<MathProblem> problems = new ArrayList<>();
        if (prefs.contains(PREFS_KEY_PROBLEMS)) {
            String savedProblems = prefs.getString(PREFS_KEY_PROBLEMS, null);
            if (savedProblems != null) {
                String[] problemStrings = savedProblems.split(",");
                for (String problemString : problemStrings) {
                    String[] parts = problemString.split(":");
                    if (parts.length == 3) {
                        String problem = parts[0];
                        String solution = parts[1];
                        String[] options = parts[2].split(";");
                        problems.add(new MathProblem(problem, solution, Arrays.asList(options)));
                    }
                }
                return problems;
            }
        }
        return SharedPreferencesData.getMathProblems(getApplicationContext());
    }

    private void saveMathProblemsToSharedPreferences(List<MathProblem> problems) {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        StringBuilder problemBuilder = new StringBuilder();
        for (MathProblem problem : problems) {
            String optionsString = String.join(";", problem.getOptions());
            problemBuilder.append(problem.getProblem()).append(":")
                    .append(problem.getSolution()).append(":")
                    .append(optionsString).append(",");
        }
        prefs.edit().putString(PREFS_KEY_PROBLEMS, problemBuilder.toString()).apply();
    }

    private void showProblem() {
        MathProblem problem = mathProblems.get(currentProblemIndex);
        problemTextView.setText(problem.getProblem());
        solutionsRadioGroup.removeAllViews();
        List<String> solutions = new ArrayList<>();
        solutions.add(problem.getSolution());
        while (solutions.size() < 4) {
            String randomSolution = getRandomSolution();
            if (!solutions.contains(randomSolution)) {
                solutions.add(randomSolution);
            }
        }
        Collections.shuffle(solutions);
        for (int i = 0; i < solutions.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(solutions.get(i));
            solutionsRadioGroup.addView(radioButton);
        }
        solutionTextView.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
    }

    private String getRandomSolution() {
        int operand1 = (int) (Math.random() * 10);
        int operand2 = (int) (Math.random() * 10);
        String operator = Math.random() < 0.5 ? "+" : "-";
        int solution = operator.equals("+") ? operand1 + operand2 : operand1 - operand2;
        return String.valueOf(solution);
    }

    private void checkAnswer() {
        MathProblem problem = mathProblems.get(currentProblemIndex);
        RadioButton selectedRadioButton = findViewById(solutionsRadioGroup.getCheckedRadioButtonId());
        if (selectedRadioButton != null) {
            String selectedSolution = selectedRadioButton.getText().toString();
            if (selectedSolution.equals(problem.getSolution())) {
                solutionTextView.setText("Correct!");
                solutionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            } else {
                solutionTextView.setText("Incorrect. The answer is " + problem.getSolution());
                solutionTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            }
            solutionTextView.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            checkButton.setVisibility(View.GONE);
        }
    }

    private void showNextProblem() {
        currentProblemIndex++;
        if (currentProblemIndex >= mathProblems.size()) {
            currentProblemIndex = 0;

        }
        showProblem();
        checkButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveMathProblemsToSharedPreferences(mathProblems);
    }
}