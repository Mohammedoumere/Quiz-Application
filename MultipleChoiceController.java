package com.client;

import com.server.Question;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.util.Arrays;
import java.util.List;

public class MultipleChoiceController extends BaseQuizController {

    @FXML private VBox optionsBox;
    @FXML private Button option1Btn;
    @FXML private Button option2Btn;
    @FXML private Button option3Btn;
    @FXML private Button option4Btn;
    private List<Button> optionButtons;

    @Override
    public void initializeQuiz(String title, List<Question> questions) {
        // FIX: Initialize the list of buttons BEFORE calling the super method.
        // The super method will call displayOptions, which needs this list.
        optionButtons = Arrays.asList(option1Btn, option2Btn, option3Btn, option4Btn);
        
        super.initializeQuiz(title, questions);
    }

    @Override
    protected void displayOptions(Question question) {
        for (int i = 0; i < optionButtons.size(); i++) {
            optionButtons.get(i).setText(question.getChoices().get(i));
        }
    }

    @Override
    protected void disableOptions() {
        optionsBox.setDisable(true);
    }

    @Override
    protected void resetUIForNextQuestion() {
        optionsBox.setDisable(false);
        for (Button btn : optionButtons) {
            btn.getStyleClass().removeAll("correct", "incorrect");
        }
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

    private void handleAnswer(int selectedIndex) {
        if (answered) return;
        
        Question q = questions.get(currentQuestion);
        int correctIndex = q.getCorrectIndex();
        
        // Apply styles
        for (int i = 0; i < optionButtons.size(); i++) {
            if (i == correctIndex) {
                optionButtons.get(i).getStyleClass().add("correct");
            } else if (i == selectedIndex) {
                optionButtons.get(i).getStyleClass().add("incorrect");
            }
        }
        
        boolean isCorrect = (selectedIndex == correctIndex);
        String correctAnswerText = q.getChoices().get(correctIndex);
        checkAnswer(isCorrect, correctAnswerText);
    }

    @FXML private void handleOption1() { handleAnswer(0); }
    @FXML private void handleOption2() { handleAnswer(1); }
    @FXML private void handleOption3() { handleAnswer(2); }
    @FXML private void handleOption4() { handleAnswer(3); }
}
