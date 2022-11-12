package org.example.authentication;

import org.example.index.IndexManager;
import org.example.server_client.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuthenticationManager {
    List<User> users;
    private static  AuthenticationManager instance;

    private AuthenticationManager() {
        users=new ArrayList<>();
    }
    public void addUser(String username,String password){
        User user=new User();
        user.setUsername(username);
        user.setPassword(password);
        users.add(user);
    }
    public boolean authenticate(String userName,String password){
        boolean isAuthenticated=false;
        for(User user:users){
            boolean isNameMatch= Objects.equals(user.getUsername(), userName);
            boolean isPasswordMatch= Objects.equals(user.getPassword(), password);
            if(isPasswordMatch && isNameMatch){
                isAuthenticated=true;
            }
        }
        return isAuthenticated;
    }
    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }
}
