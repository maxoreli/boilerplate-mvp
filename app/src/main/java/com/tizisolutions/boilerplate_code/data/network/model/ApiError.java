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

package com.tizisolutions.boilerplate_code.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by janisharali on 28/01/17.
 */

public class ApiError  extends Throwable {

    private int errorCode;

    @Expose
    @SerializedName("status_code")
    private String statusCode;

    @Expose
    @SerializedName("message")
    private String message;

    public ApiError(int errorCode, String statusCode, String message) {
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.message = message;
    }

    public ApiError(String message, int errorCode, Throwable throwable) {
        super(message,throwable);
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApiError(String message, Throwable throwable) {
        super(message, throwable);

    }

    public ApiError() {

    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ApiError apiError = (ApiError) object;

        if (errorCode != apiError.errorCode) return false;
        if (statusCode != null ? !statusCode.equals(apiError.statusCode) : apiError.statusCode != null)
            return false;
        return message != null ? message.equals(apiError.message) : apiError.message == null;

    }

    @Override
    public int hashCode() {
        int result = errorCode;
        result = 31 * result + (statusCode != null ? statusCode.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    public Throwable getThrowable() {
        return getCause();
    }

    public static ApiError parseError(Response<?> response, Retrofit mRetrofit) {

        ApiError error;
        Converter<ResponseBody, ApiError> converter =
                mRetrofit.
                        responseBodyConverter(ApiError.class, new Annotation[0]);
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            error =  new ApiError();
        }

        return error;
    }

}
