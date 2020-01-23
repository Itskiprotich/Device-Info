package com.imeja.carpooling.searching.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MembersModel  {
    @SerializedName("address_from")
    @Expose
    public String Name;
    @SerializedName("address_to")
    @Expose
    public String Phone;
    @SerializedName("price")
    @Expose
    public String Amount;

}
