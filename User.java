package com.server;

import java.io.Serializable;

public class User implements Serializable {
    private String username; // Nickname
    private String email;    // Unique identifier for login
    private String passwordHash;
    private String profilePicturePath; // Path to the profile picture

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.passwordHash = hashPassword(password);
        this.profilePicturePath = ""; // Default to no picture
    }

    // Constructor to update user with new picture path
    public User(User oldUser, String newProfilePicturePath) {
        this.username = oldUser.getUsername();
        this.email = oldUser.getEmail();
        this.passwordHash = oldUser.passwordHash; // Keep old hash
        this.profilePicturePath = newProfilePicturePath;
    }
    
    // Constructor to update user with new password
    public User(User oldUser, String newPassword, boolean isPasswordChange) {
        this.username = oldUser.getUsername();
        this.email = oldUser.getEmail();
        this.passwordHash = hashPassword(newPassword);
        this.profilePicturePath = oldUser.getProfilePicturePath();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public boolean checkPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    private String hashPassword(String password) {
        // In a real application, use a strong hashing algorithm like bcrypt
        return "hashed_" + password;
    }
}
