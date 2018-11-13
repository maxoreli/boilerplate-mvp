package com.tizisolutions.boilerplate_code.data.network.commons;

import android.content.Context;
import com.tizisolutions.boilerplate_code.data.network.commons.NoInternetException;
import com.tizisolutions.boilerplate_code.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by billionaire on 5/7/18.
 */

public class NetworkConnectionInterceptor implements Interceptor {

    //public abstract boolean isInternetAvailable();
    //public abstract void onInternetUnavailable();

    private Context mContext;

    public NetworkConnectionInterceptor(Context context) {
        mContext = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
       // request.url()
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            throw new NoInternetException();
        }

        return chain.proceed(request);
    }

}
