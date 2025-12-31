package com.client;

import com.server.Question;

public class WorkoutController extends BaseQuizController {

    @Override
    public void initializeQuiz(String title, java.util.List<Question> questions) {
        super.initializeQuiz(title, questions);
        // For workout, "Next" is always available
        nextBtn.setDisable(false);
        skipBtn.setDisable(true);
    }

    @Override
    protected void displayOptions(Question question) {
        // No options
    }

    @Override
    protected void disableOptions() {
        // No options
    }

    @Override
    protected void resetUIForNextQuestion() {
        nextBtn.setDisable(false);
        skipBtn.setDisable(true);
    }

    @Override
    protected void disableAllControls() {
        nextBtn.setDisable(true);
        skipBtn.setDisable(true);
    }
}
