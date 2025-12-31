package com.server;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DataStore {
    private static final String COURSES_FILE = "courses.dat";

    @SuppressWarnings("unchecked")
    public static List<Course> loadCourses() {
        File file = new File(COURSES_FILE);
        if (!file.exists()) {
            return createDefaultCourses();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Course>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return createDefaultCourses(); // Fallback on error
        }
    }

    public static void saveCourses(List<Course> courses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COURSES_FILE))) {
            oos.writeObject(courses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addCourse(String courseName) {
        List<Course> courses = loadCourses();
        boolean exists = courses.stream().anyMatch(c -> c.getCourseName().equalsIgnoreCase(courseName));
        if (!exists) {
            courses.add(new Course(courseName, null));
            saveCourses(courses);
        }
    }

    public static void addFileToCourse(String courseName, String filePath) {
        List<Course> courses = loadCourses();
        Optional<Course> existingCourse = courses.stream()
                .filter(c -> c.getCourseName().equalsIgnoreCase(courseName))
                .findFirst();

        if (existingCourse.isPresent()) {
            existingCourse.get().addFilePath(filePath);
            saveCourses(courses);
        }
    }

    public static void deleteFileFromCourse(String courseName, String filePath) {
        List<Course> courses = loadCourses();
        Optional<Course> existingCourse = courses.stream()
                .filter(c -> c.getCourseName().equalsIgnoreCase(courseName))
                .findFirst();

        if (existingCourse.isPresent()) {
            existingCourse.get().removeFilePath(filePath);
            saveCourses(courses);
        }
    }
    
    public static void removeCourse(String courseName) {
        List<Course> courses = loadCourses();
        courses.removeIf(c -> c.getCourseName().equalsIgnoreCase(courseName));
        saveCourses(courses);
    }

    private static List<Course> createDefaultCourses() {
        List<String> courseNames = Arrays.asList(
            "Java", "C++", "Database", "Data Structure", "Automata", "Microprocessor",
            "Software Engineering", "Operating System", "Numerics", "Object Oriented AI",
            "Web Development", "Security", "Multimedia", "Mobile App Development", "Computer Vision"
        );

        List<Course> defaultCourses = new ArrayList<>();
        for (String name : courseNames) {
            defaultCourses.add(new Course(name, null)); // Create with no files
        }
        
        saveCourses(defaultCourses);
        return defaultCourses;
    }
}
