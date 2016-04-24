package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.fragments.SecurityFragment;
import com.blm.hightide.model.MovingAvgParams;
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
        return SecurityFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        String symbol = this.getIntent().getExtras().getString(SECURITY_SYMBOL);
        onSecurityLoadStart(new SecurityLoadStart(symbol, new MovingAvgParams()));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onSecurityLoadStart(SecurityLoadStart event) {

        toast(R.string.chart_security);

        StockService service = this.getStockService();
        String symbol = event.getSymbol();
        service.findSecurity(symbol)
                .flatMap(security -> service.setStandardPriceData(security, true))
                .subscribe(security -> {

                    MovingAvgParams params = event.getParams();
                    LineData data = service.getPriceAndAverage(security, params);

                    EventBus.getDefault().post(new LineDataAvailable(security, data, params));
                });
    }
}
