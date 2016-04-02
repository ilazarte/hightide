package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.fragments.SecurityFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.service.StockService;
import com.blm.hightide.util.YahooPriceHelper;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class SecurityActivity extends AbstractBaseActivity {

    private static final String TAG = SecurityActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    private StockService service = new StockService();

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, SecurityActivity.class);
        intent.putExtra(SECURITY_SYMBOL, symbol);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        service.init(this);
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

        String symbol = event.getSymbol();
        Security security = service.findSecurity(symbol);
        this.initProgressDialog(R.string.chart_security, 1);

        YahooPriceHelper helper = new YahooPriceHelper(this);
        String message = this.getString(R.string.chart_security_msg_fmt, security.getSymbol());
        this.notifyFileProgress(message, 1);

        List<String> lines = helper.daily(security.getSymbol());
        helper.write(lines, security.getDailyFilename());
        List<Tick> ticks = helper.readDaily(lines);
        security.setTicks(ticks);

        int lastN = 60;
        int avgLen = 20;
        int lastNTicks = lastN - avgLen;

        List<ILineDataSet> datasets = service.getPriceAndAverage(security, lastN, avgLen);
        List<String> xvals = service.toXAxis(security.getTicks(), lastNTicks);
        LineData data = new LineData(xvals, datasets);

        this.completeFileProgress(R.string.chart_security_complete, new LineDataAvailable(data));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
