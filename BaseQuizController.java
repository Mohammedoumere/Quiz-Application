package com.client;

import com.server.Question;
import com.server.QuizResult;
import com.server.QuizService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.rmi.Naming;
import java.util.List;

public abstract class BaseQuizController {

    @FXML protected Label questionLabel;
    @FXML protected Label scoreLabel;
    @FXML protected Label statusLabel;
    @FXML protected Label answerStatusLabel;
    @FXML protected Button skipBtn;
    @FXML protected Button nextBtn;
    @FXML protected Button exitBtn;
    @FXML protected Label titleLabel;

    protected QuizService quizService;
    protected List<Question> questions;
    protected int currentQuestion = 0;
    protected int score = 0;
    protected int totalQuestions = 0;
    protected boolean answered = false;
    protected MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initializeQuiz(String title, List<Question> questions) {
        this.titleLabel.setText(title);
        this.questions = questions;
        this.totalQuestions = questions.size();

        if (totalQuestions == 0) {
            statusLabel.setText("No questions could be generated for this type.");
            disableAllControls();
            return;
        }

        statusLabel.setText(totalQuestions + " questions generated.");
        loadQuestion();
        nextBtn.setDisable(true);
    }

    protected void loadQuestion() {
        if (currentQuestion < totalQuestions) {
            Question q = questions.get(currentQuestion);
            questionLabel.setText("Question " + (currentQuestion + 1) + ": " + q.getQuestionText());
            scoreLabel.setText("Score: " + score + "/" + totalQuestions);
            
            // Abstract method to be implemented by subclasses
            displayOptions(q);

            resetUIForNextQuestion();
        } else {
            endQuiz();
        }
    }

    protected void checkAnswer(boolean isCorrect, String correctAnswer) {
        if (answered) return;
        answered = true;

        answerStatusLabel.getStyleClass().removeAll("correct", "incorrect");
        if (isCorrect) {
            score++;
            answerStatusLabel.setText("✔ Correct!");
            answerStatusLabel.getStyleClass().add("correct");
            SoundManager.playSuccessSound();
        } else {
            answerStatusLabel.setText("✘ Wrong! Correct answer: " + correctAnswer);
            answerStatusLabel.getStyleClass().add("incorrect");
            SoundManager.playErrorSound();
        }

        scoreLabel.setText("Score: " + score + "/" + totalQuestions);
        answerStatusLabel.setVisible(true);
        nextBtn.setDisable(false);
        skipBtn.setDisable(true);
        disableOptions();
    }

    private void endQuiz() {
        try {
            if (quizService == null) {
                quizService = (QuizService) Naming.lookup("rmi://localhost/quizService");
            }
            quizService.saveResult(new QuizResult("User", score));
        } catch (Exception e) {
            System.err.println("Could not save result: " + e.getMessage());
        }
        questionLabel.setText("Quiz Completed!");
        statusLabel.setText("Final Score: " + score + "/" + totalQuestions);
        disableAllControls();
        answerStatusLabel.setVisible(false);
        exitBtn.setText("Back to Categories");
        SoundManager.playSuccessSound(); // Play success sound on completion
    }

    @FXML
    protected void handleSkip() {
        currentQuestion++;
        loadQuestion();
        SoundManager.playNotificationSound();
    }

    @FXML
    protected void handleNext() {
        currentQuestion++;
        loadQuestion();
        SoundManager.playNotificationSound();
    }

    @FXML
    protected void handleExit() {
        if (mainController != null) {
            mainController.showCategories();
        }
        SoundManager.playNotificationSound();
    }

    // --- Abstract methods for subclasses ---
    protected abstract void displayOptions(Question question);
    protected abstract void disableOptions();
    protected abstract void resetUIForNextQuestion();
    protected abstract void disableAllControls();
}
