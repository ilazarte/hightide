package com.blm.hightide.events;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.TickType;

public class TableLoadComplete {

    private Security security;

    private TickType tickType;

    public TableLoadComplete(Security security, TickType tickType) {
        this.security = security;
        this.tickType = tickType;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public TickType getTickType() {
        return tickType;
    }

    public void setTickType(TickType tickType) {
        this.tickType = tickType;
    }
}
