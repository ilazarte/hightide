package com.blm.hightide.util;

import com.blm.corals.PriceData;

import java.util.Date;

public class StandardPriceData extends PriceData {

    private Date date;

    public StandardPriceData(PriceData priceData, Date date) {
        this.setTicks(priceData.getTicks());
        this.setErrors(priceData.getErrors());
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
