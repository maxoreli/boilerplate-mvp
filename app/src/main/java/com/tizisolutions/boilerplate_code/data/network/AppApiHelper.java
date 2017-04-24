package com.tizisolutions.boilerplate_code.data.network;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by billionaire on 24/04/2017.
 *
 * It manages the network API calls and API data handling
 */


@Singleton
public class AppApiHelper implements ApiHelper {

    @Inject
    public AppApiHelper() {
    }


    //must implement all methods from ApiHelper
  /*  @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public Observable<LoginResponse> doGoogleLoginApiCall(LoginRequest.GoogleLoginRequest
                                                                  request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_GOOGLE_LOGIN)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(LoginResponse.class);
    }*/
}
