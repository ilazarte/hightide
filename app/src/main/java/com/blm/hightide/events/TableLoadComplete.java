package com.blm.hightide.events;

import com.blm.hightide.model.AggType;
import com.blm.hightide.model.Security;

public class TableLoadComplete {

    private Security security;

    private AggType aggType;

    public TableLoadComplete(Security security, AggType aggType) {
        this.security = security;
        this.aggType = aggType;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public AggType getAggType() {
        return aggType;
    }

    public void setAggType(AggType aggType) {
        this.aggType = aggType;
    }
}
