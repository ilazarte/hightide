package com.blm.hightide.events;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;

public class LoadSecurityStartEvent {

    private Security security;

    public LoadSecurityStartEvent(Security security) {
        this.security = security;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }
}
