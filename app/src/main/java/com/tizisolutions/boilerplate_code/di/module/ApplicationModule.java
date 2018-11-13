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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tizisolutions.boilerplate_code.BuildConfig;
import com.tizisolutions.boilerplate_code.R;
import com.tizisolutions.boilerplate_code.data.AppDataManager;
import com.tizisolutions.boilerplate_code.data.DataManager;
import com.tizisolutions.boilerplate_code.data.db.AppDbHelper;
import com.tizisolutions.boilerplate_code.data.db.DbHelper;
import com.tizisolutions.boilerplate_code.data.network.ApiHelper;
import com.tizisolutions.boilerplate_code.data.network.AppApiHelper;
import com.tizisolutions.boilerplate_code.data.network.RestClient;
import com.tizisolutions.boilerplate_code.data.network.commons.NetworkInjector;
import com.tizisolutions.boilerplate_code.data.prefs.AppPreferencesHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;
import com.tizisolutions.boilerplate_code.di.ApiInfo;
import com.tizisolutions.boilerplate_code.di.ApplicationContext;
import com.tizisolutions.boilerplate_code.di.DatabaseInfo;
import com.tizisolutions.boilerplate_code.di.PreferenceInfo;
import com.tizisolutions.boilerplate_code.utils.AppConstants;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import dagger.Module;
import dagger.Provides;
import dimitrovskif.smartcache.BasicCaching;
import dimitrovskif.smartcache.SmartCallFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
    RestClient provideRestApiService(Retrofit retrofit) {
        return  retrofit.create(RestClient.class);
    }

    @Provides
    @Singleton
    BasicCaching provideCacheSystem(Context context) {
        return BasicCaching.fromCtx(context);
    }


    @Provides
    @Singleton
    @Named("RetrofitSmartCache")
    Retrofit provideRetrofitSmartCache(@Named("OkHttpCache") OkHttpClient okHttpClient,
                                       GsonConverterFactory gsonConverterFactory,
                                       BasicCaching cachingSystem) {
        //
        SmartCallFactory smartFactory = new SmartCallFactory(cachingSystem);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(smartFactory);// add this!

        Retrofit retroFit = builder.build();
        return retroFit;
    }


    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient, GsonConverterFactory gsonConverterFactory) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                //.callbackExecutor()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());


        Retrofit retroFit = builder.build();
        //okHttpClient.interceptors().
        return retroFit;
    }

    @Provides
    @Singleton
    GsonConverterFactory provideGsonFactory() {
        //
        Gson gson = new GsonBuilder()
                //.registerTypeAdapter(Id.class, new IdTypeAdapter())
                //.enableComplexMapKeySerialization()
                .serializeNulls()
                //.setExclusionStrategies()
                //.setDateFormat(DateFormat.LONG)
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                //.excludeFieldsWithoutExposeAnnotation()
                .create();
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    SSLContext getSSLConfig(Context context) {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        Certificate ca = null;
        // I'm using Java7. If you used Java6 close it manually with finally.
        InputStream cert = null;
        //cert = context.getResources().openRawResource(R.raw.certificate);
        try {
            ca = cf.generateCertificate(cert);
            ca.toString();
        } catch (CertificateException e) {
            e.printStackTrace();
        } finally {
            try {
                cert.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //
        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        try {
            keyStore.setCertificateEntry("ca", ca);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // Creating a KeyStore containing our trusted CAs
        /*String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);*/

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        //String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        //TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        //tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        //SSLContext sslContext = SSLContext.getInstance("TLS");
        //sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;

    }



    @Provides
    @Singleton
    @Named("OkHttpCache")
    OkHttpClient provideOkHttpClientCache(NetworkInjector injector,
                                          final Application application,
                                          SSLContext sslContext) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        /*if (provideCache) {
            okHttpClient.cache(injector.provideCache())
                    .addNetworkInterceptor(injector.provideCacheInterceptor())
                    .addInterceptor(injector.provideOfflineCacheInterceptor());
        }*/
        okHttpClient.addInterceptor(injector.provideHttpBodyLoggingInterceptor());
        okHttpClient.addInterceptor(injector.provideHttpLoggingInterceptor());
        okHttpClient.addInterceptor(injector.provideConnectionInterceptor());

        //okHttpClient.addInterceptor(injector.provideTokenExpiredInterceptor());
        //okHttpClient.addInterceptor(injector.provideAuthorizationInterceptor());
        okHttpClient.addInterceptor(injector.provideAddHeaderInterceptor());

        okHttpClient.connectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.readTimeout(30, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(30, TimeUnit.SECONDS);

        //
        okHttpClient.sslSocketFactory(sslContext.getSocketFactory());
        return okHttpClient.build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(NetworkInjector injector,
                                     final Application application,
                                     SSLContext sslContext) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        /*if (provideCache) {
            okHttpClient.cache(injector.provideCache())
                    .addNetworkInterceptor(injector.provideCacheInterceptor())
                    .addInterceptor(injector.provideOfflineCacheInterceptor());
        }*/
        okHttpClient.addInterceptor(injector.provideHttpBodyLoggingInterceptor());
        okHttpClient.addInterceptor(injector.provideHttpLoggingInterceptor());
        okHttpClient.addInterceptor(injector.provideConnectionInterceptor());

        //okHttpClient.addInterceptor(injector.provideTokenExpiredInterceptor());
        //okHttpClient.addInterceptor(injector.provideAuthorizationInterceptor());
        okHttpClient.addInterceptor(injector.provideAddHeaderInterceptor());


        okHttpClient.connectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.readTimeout(30, TimeUnit.SECONDS);
        okHttpClient.writeTimeout(30, TimeUnit.SECONDS);

        //
        okHttpClient.sslSocketFactory(sslContext.getSocketFactory());
        return okHttpClient.build();
    }


    @Provides
    @Singleton
    NetworkInjector provideInjector(Application application) {
        return new NetworkInjector(application);
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
