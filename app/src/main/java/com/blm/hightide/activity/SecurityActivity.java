package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.FilesNotificationEvent;
import com.blm.hightide.events.LineDataAvailableEvent;
import com.blm.hightide.events.LoadFilesCompleteEvent;
import com.blm.hightide.events.LoadFilesInitEvent;
import com.blm.hightide.events.LoadFilesStartEvent;
import com.blm.hightide.events.LoadSecurityInitEvent;
import com.blm.hightide.events.LoadSecurityStartEvent;
import com.blm.hightide.events.RequestFilesCompleteEvent;
import com.blm.hightide.events.RequestFilesInitEvent;
import com.blm.hightide.events.RequestFilesStartEvent;
import com.blm.hightide.fragments.RelativePerformanceFragment;
import com.blm.hightide.fragments.SecurityFragment;
import com.blm.hightide.fragments.WatchlistFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
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

    @Subscribe
    public void onLoadSecurityInitEvent(LoadSecurityInitEvent event) {

        String symbol = event.getSymbol();
        Security security = service.findSecurity(symbol);
        this.initProgressDialog(R.string.chart_security, 1);

        EventBus.getDefault().post(new LoadSecurityStartEvent(security));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLoadSecurity(LoadSecurityStartEvent event) {

        Security security = event.getSecurity();
        service.requestDailyTicks(security);

        /**
         * Make this a ui configurable via RPF
         */
        int lastN = 60;
        int avgLen = 20;
        int lastNTicks = lastN - avgLen;

        List<ILineDataSet> datasets = service.getPriceAndAverage(security, lastN, avgLen);
        List<String> xvals = service.toXAxis(security.getTicks(), lastNTicks);
        LineData data = new LineData(xvals, datasets);

        EventBus.getDefault().post(new LoadFilesCompleteEvent(new LineDataAvailableEvent(data)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadNotification(FilesNotificationEvent event) {
        this.notifyFileProgress(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadFilesCompleteEvent(LoadFilesCompleteEvent event) {
        this.completeFileProgress(R.string.chart_security_complete, event.getLineDataAvailableEvent());
    }

    @Subscribe
    public void error(SubscriberExceptionEvent event) {
        handleThrowable(TAG, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
