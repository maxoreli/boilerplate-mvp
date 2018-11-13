package com.tizisolutions.boilerplate_code.data.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by billionaire on 02/06/2017.
 */

public class BaseResponse<T>  {

    @Expose
    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("message")
    private String  message;

    @Expose
    @SerializedName("data")
    private T data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
