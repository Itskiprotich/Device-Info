package com.imeja.carpooling.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Riders extends RealmObject {
/*
    @SerializedName("updated_at")
    @Expose
    public String updated_at = "";*/

    @SerializedName("created_at")
    @Expose
    public String created_at = "";

    @SerializedName("status")
    @Expose
    public String status = "";

    @SerializedName("price")
    @Expose
    public String price = "";

    @SerializedName("seats")
    @Expose
    public String seats = "";

    @SerializedName("ride_on")
    @Expose
    public String ride_on = "";

    @SerializedName("latitude_to")
    @Expose
    public String latitude_to = "";

    @SerializedName("longitude_to")
    @Expose
    public String longitude_to = "";

    @SerializedName("address_to")
    @Expose
    public String address_to = "";

    @SerializedName("latitude_from")
    @Expose
    public String latitude_from = "";

    @SerializedName("longitude_from")
    @Expose
    public String longitude_from = "";

    @SerializedName("address_from")
    @Expose
    public String address_from = "";

    @SerializedName("user_id")
    @Expose
    public String user_id = "";

    @SerializedName("provider_id")
    @Expose
    public String provider_id = "";

    @SerializedName("id")
    @Expose
    public String id = "";


    @PrimaryKey
    int realmId = 1;
}
