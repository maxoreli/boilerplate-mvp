package com.tizisolutions.boilerplate_code.data.network.commons;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tizisolutions.boilerplate_code.data.model.MSession;
import com.tizisolutions.boilerplate_code.data.network.AppApiHelper;

import dimitrovskif.smartcache.SmartCall;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class UtilsApiHelper {

    private final AppApiHelper mAppApiHelper;

    public UtilsApiHelper(AppApiHelper appApiHelper) {
        this.mAppApiHelper = appApiHelper;
    }

    public <T> void enqueueWithRetry(Call<T> call, final Callback<T> callback) {
        call.enqueue(new RetryableCallback<T>(call) {

            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                Log.d("APIHelper", "reached onFinalResponse");
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                Log.d("APIHelper", "reached onFinalFailure");
                callback.onFailure(call, t);
            }
        });
    }

    public <T> void enqueueWithRetry(SmartCall<T> smartCall, Callback<T> callback) {
        smartCall.enqueue(new RetryableCallback<T>(smartCall) {

            @Override
            public void onFinalResponse(Call<T> call, Response<T> response) {
                Log.d("APIHelper", "reached onFinalResponse");
                callback.onResponse(call, response);
            }

            @Override
            public void onFinalFailure(Call<T> call, Throwable t) {
                Log.d("APIHelper", "reached onFinalFailure");
                callback.onFailure(call, t);
            }
        });
    }



    public ObservableSource<?> retryOnErrorAuth(Observable<Throwable> errors, Observable<?> source) {
        // For IOExceptions, we  retry
        return errors.flatMap(error -> {
            Timber.e(error);
            if (error instanceof HttpException) {
                HttpException e = (HttpException) error;
                if (e.code() == 401 || e.code() == 403) {

                    //refresh token
                    MSession session = mAppApiHelper.getPrefs().getSession();
                    Response<JsonElement> res = mAppApiHelper.reqRefreshAccessToken(session.getRefreshToken());
                    if (res.isSuccessful()) {
                        JsonObject jsonObject = res.body().getAsJsonObject();
                        JsonObject data = jsonObject.getAsJsonObject("data");
                        String accessToken = data.get("accessToken").getAsString();
                        String refreshToken = data.get("refreshToken").getAsString();
                        String fbAuthToken = data.get("fbAuthToken").getAsString();

                        session.setAccessToken(accessToken);
                        session.setRefreshToken(refreshToken);
                        session.setFbAuthToken(fbAuthToken);
                        session.setExpired(false);
                        mAppApiHelper.getPrefs().saveSession(session);
                        Timber.w("Session refreshed");
                        return source;
                    } else {
                        Timber.e("from api %s", res.errorBody().bytes().toString());
                        if (res.code() == 403) {
                            session.setExpired(true);
                            mAppApiHelper.getPrefs().saveSession(session);
                        }
                    }
                }
            }
            // For anything else, don't retry
            return Observable.error(error);
        });
    }



    public abstract class RetryableCallback<T> implements Callback<T> {

        private final String TAG = RetryableCallback.class.getSimpleName();
        private final Call<T> call;

        public RetryableCallback(Call<T> call) {
            this.call = call;
        }

        public RetryableCallback(SmartCall<T> smartCall) {
            this.call = smartCall.clone();
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (!isCallSuccess(response)) {
                if (response.code() == 403 || response.code() == 401) {
                    Log.d("RetryableCallback", "reached 403 or 401, check condition and retry if needed");
                    MSession session = mAppApiHelper.getPrefs().getSession();
                    mAppApiHelper.getRefreshAccessToken(mAppApiHelper.getPrefs().getSession().getRefreshToken())
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .blockingSubscribe(res -> {
                                JsonObject jsonObject = res.getAsJsonObject();
                                JsonObject data = jsonObject.getAsJsonObject("data");
                                String accessToken = data.get("accessToken").getAsString();
                                String refreshToken = data.get("refreshToken").getAsString();
                                String fbAuthToken = data.get("fbAuthToken").getAsString();

                                session.setAccessToken(accessToken);
                                session.setRefreshToken(refreshToken);
                                session.setFbAuthToken(fbAuthToken);
                                session.setExpired(false);
                                mAppApiHelper.getPrefs().saveSession(session);
                                Timber.w("Session refreshed");
                                retryCall(); // if the response succeeded retry the original call

                            }, throwable -> {

                                if (throwable instanceof HttpException) {
                                    HttpException e = (HttpException) throwable;
                                    if (e.code() == 403) {
                                        session.setExpired(true);
                                        mAppApiHelper.getPrefs().saveSession(session);
                                        return;
                                    }
                                }
                                onFinalFailure(call, throwable);
                            });
                }
            } else {
                onFinalResponse(call, response); // no need to do any retry,
                // pass the response and the call to the final callback
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            Timber.d("RetryableCallback %s", t);
            onFinalFailure(call, t);
           /* if (t.getMessage().contains("Server")) { // if error contains some keyword, retry the request as well. This is just an example to show you can call retry from either success or failure.
                retryCall();
            } else
           */ // if not, finish the call as a failure
        }

        public void onFinalResponse(Call<T> call, Response<T> response) { // to be overriden by calling class
        }

        public void onFinalFailure(Call<T> call, Throwable t) { // to be overriden by calling class
        }

        private void retryCall() {
            call.clone().enqueue(this); // clone the original call and enqueue it for retry
        }

    }

    public static boolean isCallSuccess(Response response) {
        int code = response.code();
        return (code >= 200 && code < 400);
    }

}
