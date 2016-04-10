package com.blm.hightide.events;

import com.blm.hightide.model.Security;

public class SecurityLoadComplete {

    private Security security;

    public SecurityLoadComplete(Security security) {
        this.security = security;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }
}
