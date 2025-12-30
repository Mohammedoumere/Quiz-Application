package com.server;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl extends UnicastRemoteObject implements AuthService {

    private static final String USERS_FILE = "users.dat";
    private Map<String, User> users; // Key is now email

    public AuthServiceImpl() throws RemoteException {
        super();
        this.users = loadUsers();
    }

    @Override
    public synchronized boolean signUp(String username, String email, String password) throws RemoteException {
        if (users.containsKey(email)) {
            return false; // Email already exists
        }
        User newUser = new User(username, email, password);
        users.put(email, newUser);
        saveUsers();
        return true;
    }

    @Override
    public synchronized User signIn(String email, String password) throws RemoteException {
        User user = users.get(email);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null; // Authentication failed
    }

    @Override
    public synchronized boolean changePassword(String email, String oldPassword, String newPassword) throws RemoteException {
        User user = users.get(email);
        if (user != null && user.checkPassword(oldPassword)) {
            User updatedUser = new User(user, newPassword, true);
            users.put(email, updatedUser);
            saveUsers();
            return true;
        }
        return false; // Password change failed
    }

    @Override
    public synchronized void updateProfilePicture(String email, String picturePath) throws RemoteException {
        User user = users.get(email);
        if (user != null) {
            User updatedUser = new User(user, picturePath);
            users.put(email, updatedUser);
            saveUsers();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            return (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new HashMap<>(); // No users yet, return empty map
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
