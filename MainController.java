package com.client;

import com.server.Course;
import com.server.Question;
import com.server.QuizService;
import com.server.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.Naming;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private VBox contentArea;

    private User currentUser;
    private Stage primaryStage;
    private QuizService quizService;
    private Scene mainScene;
    private String currentTheme = "Light"; // Default theme

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        try {
            quizService = (QuizService) Naming.lookup("rmi://localhost/quizService");
        } catch (Exception e) {
            e.printStackTrace();
        }
        showHome();
    }

    @FXML
    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/home.fxml"));
            Node homeView = loader.load();
            HomeController homeController = loader.getController();
            
            List<Course> courses = quizService.getCourses();
            List<String> questionTypes = Arrays.asList("Multiple Choice", "True/False", "Fill in the Blank", "Workout");
            homeController.setData(courses.size(), questionTypes);
            
            contentArea.getChildren().setAll(homeView);
        } catch (Exception e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading home view."));
        }
    }

    @FXML
    public void showCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/categories.fxml"));
            Node categoriesView = loader.load();
            CategoriesController categoriesController = loader.getController();
            categoriesController.setMainController(this);
            contentArea.getChildren().setAll(categoriesView);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading categories view."));
        }
    }

    public void handleFileSelection(String filePath) {
        Platform.runLater(() -> {
            String fileName = new File(filePath).getName();
            List<String> questionTypes = Arrays.asList("Multiple Choice", "True/False", "Fill in the Blank", "Workout");
            ChoiceDialog<String> typeDialog = new ChoiceDialog<>(questionTypes.get(0), questionTypes);
            typeDialog.setTitle("Start Quiz");
            typeDialog.setHeaderText("Choose question type for '" + fileName + "':");
            applyThemeToDialog(typeDialog); // Apply theme
            Optional<String> resultType = typeDialog.showAndWait();

            resultType.ifPresent(selectedType -> {
                TextInputDialog countDialog = new TextInputDialog("10");
                countDialog.setTitle("Question Count");
                countDialog.setHeaderText("How many questions do you want to generate?");
                applyThemeToDialog(countDialog); // Apply theme
                Optional<String> resultCount = countDialog.showAndWait();

                resultCount.ifPresent(countStr -> {
                    try {
                        int count = Integer.parseInt(countStr.trim());
                        startQuiz(filePath, selectedType, count);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format for question count.");
                    }
                });
            });
        });
    }

    private void startQuiz(String filePath, String questionType, int count) {
        try {
            List<Question> questions = quizService.getQuestions(filePath, questionType, count);
            
            String fxmlFile;
            switch (questionType) {
                case "True/False": fxmlFile = "/com/client/true_false.fxml"; break;
                case "Fill in the Blank": fxmlFile = "/com/client/fill_in_the_blank.fxml"; break;
                case "Workout": fxmlFile = "/com/client/workout.fxml"; break;
                default: fxmlFile = "/com/client/multiple_choice.fxml"; break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node quizView = loader.load();
            BaseQuizController quizController = loader.getController();
            quizController.setMainController(this);
            quizController.initializeQuiz(new File(filePath).getName(), questions);
            
            contentArea.getChildren().setAll(quizView);
        } catch (Exception e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading quiz view."));
        }
    }

    @FXML
    public void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/profile.fxml"));
            Node profileView = loader.load();
            ProfileController profileController = loader.getController();
            if (currentUser != null) {
                profileController.setProfileData(currentUser.getUsername(), currentUser.getEmail(), currentUser.getProfilePicturePath());
            } else {
                profileController.setProfileData("Guest", "guest@example.com", "");
            }
            contentArea.getChildren().setAll(profileView);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading profile view."));
        }
    }

    @FXML
    public void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/settings.fxml"));
            Node settingsView = loader.load();
            SettingsController settingsController = loader.getController();
            settingsController.setMainController(this);
            if (currentUser != null) {
                settingsController.setUserData(currentUser.getEmail());
            }
            contentArea.getChildren().setAll(settingsView);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().setAll(new Label("Error loading settings view."));
        }
    }

    public void setTheme(String theme) {
        this.currentTheme = theme;
        if (mainScene == null) {
            System.err.println("Error: mainScene is null. Cannot set theme.");
            return;
        }
        mainScene.getStylesheets().clear();
        String cssPath = "Dark".equalsIgnoreCase(theme) ? "dark_theme.css" : "quiz.css";
        
        URL cssUrl = getClass().getResource(cssPath);
        if (cssUrl == null) {
            System.err.println("FATAL ERROR: Cannot find stylesheet resource: " + cssPath);
            URL defaultUrl = getClass().getResource("quiz.css");
            if (defaultUrl != null) {
                mainScene.getStylesheets().add(defaultUrl.toExternalForm());
            }
        } else {
            mainScene.getStylesheets().add(cssUrl.toExternalForm());
        }
    }
    
    // Helper method to apply theme to dialogs
    public void applyThemeToDialog(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().clear();
        String cssPath = "Dark".equalsIgnoreCase(currentTheme) ? "dark_theme.css" : "quiz.css";
        URL cssUrl = getClass().getResource(cssPath);
        if (cssUrl != null) {
            dialogPane.getStylesheets().add(cssUrl.toExternalForm());
        }
    }

    public void logout() {
        currentUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/client/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Quiz App - Login");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load login screen after logout.");
        }
    }
}
