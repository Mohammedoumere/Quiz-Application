package com.client;

import com.server.Question;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class TrueFalseController extends BaseQuizController {

    @FXML private HBox optionsBox;

    @Override
    protected void displayOptions(Question question) {
        // No options to display for True/False
    }

    @Override
    protected void disableOptions() {
        optionsBox.setDisable(true);
    }

    @Override
    protected void resetUIForNextQuestion() {
        optionsBox.setDisable(false);
        answerStatusLabel.setVisible(false);
        nextBtn.setDisable(true);
        skipBtn.setDisable(false);
        answered = false;
    }

    @Override
    protected void disableAllControls() {
        disableOptions();
        skipBtn.setDisable(true);
        nextBtn.setDisable(true);
    }

    private void handleAnswer(boolean wasCorrect) {
        checkAnswer(wasCorrect, "True");
    }

    @FXML private void handleTrue() { handleAnswer(true); }
    @FXML private void handleFalse() { handleAnswer(false); }
}
