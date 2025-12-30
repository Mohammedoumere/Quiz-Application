package com.client;

import com.server.AuthService;
import com.server.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.Naming;
import java.util.regex.Pattern;

public class LoginController {

    @FXML private VBox signUpBox;
    @FXML private TextField usernameSignUpField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private CheckBox showPasswordCheckBox;
    @FXML private Label statusLabel;

    private AuthService authService;
    private boolean isSignUp = false;
    private static final Pattern GMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@gmail\\.com$");

    @FXML
    public void initialize() {
        setupPasswordVisibility();
        try {
            authService = (AuthService) Naming.lookup("rmi://localhost/authService");
        } catch (Exception e) {
            statusLabel.setText("Error connecting to authentication service.");
            e.printStackTrace();
        }
    }

    private void setupPasswordVisibility() {
        passwordTextField.managedProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordTextField.visibleProperty().bind(showPasswordCheckBox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckBox.selectedProperty().not());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    private void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (!isValidEmail(email) || password.isEmpty()) {
            statusLabel.setText("Valid email and password are required.");
            return;
        }

        try {
            User user = authService.signIn(email, password);
            if (user != null) {
                statusLabel.setText("Sign-in successful!");
                loadMainApplication(user);
            } else {
                statusLabel.setText("Invalid email or password.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error during sign-in.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp() {
        if (!isSignUp) {
            statusLabel.setText("Please click 'New User?' to enable sign-up.");
            return;
        }

        String username = usernameSignUpField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || !isValidEmail(email) || password.isEmpty()) {
            statusLabel.setText("Username, valid email, and password are required.");
            return;
        }

        try {
            if (authService.signUp(username, email, password)) {
                statusLabel.setText("Sign-up successful! Please sign in.");
                toggleSignUp();
                emailField.clear();
                passwordField.clear();
            } else {
                statusLabel.setText("Email is already taken.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error during sign-up.");
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleSignUp() {
        isSignUp = !isSignUp;
        signUpBox.setVisible(isSignUp);
        signUpBox.setManaged(isSignUp);
        statusLabel.setText(isSignUp ? "Please fill in all fields to sign up." : "");
    }

    private boolean isValidEmail(String email) {
        if (email.isEmpty()) return false;
        return GMAIL_PATTERN.matcher(email).matches();
    }

    private void loadMainApplication(User user) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/main.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            MainController mainController = loader.getController();
            mainController.setCurrentUser(user);
            mainController.setPrimaryStage(stage);
            mainController.setMainScene(scene); // Pass the scene to the controller

            stage.setScene(scene);
            stage.setTitle("Quiz App");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to load the main application.");
        }
    }
}
