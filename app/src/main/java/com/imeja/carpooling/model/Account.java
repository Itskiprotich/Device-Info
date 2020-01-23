package com.imeja.carpooling.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Account extends RealmObject {
    @SerializedName("Phone")
    @Expose
    public String phone = "";

    @SerializedName("Firstname")
    @Expose
    public String firstname = "";

    @SerializedName("Lastname")
    @Expose
    public String lastname = "";

    @SerializedName("Email")
    @Expose
    public String email = "";

    @SerializedName("Profile")
    @Expose
    public String profile = "";

    @SerializedName("Licence")
    @Expose
    public String licence = "";

    @SerializedName("Registration")
    @Expose
    public String registration = "";

    @SerializedName("Home")
    @Expose
    public String home = "";

    @SerializedName("Office")
    @Expose
    public String office = "";


    boolean loggedIn = false;
    String useridback = "";
    String useridfront = "";
    String profileImage = "";
    String vehicleregistration = "";
    String driverlicence = "";
    String uniquePhone = "";

    @PrimaryKey
    int realmId = 1;
}
