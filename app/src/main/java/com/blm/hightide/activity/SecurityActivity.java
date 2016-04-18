package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.fragments.SecurityFragment;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SecurityActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = SecurityActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, SecurityActivity.class);
        intent.putExtra(SECURITY_SYMBOL, symbol);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        String symbol = this.getIntent().getExtras().getString(SECURITY_SYMBOL);
        return SecurityFragment.newInstance(symbol);
    }

    /**
     * TODO Make study params a ui configurable via RPF
     * @param event the security load start
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onSecurityLoadStart(SecurityLoadStart event) {

        toast(R.string.chart_security);

        StockService service = this.getStockService();
        String symbol = event.getSymbol();
        service.findSecurity(symbol)
                .flatMap(security -> service.setStandardPriceData(security, true))
                .subscribe(security -> {

                    int lastN = 60;
                    int avgLen = 20;
                    LineData data = service.getPriceAndAverage(security, lastN, avgLen);

                    EventBus.getDefault().post(new LineDataAvailable(security, data));
                });
    }
}
