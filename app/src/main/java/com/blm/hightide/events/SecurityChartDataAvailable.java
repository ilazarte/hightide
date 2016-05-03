package com.blm.hightide.events;

import com.blm.hightide.model.Security;
import com.blm.hightide.model.StudyParams;
import com.github.mikephil.charting.data.CombinedData;

public class SecurityChartDataAvailable {

    private Security security;

    private CombinedData combinedData;

    private StudyParams params;

    public SecurityChartDataAvailable(Security security, CombinedData candleData) {
        this.security = security;
        this.combinedData = candleData;
    }


    public SecurityChartDataAvailable(Security security, CombinedData data, StudyParams params) {
        this.security = security;
        this.combinedData = data;
        this.params = params;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public CombinedData getCombinedData() {
        return combinedData;
    }

    public void setCombinedData(CombinedData combinedData) {
        this.combinedData = combinedData;
    }

    public StudyParams getParams() {
        return params;
    }

    public void setParams(StudyParams params) {
        this.params = params;
    }
}
