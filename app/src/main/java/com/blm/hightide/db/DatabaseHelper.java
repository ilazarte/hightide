package com.blm.hightide.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.model.WatchlistSecurity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String[] MAIN_WATCHLIST = new String[] {
            "AAPL", "AMZN", "BAC", "BIDU", "CAT",
            "DIA", "EEM", "EWZ", "FCX", "FFIV",
            "FSLR", "GLD", "GOOG", "GS", "IBM",
            "IWM", "JPM", "KO", "MCD", "NFLX",
            "PG", "PM", "QCOM", "QQQ", "SLV",
            "SPY", "USO", "WMT", "XOM"
    };

    private static final String[] INDEX_WATCHLIST = new String[] {
            "XLK","XLF","XLP","XLE","XLI",
            "XLV","XLY","XLU","XLB","GLD",
            "SLV","SPY","IWM","DIA","EWZ",
            "USO","EEM","QQQ","TLT"
    };

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        super(context, DatabaseHelper.class.getSimpleName(), null, 1);
    }

    public <T, U> RuntimeExceptionDao<T, U> getDaoByKey(Class<T> clazz, Class<U> typeClass) {
        RuntimeExceptionDao<T, U> dao = (RuntimeExceptionDao<T, U>) getRuntimeExceptionDao(clazz);
        return dao;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "Creating tables");
            TableUtils.createTable(connectionSource, Watchlist.class);
            TableUtils.createTable(connectionSource, Security.class);
            TableUtils.createTable(connectionSource, WatchlistSecurity.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }

        final RuntimeExceptionDao<Security, String> securityDao = getDaoByKey(Security.class, String.class);
        securityDao.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (String symbol : MAIN_WATCHLIST) {
                    Security security = new Security(symbol);
                    if (!securityDao.idExists(symbol)) {
                        securityDao.create(security);
                    }
                }

                for (String symbol : INDEX_WATCHLIST) {
                    Security security = new Security(symbol);
                    if (!securityDao.idExists(symbol)) {
                        securityDao.create(security);
                    }
                }
                return null;
            }
        });

        RuntimeExceptionDao<Watchlist, Integer> watchlistDao = getDaoByKey(Watchlist.class, Integer.class);
        final Watchlist mainWatchlist = new Watchlist("Main");
        final Watchlist indexWatchlist = new Watchlist("Index");

        watchlistDao.create(mainWatchlist);
        watchlistDao.create(indexWatchlist);


        final RuntimeExceptionDao<WatchlistSecurity, Integer> watchlistSecurityDao = getDaoByKey(WatchlistSecurity.class, Integer.class);
        watchlistSecurityDao.callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (String symbol : MAIN_WATCHLIST) {
                    WatchlistSecurity watchlistSecurity = new WatchlistSecurity(mainWatchlist, new Security(symbol));
                    watchlistSecurityDao.create(watchlistSecurity);
                }

                for (String symbol : INDEX_WATCHLIST) {
                    WatchlistSecurity watchlistSecurity = new WatchlistSecurity(indexWatchlist, new Security(symbol));
                    watchlistSecurityDao.create(watchlistSecurity);
                }
                return null;
            }
        });
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "Upgrade");
            TableUtils.dropTable(connectionSource, Security.class, true);
            TableUtils.dropTable(connectionSource, Watchlist.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public List<Watchlist> findAllWatchlists() {
        RuntimeExceptionDao<Watchlist, Integer> watchlistDao = getDaoByKey(Watchlist.class, Integer.class);
        return watchlistDao.queryForAll();
    }

    public Watchlist findWatchlist(int id) {
        RuntimeExceptionDao<Watchlist, Integer> watchlistDao = getDaoByKey(Watchlist.class, Integer.class);
        return watchlistDao.queryForId(id);
    }

    /**
     * Magic?!?
     * @param watchlist the watchlist to load securities for
     * @return list of securities
     */
    public List<Security> findSecuritiesByWatchlist(Watchlist watchlist) {

        RuntimeExceptionDao<WatchlistSecurity, Integer> watchlistSecurityDao = getDaoByKey(WatchlistSecurity.class, Integer.class);
        RuntimeExceptionDao<Security, String> securityDao = getDaoByKey(Security.class, String.class);

        try {

            QueryBuilder<WatchlistSecurity, Integer> inner = watchlistSecurityDao.queryBuilder();
            SelectArg watchlistSelectArg = new SelectArg();

            inner.selectColumns(WatchlistSecurity.SECURITY_SYMBOL);
            inner.where().eq(WatchlistSecurity.WATCHLIST_ID, watchlistSelectArg);

            QueryBuilder<Security, String> outer = securityDao.queryBuilder();
            outer.where().in(Security.SYMBOL, inner);

            PreparedQuery<Security> query = outer.prepare();
            query.setArgumentHolderValue(0, watchlist);

            return securityDao.query(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
