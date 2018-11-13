package com.tizisolutions.boilerplate_code.data.network;

import com.google.gson.JsonElement;

import java.io.IOException;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by billionaire on 24/04/2017.
 *
 * It is an interface just like DbHelper but implemented by AppApiHelper.
 */

public interface ApiHelper {


    //For instance
    /*
    ApiHeader getApiHeader();
    Observable<LoginResponse> doGoogleLoginApiCall(LoginRequest.GoogleLoginRequest request);*/
    Response<JsonElement> reqRefreshAccessToken(String refreshToken) throws IOException;
    Observable<JsonElement> getRefreshAccessToken(String refreshToken);
}
