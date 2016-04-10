package com.blm.hightide.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by perico on 3/15/2016.
 * https://github.com/j256/ormlite-jdbc/tree/master/src/test/java/com/j256/ormlite/examples/manytomany
 * http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_2.html#Foreign-Objects
 */
@DatabaseTable(tableName = "watchlist_security")
public class WatchlistSecurity {

    public static final String SECURITY_SYMBOL = "security_symbol";

    public static final String WATCHLIST_ID = "watchlist_id";

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(foreign = true, columnName = WATCHLIST_ID)
    private Watchlist watchlist;

    @DatabaseField(foreign = true, columnName = SECURITY_SYMBOL)
    private Security security;

    /**
     * Required for ORMLite
     */
    @SuppressWarnings("unused")
    public WatchlistSecurity() {
    }

    public WatchlistSecurity(Watchlist watchlist, Security security) {
        this.watchlist = watchlist;
        this.security = security;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Watchlist getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Watchlist watchlist) {
        this.watchlist = watchlist;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WatchlistSecurity that = (WatchlistSecurity) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "WatchlistSecurity{" +
                "id=" + id +
                ", watchlist=" + watchlist +
                ", security=" + security +
                '}';
    }
}
