package com.imeja.carpooling.auth.phone;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class CountryBean extends BaseBean implements Comparable<CountryBean> {

    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("dial_code")
    private String dialCode;
    @SerializedName("code")
    private String countryCode;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public int compareTo(@NonNull CountryBean bean) {
        int comparison = dialCode.compareTo(bean.getDialCode());
        if (comparison == 0) {
            return 0;
        } else if (comparison > 0) {
            return 1;
        } else
            return -1;
    }
}
