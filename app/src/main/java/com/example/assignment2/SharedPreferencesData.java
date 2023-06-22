package com.example.assignment2;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class SharedPreferencesData {

    private static final int NUM_PROBLEMS = 5;
    private static final int MAX_NUM = 10;
    private static final String PREFS_KEY_PROBLEMS = "math_problems";
    private static final Random random = new Random();

    public static List<MathProblem>  getMathProblems(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        List<MathProblem> problems = new ArrayList<>();

        // Check if there are saved problems in SharedPreferences
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

        // Generate new problems if there are no saved problems
        for (int i = 0; i < NUM_PROBLEMS; i++) {
            int num1 = random.nextInt(MAX_NUM + 1);
            int num2 = random.nextInt(MAX_NUM + 1);
            String problem = String.format("%d + %d =", num1, num2);
            int solution = num1 + num2;

            List<String> options = new ArrayList<>();
            options.add(String.valueOf(solution));
            while (options.size() < 4) {
                int incorrect = random.nextInt(MAX_NUM * 2 + 1);
                if (incorrect != solution && !options.contains(String.valueOf(incorrect))) {
                    options.add(String.valueOf(incorrect));
                }
            }
            Collections.shuffle(options);
            problems.add(new MathProblem(problem, String.valueOf(solution), options));
        }

        // Save generated problems in SharedPreferences
        StringBuilder problemBuilder = new StringBuilder();
        for (MathProblem problem : problems) {
            String optionsString = String.join(";", problem.getOptions());
            problemBuilder.append(problem.getProblem()).append(":")
                    .append(problem.getSolution()).append(":")
                    .append(optionsString).append(",");
        }
        prefs.edit().putString(PREFS_KEY_PROBLEMS, problemBuilder.toString()).apply();

        return problems;
    }
}

