package com.tizisolutions.boilerplate_code.data.network;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.tizisolutions.boilerplate_code.data.network.commons.UtilsApiHelper;
import com.tizisolutions.boilerplate_code.data.network.model.ApiError;
import com.tizisolutions.boilerplate_code.data.prefs.AppPreferencesHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;
import com.tizisolutions.boilerplate_code.ui.base.BasePresenter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dimitrovskif.smartcache.BasicCaching;
import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by billionaire on 24/04/2017.
 *
 * It manages the network API calls and API data handling
 */


@Singleton
public class AppApiHelper implements ApiHelper {

    private static final Object BTAG = AppApiHelper.class.getSimpleName();
    private final Retrofit mRetrofit;
    private final Retrofit mRetrofitCache;
    private final BasicCaching cacheSytem;
    private final AppPreferencesHelper mPrefs;
    private final UtilsApiHelper mUtils;
    private Retrofit retrofitCache;
    private PreferencesHelper prefs;


    @Inject
    public AppApiHelper(Retrofit retrofit,
                        @Named("RetrofitSmartCache") Retrofit retrofitCache,
                        BasicCaching cacheSytem,
                        AppPreferencesHelper appPreferencesHelper) {
        this.mRetrofit = retrofit;
        this.mRetrofitCache = retrofitCache;
        this.cacheSytem = cacheSytem;
        this.mPrefs = appPreferencesHelper;
        this.mUtils = new UtilsApiHelper(this);
    }

    public PreferencesHelper getPrefs() {
        return prefs;
    }

    @Override
    public Response<JsonElement> reqRefreshAccessToken(String refreshToken) throws IOException {
        return null;
    }

    @Override
    public Observable<JsonElement> getRefreshAccessToken(String refreshToken) {
        return null;
    }




    //example with POST with  File
    /*@Override
    public void reqUpdatePhoto(String userID, File imgFile, final BasePresenter.ApiListener<JsonPrimitive, ApiError> apiListener) {
        // create part for file (photo, video, ...)
        MultipartBody.Part body = prepareFilePart("photo", imgFile);

        // create a map of data to pass along
        RequestBody userIDB = createPartFromString(userID);

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("userID", userIDB);


        Call<BaseResponse<JsonPrimitive>> call = mRetrofit.
                create(RestClient.class).
                reqUpdatePhoto(map, body);

        mUtils.enqueueWithRetry(call, new Callback<BaseResponse<JsonPrimitive>>() {
            @Override
            public void onResponse(Call<BaseResponse<JsonPrimitive>> call, Response<BaseResponse<JsonPrimitive>> response) {

                if (response.isSuccessful()) {

                    BaseResponse<JsonPrimitive> baseResp = response.body();
                    apiListener.onSuccess(baseResp.getData());

                } else {
                    // error response, no access to resource?
                    // parse the response body …
                    ApiError error = ApiError.parseError(response, mRetrofit);
                    Timber.e(error);
                    error.setErrorCode(response.code());
                    apiListener.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<JsonPrimitive>> call, Throwable t) {

                // something went completely south (like no internet connection)
                //Log.d("Error", t.getMessage());
                Timber.e(t, "error");

                apiListener.onError(new ApiError(t.getMessage(), ApiError.UNKNOW, t));

            }
        });
    }*/


    //example without smarcache - GET
   /*
   @Override
    public void reqListProfils(String fromUserID, final BasePresenter.ApiListener<List<Person>, ApiError> apiListener) {

        Call<BaseResponse<List<Person>>> call = mRetrofit.
                create(RestClient.class).
                reqListProfils(fromUserID);

        mUtils.enqueueWithRetry(call, new Callback<BaseResponse<List<Person>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Person>>> call, Response<BaseResponse<List<Person>>> response) {

                if (response.isSuccessful()) {

                    BaseResponse<List<Person>> baseResp = response.body();
                    if (baseResp != null)
                        apiListener.onSuccess(baseResp.getData());
                    else
                        apiListener.onError(new ApiError("", new NullPointerException("response is null")));

                } else {
                    // error response, no access to resource?
                    // parse the response body …
                    ApiError error = ApiError.parseError(response, mRetrofit);
                    Timber.e(error);
                    error.setErrorCode(response.code());
                    apiListener.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Person>>> call, Throwable t) {

                // something went completely south (like no internet connection)
                //Log.d("Error", Log.getStackTraceString(t));
                Timber.e(t, "error");

                apiListener.onError(new ApiError(t.getMessage(), ApiError.UNKNOW, t));

            }
        });

    }
*/

    //example wit SmarCache
    /*
    public void reqListNotifications(String id, int page, int limit, final boolean cache, final BasePresenter.CacheApiListener apiListener) {

        final boolean refresh = !cache;
        SmartCall<BaseResponse<List<MPushNotification>>> smartCall =
                mRetrofitCache.create(RestClient.class).reqListNotificationsCache(id, page, limit);

        final byte[] bytes = cacheSytem.getFromCache(smartCall.buildRequest());
        if (bytes != null) {
            Timber.w(" %s Cache is not null", bytes);
        } else {
            Timber.w(" %s Cache is null", bytes);
        }
        mUtils.enqueueWithRetry(smartCall,new Callback<BaseResponse<List<MPushNotification>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<MPushNotification>>> call, Response<BaseResponse<List<MPushNotification>>> response) {

                if (response.isSuccessful()) {
                    getSmartCacheOrRefreshResponse(response, apiListener, bytes, refresh);

                } else {
                    // error response, no access to resource?
                    // parse the response body…
                    ApiError error = ApiError.parseError(response, mRetrofitCache);
                    Timber.e(error);
                    error.setErrorCode(response.code());
                    apiListener.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<MPushNotification>>> call, Throwable t) {

                // something went completely south (like no internet connection)
                //Log.d("Error", Log.getStackTraceString(t));
                Timber.e(t, "error");
                apiListener.onError(new ApiError(Log.getStackTraceString(t), ApiError.UNKNOW, t));
            }
        });

    } */


    //must implement all methods from ApiHelper
  /*  @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public Observable<LoginResponse> doGoogleLoginApiCall(LoginRequest.GoogleLoginRequest
                                                                  request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_GOOGLE_LOGIN)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(LoginResponse.class);
    }*/

    //@NonNull
    private MultipartBody.Part prepareFilePart(String partName, File file) {

        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        // create RequestBody instance from file

        if (file != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            // MultipartBody.Part is used to send also the actual file name
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } else
            return null;

    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

    private <T> void getSmartCacheOrRefreshResponse(Response<BaseResponse<T>> response,
                                                    BasePresenter.CacheApiListener apiListener,
                                                    byte[] bytes, boolean refresh) {

        HttpUrl url = response.raw().request().url();
        boolean isFromCache = url.host().equalsIgnoreCase("localhost");
        BaseResponse<T> baseResp = response.body();

        if (baseResp != null) {
            if (bytes == null && !isFromCache) {
                Timber.w("%s Cache is not here, it is the first time", BTAG);
                apiListener.onSuccess(baseResp.getData(), isFromCache);
            }

            if (bytes != null) {

                if (isFromCache && !refresh) {
                    Timber.w("%s Cache is here,get it from localhost", BTAG);
                    apiListener.onSuccess(baseResp.getData(), isFromCache);
                } else if (!isFromCache && !refresh) {
                    Timber.w("%s Cache is here,but it is a refresh, no callback", BTAG);
                } else if (!isFromCache && refresh) {
                    Timber.w("%s Cache is here,but it is a refresh,", BTAG);
                    apiListener.onSuccess(baseResp.getData(), !isFromCache);
                }
            }
        } else {
            apiListener.onError(new ApiError("", new NullPointerException("response is null")));
        }
    }


}
