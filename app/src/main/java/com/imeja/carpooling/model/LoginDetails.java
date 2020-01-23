package com.imeja.carpooling.model;

import com.google.gson.annotations.Expose;

public class LoginDetails {
    //@SerializedName("Username")
    @Expose
    public String userName;
    //@SerializedName("Password")
    @Expose
    public String password;

    public LoginDetails(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
