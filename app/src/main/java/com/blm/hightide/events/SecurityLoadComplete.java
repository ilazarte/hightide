package com.blm.hightide.events;

import com.blm.hightide.model.Security;

import java.util.List;

public class SecurityLoadComplete {

    private Security security;

    private List<String> columns;

    public SecurityLoadComplete(Security security, List<String> columns) {
        this.security = security;
        this.columns = columns;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
