package com.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface QuizService extends Remote {
    List<Course> getCourses() throws RemoteException;
    void addCourse(String courseName) throws RemoteException;
    void deleteCourse(String courseName) throws RemoteException;
    void addFileToCourse(String courseName, String filePath) throws RemoteException;
    void deleteFileFromCourse(String courseName, String filePath) throws RemoteException;
    List<Question> getQuestions(String filePath, String questionType, int count) throws RemoteException;
    void saveResult(QuizResult result) throws RemoteException;
}
