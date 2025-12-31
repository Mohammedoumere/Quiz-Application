package com.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class HomeController {

    @FXML private Label courseCountLabel;
    @FXML private Label questionTypeCountLabel;
    @FXML private Label questionTypesLabel;

    public void setData(int courseCount, List<String> questionTypes) {
        courseCountLabel.setText("Number of Courses Available: " + courseCount);
        questionTypeCountLabel.setText("Number of Question Types Available: " + questionTypes.size());
        questionTypesLabel.setText("Question Types: " + String.join(", ", questionTypes));
    }
}
