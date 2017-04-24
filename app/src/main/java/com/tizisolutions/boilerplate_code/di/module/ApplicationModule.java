/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.tizisolutions.boilerplate_code.di.module;

import android.app.Application;
import android.content.Context;


import com.tizisolutions.boilerplate_code.BuildConfig;
import com.tizisolutions.boilerplate_code.R;
import com.tizisolutions.boilerplate_code.data.AppDataManager;
import com.tizisolutions.boilerplate_code.data.DataManager;
import com.tizisolutions.boilerplate_code.data.db.AppDbHelper;
import com.tizisolutions.boilerplate_code.data.db.DbHelper;
import com.tizisolutions.boilerplate_code.data.network.ApiHeader;
import com.tizisolutions.boilerplate_code.data.network.ApiHelper;
import com.tizisolutions.boilerplate_code.data.network.AppApiHelper;
import com.tizisolutions.boilerplate_code.data.prefs.AppPreferencesHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;
import com.tizisolutions.boilerplate_code.di.ApiInfo;
import com.tizisolutions.boilerplate_code.di.ApplicationContext;
import com.tizisolutions.boilerplate_code.di.DatabaseInfo;
import com.tizisolutions.boilerplate_code.di.PreferenceInfo;
import com.tizisolutions.boilerplate_code.utils.AppConstants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by janisharali on 27/01/17.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @ApiInfo
    String provideApiKey() {
        return BuildConfig.API_KEY;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }


    @Provides
    @Singleton
    CalligraphyConfig provideCalligraphyDefaultConfig() {
        return new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/source-sans-pro/SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build();
    }
}
