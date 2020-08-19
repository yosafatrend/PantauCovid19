package com.spect.pantaucovid19;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("indonesia")
    Call<List<Post>> getPosts();

    @GET("indonesia/provinsi")
    Call<List<Provinsi>> getProv();

}
