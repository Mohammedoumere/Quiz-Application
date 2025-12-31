package com.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthService extends Remote {
    boolean signUp(String username, String email, String password) throws RemoteException;
    User signIn(String email, String password) throws RemoteException;
    boolean changePassword(String email, String oldPassword, String newPassword) throws RemoteException;
    void updateProfilePicture(String email, String picturePath) throws RemoteException;
}
