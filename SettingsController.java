package com.client;

import com.server.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.rmi.Naming;

public class SettingsController {

    @FXML private Label emailLabel;
    @FXML private VBox passwordChangeBox;
    @FXML private PasswordField oldPasswordField;
    @FXML private TextField oldPasswordTextField;
    @FXML private CheckBox showOldPasswordCheckBox;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordTextField;
    @FXML private CheckBox showNewPasswordCheckBox;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordTextField;
    @FXML private CheckBox showConfirmPasswordCheckBox;
    @FXML private CheckBox dailyReminderCheck;
    @FXML private CheckBox quizAlertsCheck;
    @FXML private ChoiceBox<String> themeChoiceBox;
    @FXML private Label statusLabel;
    @FXML private CheckBox soundCheck;

    private MainController mainController;
    private AuthService authService;
    private String userEmail;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setUserData(String email) {
        this.userEmail = email;
        emailLabel.setText(email);
    }

    @FXML
    public void initialize() {
        // Theme settings
        themeChoiceBox.getItems().addAll("Light", "Dark");
        themeChoiceBox.setValue("Light");
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mainController.setTheme(newVal);
                SoundManager.playNotificationSound();
            }
        });

        // Notification settings
        dailyReminderCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Daily Reminders Toggled: " + (newVal ? "ON" : "OFF"));
            SoundManager.playNotificationSound();
        });
        
        quizAlertsCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Quiz Alerts Toggled: " + (newVal ? "ON" : "OFF"));
            SoundManager.playNotificationSound();
        });

        // Sound settings
        soundCheck.setSelected(SoundManager.isSoundEnabled());
        soundCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSoundEnabled(newVal);
            System.out.println("Quiz Sounds Toggled: " + (newVal ? "ON" : "OFF"));
            if (newVal) SoundManager.playNotificationSound();
        });

        // Password visibility
        setupPasswordVisibility(oldPasswordField, oldPasswordTextField, showOldPasswordCheckBox);
        setupPasswordVisibility(newPasswordField, newPasswordTextField, showNewPasswordCheckBox);
        setupPasswordVisibility(confirmPasswordField, confirmPasswordTextField, showConfirmPasswordCheckBox);

        try {
            authService = (AuthService) Naming.lookup("rmi://localhost/authService");
        } catch (Exception e) {
            statusLabel.setText("Error connecting to authentication service.");
            e.printStackTrace();
        }
    }
    
    private void setupPasswordVisibility(PasswordField passwordField, TextField textField, CheckBox checkBox) {
        textField.managedProperty().bind(checkBox.selectedProperty());
        textField.visibleProperty().bind(checkBox.selectedProperty());
        passwordField.managedProperty().bind(checkBox.selectedProperty().not());
        passwordField.visibleProperty().bind(checkBox.selectedProperty().not());
        textField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void handleLogout() {
        if (mainController != null) {
            mainController.logout();
        }
    }

    @FXML
    private void handleShowPasswordChange() {
        passwordChangeBox.setVisible(true);
        passwordChangeBox.setManaged(true);
        SoundManager.playNotificationSound();
    }

    @FXML
    private void handleCancelPasswordChange() {
        passwordChangeBox.setVisible(false);
        passwordChangeBox.setManaged(false);
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        SoundManager.playNotificationSound();
    }

    @FXML
    private void handleConfirmPasswordChange() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All password fields are required.");
            SoundManager.playErrorSound();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("New passwords do not match.");
            SoundManager.playErrorSound();
            return;
        }

        try {
            boolean success = authService.changePassword(userEmail, oldPassword, newPassword);
            if (success) {
                statusLabel.setText("Password changed successfully.");
                SoundManager.playSuccessSound();
                handleCancelPasswordChange();
            } else {
                statusLabel.setText("Password change failed. Check old password.");
                SoundManager.playErrorSound();
            }
        } catch (Exception e) {
            statusLabel.setText("Error changing password.");
            e.printStackTrace();
        }
    }
}
