package com.tizisolutions.boilerplate_code.data;

import android.content.Context;

import com.tizisolutions.boilerplate_code.data.db.DbHelper;
import com.tizisolutions.boilerplate_code.data.network.ApiHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;
import com.tizisolutions.boilerplate_code.di.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;

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

}
