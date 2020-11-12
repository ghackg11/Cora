package com.example.cora;

public class User {

    public String username;
    public String email;
    public String password;
    public boolean CR = false;

    public User(String username, String email, String password, boolean CR) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.CR = CR;
    }

    public User(){};
}
