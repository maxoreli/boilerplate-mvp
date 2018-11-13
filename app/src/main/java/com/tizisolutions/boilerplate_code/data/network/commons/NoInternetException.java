package com.tizisolutions.boilerplate_code.data.network.commons;

import java.io.IOException;

/**
 * Created by billionaire on 5/8/18.
 */

public class NoInternetException extends IOException {

    @Override
    public String getMessage() {
        return "No connectivity exception";
    }

}
