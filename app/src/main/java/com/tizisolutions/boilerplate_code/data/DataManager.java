package com.tizisolutions.boilerplate_code.data;

import com.tizisolutions.boilerplate_code.data.db.DbHelper;
import com.tizisolutions.boilerplate_code.data.network.ApiHelper;
import com.tizisolutions.boilerplate_code.data.prefs.PreferencesHelper;

/**
 * Created by billionaire on 24/04/2017.
 *
 * It is an interface that is implemented by the AppDataManager.
 * It contains methods, exposed for all the data handling operations.
 * Ideally, it delegates the services provided by all the Helper classes.
 * For this DataManager interface extends DbHelper, PreferenceHelper and ApiHelper interfaces
 */

public interface DataManager extends DbHelper, PreferencesHelper, ApiHelper {


}
