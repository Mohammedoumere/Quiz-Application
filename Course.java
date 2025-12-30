package com.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
    private String courseName;
    private List<String> filePaths;

    public Course(String courseName, String initialFilePath) {
        this.courseName = courseName;
        this.filePaths = new ArrayList<>();
        if (initialFilePath != null && !initialFilePath.isEmpty()) {
            this.filePaths.add(initialFilePath);
        }
    }

    public String getCourseName() {
        return courseName;
    }

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void addFilePath(String filePath) {
        if (filePath != null && !filePath.isEmpty() && !this.filePaths.contains(filePath)) {
            this.filePaths.add(filePath);
        }
    }

    public void removeFilePath(String filePath) {
        this.filePaths.remove(filePath);
    }

    @Override
    public String toString() {
        return courseName;
    }
}
