package com.limecoders.quizapp.Util;

import com.limecoders.quizapp.ApiService.APIService;
import com.limecoders.quizapp.Retrofit.RetrofitClient;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://www.inventerit.com";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
