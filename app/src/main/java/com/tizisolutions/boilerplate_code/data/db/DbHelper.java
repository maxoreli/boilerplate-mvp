package com.tizisolutions.boilerplate_code.data.db;

import android.database.Observable;

import java.util.List;

/**
 * Created by billionaire on 23/04/2017.
 *
 * It is an interface implemented by the AppDbHelper and contains methods exposed to the application components.
 * This layer decouples any specific implementation of the DbHelper and hence makes AppDbHelper as plug and play unit
 */

public interface DbHelper {

    //by example
   /* Observable<Long> insertUser(final User user);
    Observable<List<User>> getAllUsers();
    Observable<List<Question>> getAllQuestions();*/
}
