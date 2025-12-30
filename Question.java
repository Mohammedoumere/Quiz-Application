package com.server;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private String questionText;
    private List<String> choices;
    private int correctIndex;
    private String explanation;

    public Question(String questionText, List<String> choices, int correctIndex, String explanation) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getChoices() { return choices; }
    public int getCorrectIndex() { return correctIndex; }
    public String getExplanation() { return explanation; }
}