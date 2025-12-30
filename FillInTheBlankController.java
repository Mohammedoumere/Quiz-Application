package com.client;

import com.server.Question;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class FillInTheBlankController extends BaseQuizController {

    @FXML private HBox answerBox;
    @FXML private TextField answerField;

    @Override
    protected void displayOptions(Question question) {
        // No options to display
    }

    @Override
    protected void disableOptions() {
        answerBox.setDisable(true);
    }

    @Override
    protected void resetUIForNextQuestion() {
        answerBox.setDisable(false);
        answerField.clear();
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

    @FXML
    private void handleSubmitAnswer() {
        Question q = questions.get(currentQuestion);
        String correctAnswer = q.getChoices().get(0);
        boolean isCorrect = answerField.getText().trim().equalsIgnoreCase(correctAnswer);
        checkAnswer(isCorrect, correctAnswer);
    }
}
