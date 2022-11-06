package org.example.authentication;

import java.util.List;
import java.util.Objects;

public class AuthenticationManager {
    List<User> users;

    public AuthenticationManager(List<User> users) {
        this.users = users;
    }
    public void authenticate(User user){
        boolean isAuthenticated=false;
        for(User databaseUser:users){
            boolean isNameMatch= Objects.equals(databaseUser.getUsername(), user.getUsername());
            boolean isUUIDMatch= Objects.equals(databaseUser.getUuid(), user.getUuid());
            if(isUUIDMatch && isNameMatch){
                isAuthenticated=true;
            }
        }
        if(!isAuthenticated){
            throw new IllegalArgumentException();
        }
    }
}
