package com.imeja.carpooling.auth.phone;

import java.util.List;

public class CountryListBean extends BaseBean {
    private List<CountryBean> countries;

    public List<CountryBean> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryBean> countries) {
        this.countries = countries;
    }
}
