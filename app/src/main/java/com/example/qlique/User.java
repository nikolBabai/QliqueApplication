package com.example.qlique;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String firstName, lastName, email, city, gender;
    public List<String> friends;
    public List<String> hobbies;
    public User(String firstName, String lastName, String city, String email, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.gender = gender;
        this.friends = new ArrayList<>();
    }
    public List<String>getFriends() {
        if (this.friends!=null){
            return this.friends;
        }
        return null;
    }
    public List<String>getHobbiess() {
        if (this.hobbies!=null){
            return this.hobbies;
        }
        return null;
    }
    public String getFirstName() {
        if (this.hobbies!=null){
            return this.firstName;
        }
        return null;
    }
    public User() {
    }

}