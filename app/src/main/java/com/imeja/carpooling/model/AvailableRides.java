package com.imeja.carpooling.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AvailableRides extends RealmObject {

    @SerializedName("id")
    @Expose
    public String id = "";
    @SerializedName("provider_id")
    @Expose
    public String provider_id = "";
    @SerializedName("user_id")
    @Expose
    public String user_id = "";
    @SerializedName("address_from")
    @Expose
    public String address_from = "";
    @SerializedName("longitude_from")
    @Expose
    public String longitude_from = "";


    @SerializedName("address_to")
    @Expose
    public String address_to = "";

    @SerializedName("longitude_to")
    @Expose
    public String longitude_to = "";

    @SerializedName("latitude_to")
    @Expose
    public String latitude_to = "";

    @SerializedName("seats")
    @Expose
    public String seats = "";

    @SerializedName("ride_on")
    @Expose
    public String ride_on = "";

    @SerializedName("price")
    @Expose
    public String price = "";

    @SerializedName("status")
    @Expose
    public String status = "";

    @SerializedName("created_at")
    @Expose
    public String created_at = "";

    @SerializedName("updated_at")
    @Expose
    public String updated_at = "";

    @PrimaryKey
    int realmId = 1;
}
