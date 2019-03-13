package com.tizisolutions.smartcacherxjava2calladapter;

import okhttp3.Request;
import retrofit2.Response;

public interface CachingSystem {
    <T> void addInCache(Response<T> var1, byte[] var2);
    <T> byte[] getFromCache(Request var1);
}
