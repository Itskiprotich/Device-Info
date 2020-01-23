package com.imeja.carpooling.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import com.imeja.carpooling.model.LoginDetails;

public interface ApiService {

    @POST
    Call<LoginResponse> loginUser(@Url String loginUrl, @Body LoginDetails loginDetails);

}