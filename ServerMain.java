package com.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerMain {
    public static void main(String[] args) {
        int port = 1099;
        try {
            System.out.println("Starting Quiz Server...");
            LocateRegistry.createRegistry(1099);

            QuizService quizService = new QuizServiceImpl();
            Naming.rebind("quizService", quizService);

            AuthService authService = new AuthServiceImpl();
            Naming.rebind("authService", authService);

            System.out.println("Quiz and Auth RMI Services are running on port 1099...");
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
