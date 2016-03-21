package com.blm.hightide;

import android.app.Application;
import android.database.sqlite.SQLiteQueryBuilder;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testQuery() {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables("customers");
        String query = builder.toString();

        assertNotNull(query);
        System.out.println(query);
    }
}