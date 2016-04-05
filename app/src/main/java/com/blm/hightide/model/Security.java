package com.blm.hightide.model;

import com.blm.corals.PriceData;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "security")
public class Security {

    public static final String SYMBOL = "symbol";

    @DatabaseField(id = true, columnName = SYMBOL)
    private String symbol;

    @DatabaseField
    private boolean enabled = true;

    private PriceData priceData;

    public Security() {
    }

    public Security(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PriceData getPriceData() {
        return priceData;
    }

    public void setPriceData(PriceData priceData) {
        this.priceData = priceData;
    }

    public String getDailyFilename() {
        return symbol + "_daily.csv";
    }

    public String getIntradayFilename() {
        return symbol + "_intraday.csv";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Security security = (Security) o;

        return !(symbol != null ? !symbol.equals(security.symbol) : security.symbol != null);

    }

    @Override
    public int hashCode() {
        return symbol != null ? symbol.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Security{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
