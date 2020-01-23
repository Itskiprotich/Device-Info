package com.imeja.carpooling.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Reviews extends RealmObject {

    @SerializedName("Profile")
    @Expose
    public String profile = "";

    @SerializedName("Name")
    @Expose
    public String name = "";

    @SerializedName("Comment")
    @Expose
    public String comment = "";

    @SerializedName("Rating")
    @Expose
    public String rating = "";

    @PrimaryKey
    int realmId = 1;

}
