package com.tizisolutions.boilerplate_code.data.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tizisolutions.boilerplate_code.data.model.User;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dimitrovskif.smartcache.SmartCall;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by billionaire on 26/08/2017.
 */


public interface RestClient {


    @FormUrlEncoded
    @POST("/")
    Call<BaseResponse<User>> reqLoginUser(@Field("countryCode") String dialCode,
                                          @Field("phone") String phone,
                                          @Field("password") String password);
    @Multipart
    @POST("/account/createAccountByPhone")
    Call<BaseResponse<User>> reqCreateAccountByPhone(@PartMap() Map<String, RequestBody> partMap,
                                                            @Part MultipartBody.Part file);

    @GET("/user/list")
    Call<BaseResponse<List<User>>> reqListProfils(@Query("fromUserID") String fromUserID);
    @GET("/user/list")
    Observable<BaseResponse<List<User>>> reqListUsers(@Query("fromUserID") String fromUserID);
    @POST("/")
    Call<BaseResponse<User>> reqUpdateRegisterInfos(@Body User req);
    @Multipart
    @POST("/user/")
    Call<BaseResponse<User>> reqSaveAds(@PartMap() HashMap<String, RequestBody> map, @Part List<MultipartBody.Part> parts);
    @Multipart
    @POST("/user/updatePhoto")
    Call<BaseResponse<JsonPrimitive>> reqUpdatePhoto(@PartMap() HashMap<String, RequestBody> map, @Part MultipartBody.Part body);

    @FormUrlEncoded
    @POST("/auth/token")
    Observable<JsonElement> getRefreshAccessToken(@Field("refreshToken") String refreshToken);
}