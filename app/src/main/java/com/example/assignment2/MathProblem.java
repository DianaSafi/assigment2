package com.example.assignment2;

import java.util.List;

public class MathProblem {
    private String problem;
    private String solution;
    private List<String> options;

    public MathProblem(String problem, String solution, List<String> options) {
        this.problem = problem;
        this.solution = solution;
        this.options = options;
    }

    public String getProblem() {
        return problem;
    }

    public String getSolution() {
        return solution;
    }

    public List<String> getOptions() {
        return options;
    }
}