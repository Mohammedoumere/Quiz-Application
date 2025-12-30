package com.client;

import com.server.Course;
import com.server.QuizService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

import java.io.File;
import java.rmi.Naming;
import java.util.List;
import java.util.Optional;

public class CategoriesController {

    @FXML private ListView<Course> courseListView;
    @FXML private ListView<String> fileListView;
    @FXML private Button addFileButton;
    @FXML private Button deleteFileButton;
    private QuizService quizService;
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadCourses();

        // Set a custom cell factory to display only the file name
        fileListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Display just the name of the file, not the full path
                    setText(new File(item).getName());
                }
            }
        });

        courseListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateFileView(newVal);
                addFileButton.setDisable(false);
                deleteFileButton.setDisable(true);
            } else {
                fileListView.getItems().clear();
                addFileButton.setDisable(true);
                deleteFileButton.setDisable(true);
            }
        });

        fileListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            deleteFileButton.setDisable(newVal == null);
        });
        
        fileListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedFile = fileListView.getSelectionModel().getSelectedItem();
                if (selectedFile != null) {
                    mainController.handleFileSelection(selectedFile);
                }
            }
        });
    }

    private void loadCourses() {
        try {
            quizService = (QuizService) Naming.lookup("rmi://localhost/quizService");
            List<Course> courses = quizService.getCourses();
            courseListView.getItems().setAll(courses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFileView(Course course) {
        if (course != null && course.getFilePaths() != null) {
            fileListView.getItems().setAll(course.getFilePaths());
        } else {
            fileListView.getItems().clear();
        }
    }

    @FXML
    private void handleAddCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Course");
        dialog.setHeaderText("Enter the name for the new course:");
        // Apply theme to the dialog
        if (mainController != null) {
            mainController.applyThemeToDialog(dialog);
        }
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(courseName -> {
            if (!courseName.trim().isEmpty()) {
                try {
                    quizService.addCourse(courseName.trim());
                    loadCourses();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a course to delete.");
            if (mainController != null) mainController.applyThemeToDialog(alert);
            alert.show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete '" + selectedCourse.getCourseName() + "'?", ButtonType.YES, ButtonType.NO);
        if (mainController != null) mainController.applyThemeToDialog(alert);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    quizService.deleteCourse(selectedCourse.getCourseName());
                    loadCourses();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleAddFiles() {
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a course from the left list first.");
            if (mainController != null) mainController.applyThemeToDialog(alert);
            alert.show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Files to " + selectedCourse.getCourseName());
        List<File> files = fileChooser.showOpenMultipleDialog(courseListView.getScene().getWindow());

        if (files != null && !files.isEmpty()) {
            try {
                for (File file : files) {
                    quizService.addFileToCourse(selectedCourse.getCourseName(), file.getAbsolutePath());
                }
                int selectedIndex = courseListView.getSelectionModel().getSelectedIndex();
                loadCourses();
                courseListView.getSelectionModel().select(selectedIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteFile() {
        Course selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedCourse == null || selectedFile == null) return;

        try {
            quizService.deleteFileFromCourse(selectedCourse.getCourseName(), selectedFile);
            int selectedIndex = courseListView.getSelectionModel().getSelectedIndex();
            loadCourses();
            courseListView.getSelectionModel().select(selectedIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
