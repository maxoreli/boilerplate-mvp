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

package com.tizisolutions.boilerplate_code.data.prefs;


import com.tizisolutions.boilerplate_code.data.model.MSession;
import com.tizisolutions.boilerplate_code.data.model.User;

/**
 * Created by janisharali on 27/01/17.
 *
 *  Interface just like DbHelper but implemented by AppPreferenceHelper.
 */

public interface PreferencesHelper {
    void setCurrentUserLoggedIn(User object);
    User getCurrentUserLoggedIn();
    void removeCurrentUser();
    String getRegToken();
    void setRegTokenSent();
    boolean isTokenSent();

    void setFbRefreshToken(String token);
    void saveSession(MSession session);
    MSession getSession();

}
