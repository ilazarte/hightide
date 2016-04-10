package com.blm.hightide.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

@DatabaseTable(tableName = "watchlist")
public class Watchlist {

    public static final String ID = "id";

    @DatabaseField(generatedId = true, columnName = ID)
    private Integer id;

    @DatabaseField
    private String name;

    private List<Security> securities;

    /**
     * Required for ORMLite
     */
    @SuppressWarnings("unused")
    public Watchlist() {
    }

    public Watchlist(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public void setSecurities(List<Security> securities) {
        this.securities = securities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Watchlist watchlist = (Watchlist) o;

        return !(id != null ? !id.equals(watchlist.id) : watchlist.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
