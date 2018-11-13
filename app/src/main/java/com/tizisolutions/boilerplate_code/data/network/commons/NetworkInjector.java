package com.tizisolutions.boilerplate_code.data.network.commons;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;


import com.tizisolutions.boilerplate_code.BuildConfig;
import com.tizisolutions.boilerplate_code.MyApp;
import com.tizisolutions.boilerplate_code.data.DataManager;
import com.tizisolutions.boilerplate_code.data.model.MSession;
import com.tizisolutions.boilerplate_code.data.network.commons.InternetConnectionListener;
import com.tizisolutions.boilerplate_code.data.network.commons.NetworkConnectionInterceptor;
import com.tizisolutions.boilerplate_code.utils.AppConstants;
import com.tizisolutions.boilerplate_code.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created by billionaire on 5/8/18.
 */

public class NetworkInjector {

    private static final String CACHE_CONTROL = "Cache-Control";
    private final Application mApp;
    private InternetConnectionListener mInternetConnectionListener;


    @Inject
    public NetworkInjector(Application application) {

        this.mApp = application;

        //this.mInternetConnectionListener = mApp.getI;
    }

    public Retrofit provideRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();
    }

    public Cache provideCache() {

        /*int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(MyApp.getContext().getCacheDir(), cacheSize);*/
        Cache cache = null;
        try {
            cache = new Cache(new File(mApp.getCacheDir(), "http-cache"), 10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Timber.e(e, "Could not create Cache!");
        }
        return cache;
    }

    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    public HttpLoggingInterceptor provideHttpBodyLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                });
        httpLoggingInterceptor.setLevel(
                BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : NONE
        );
        //.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }


    public Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!NetworkUtils.isNetworkConnected(mApp)) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }
                return chain.proceed(request);
            }
        };
    }

    public Interceptor provideConnectionInterceptor() {
        return new NetworkConnectionInterceptor(mApp);

    }

    public Interceptor provideTokenExpiredInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                DataManager mDataManager = ((MyApp) mApp).getComponent().getDataManager();
                MSession session = mDataManager.getSession();

                //if session is expired , cancel request and broadcast session expired event
                if (session != null && session.isExpired() && mDataManager.getCurrentUserLoggedIn() != null) {
                    chain.call().cancel();
                    Intent in = new Intent(AppConstants.INTENT_ACTION_LOGOUT);
                    LocalBroadcastManager.getInstance(mApp).sendBroadcast(in);
                    //return null;
                }

                //if it is refresh token and it is ok ? update session
                if (session != null && original.url().encodedPath().equalsIgnoreCase("/auth/token")) {

                    Response newTokenRes = chain.proceed(original);
                    if (newTokenRes.code() == 200) {
                        // save the new token
                        try {
                            String body = newTokenRes.body().string();

                            Timber.e("body string: %s", body);

                            JSONObject jsonObject = new JSONObject(body);
                            JSONObject data = jsonObject.getJSONObject("data");
                            String accessToken = data.getString("accessToken");
                            String refreshToken = data.getString("refreshToken");
                            String fbAuthToken = data.getString("fbAuthToken");
                            session.setAccessToken(accessToken);
                            session.setRefreshToken(refreshToken);
                            session.setFbAuthToken(fbAuthToken);
                            session.setExpired(false);
                            mDataManager.saveSession(session);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {//refresh token expired logout user
                        //Intent in = new Intent(AppConstants.INTENT_ACTION_LOGOUT);
                        //LocalBroadcastManager.getInstance(mApp).sendBroadcast(in);
                        session.setExpired(true);
                        mDataManager.saveSession(session);
                        chain.call().cancel();
                    }
                }


                //if session is null or is refresh token request ? dont add headers
                if (session != null && !original.url().encodedPath().equalsIgnoreCase("/auth/token")) {
                    Request mainRequest = original.newBuilder()
                            .header("Authorization", "Bearer " + session.getAccessToken())
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(mainRequest);
                }


                return chain.proceed(original);
            }
        };
    }

    public Interceptor provideAuthorizationInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Response mainResponse = chain.proceed(original);

                DataManager mDataManager = ((MyApp) mApp).getComponent().getDataManager();

                // if response code is 401 or 403, 'mainRequest' has encountered authentication error
                if (mainResponse.code() == 401 || mainResponse.code() == 403) {
                    mDataManager.reqRefreshAccessToken(mDataManager.getSession().getRefreshToken());
                }
                return mainResponse;
            }
        };
    }


    public Interceptor provideAddHeaderInterceptor() {

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Response mainResponse = chain.proceed(original);

                DataManager mDataManager = ((MyApp) mApp).getComponent().getDataManager();
                MSession session = mDataManager.getSession();

                if (session != null && session.isExpired() && mDataManager.getCurrentUserLoggedIn() != null) {
                    chain.call().cancel();
                    Intent in = new Intent(AppConstants.INTENT_ACTION_LOGOUT);
                    LocalBroadcastManager.getInstance(mApp).sendBroadcast(in);
                }

                if (session != null) {
                    Request mainRequest = original.newBuilder()
                            .header("Authorization", "Bearer " + session.getAccessToken())
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(mainRequest);
                }

                return  mainResponse;
            }
        };
    }

   /* public static RestClient provideRESTService() {
        return provideRetrofit(BuildConfig.BASE_URL).create(RestClient.class);
    }*/

}
