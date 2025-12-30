package com.client;

import com.server.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;

public class ProfileController {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private ImageView profileImageView;
    @FXML private Circle profilePictureClip;
    @FXML private Label statusLabel;
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

    private String username;
    private String email;
    private String profilePicturePath;
    private AuthService authService;

    public void setProfileData(String username, String email, String profilePicturePath) {
        this.username = username;
        this.email = email;
        this.profilePicturePath = profilePicturePath;
        updateUI();
    }

    @FXML
    public void initialize() {
        Circle clip = new Circle(profileImageView.getFitWidth() / 2, profileImageView.getFitHeight() / 2, 75);
        profileImageView.setClip(clip);

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

    private void updateUI() {
        usernameLabel.setText(username);
        emailLabel.setText(email);
        if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
            loadProfilePicture(profilePicturePath);
        } else {
            profileImageView.setImage(null);
        }
    }

    private void loadProfilePicture(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Image image = new Image(file.toURI().toURL().toExternalForm());
                if (!image.isError()) {
                    profileImageView.setImage(image);
                } else {
                    statusLabel.setText("Error loading image.");
                }
            }
        } catch (MalformedURLException e) {
            statusLabel.setText("Invalid image path.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            this.profilePicturePath = selectedFile.getAbsolutePath();
            loadProfilePicture(profilePicturePath);
            try {
                authService.updateProfilePicture(email, profilePicturePath);
                statusLabel.setText("Profile picture updated.");
            } catch (Exception e) {
                statusLabel.setText("Error saving profile picture.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleShowPasswordChange() {
        passwordChangeBox.setVisible(true);
        passwordChangeBox.setManaged(true);
    }

    @FXML
    private void handleCancelPasswordChange() {
        passwordChangeBox.setVisible(false);
        passwordChangeBox.setManaged(false);
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleConfirmPasswordChange() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("All password fields are required.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        try {
            boolean success = authService.changePassword(email, oldPassword, newPassword);
            if (success) {
                statusLabel.setText("Password changed successfully.");
                handleCancelPasswordChange();
            } else {
                statusLabel.setText("Password change failed. Check old password.");
            }
        } catch (Exception e) {
            statusLabel.setText("Error changing password.");
            e.printStackTrace();
        }
    }
}
