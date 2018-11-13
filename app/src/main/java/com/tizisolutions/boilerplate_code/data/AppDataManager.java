package com.tizisolutions.boilerplate_code.data;

import android.content.Context;

import com.google.gson.JsonElement;
import com.tizisolutions.boilerplate_code.data.db.DbHelper;
import com.tizisolutions.boilerplate_code.data.model.MSession;
import com.tizisolutions.boilerplate_code.data.model.User;
import com.tizisolutions.boilerplate_code.data.network.ApiHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;
import com.tizisolutions.boilerplate_code.di.ApplicationContext;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import retrofit2.Response;

/**
 * Created by billionaire on 24/04/2017.
 *
 * It is the one point of contact for any data related operation in the application.
 * DbHelper, PreferenceHelper, and ApiHelper only work for DataManager.
 * It delegates all the operations specific to any Helper.
 */

@Singleton
public class AppDataManager implements DataManager {


    private final Context mContext;
    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          DbHelper dbHelper,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper) {
        mContext = context;
        mDbHelper = dbHelper;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
    }

    @Override
    public Response<JsonElement> reqRefreshAccessToken(String refreshToken) throws IOException {
        return null;
    }

    @Override
    public Observable<JsonElement> getRefreshAccessToken(String refreshToken) {
        return null;
    }

    @Override
    public void setCurrentUserLoggedIn(User object) {

    }

    @Override
    public User getCurrentUserLoggedIn() {
        return null;
    }

    @Override
    public void removeCurrentUser() {

    }

    @Override
    public String getRegToken() {
        return null;
    }

    @Override
    public void setRegTokenSent() {

    }

    @Override
    public boolean isTokenSent() {
        return false;
    }

    @Override
    public void setFbRefreshToken(String token) {

    }

    @Override
    public void saveSession(MSession session) {

    }

    @Override
    public MSession getSession() {
        return null;
    }
}
