package com.blm.hightide.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.events.RequestFilesCompleteEvent;
import com.blm.hightide.events.RequestFilesStartEvent;
import com.blm.hightide.fragments.WatchlistFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;

import java.net.UnknownHostException;
import java.util.List;

public class WatchlistActivity extends AbstractBaseActivity {

    private static final String TAG = WatchlistActivity.class.getSimpleName();

    private StockService service = new StockService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service.init(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public Fragment createFragment() {
        return WatchlistFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRequestData(RequestFilesStartEvent event) {

        Watchlist watchlist = event.getWatchlist();
        service.findSecurities(watchlist);
        service.requestDailyTicks(watchlist);

        EventBus.getDefault().post(new RequestFilesCompleteEvent(watchlist));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestCompleted(RequestFilesCompleteEvent event) {

        Watchlist watchlist = event.getWatchlist();
        List<Security> securities = watchlist.getSecurities();

        Log.i(TAG, "onRequestCompleted: downloaded: " + watchlist);
        Log.i(TAG, "onRequestCompleted: securities: " + securities);

        System.out.println("done, now what!");
    }

    @Subscribe
    public void error(SubscriberExceptionEvent event) {
        Throwable throwable = event.throwable;
        if (UnknownHostException.class.isAssignableFrom(throwable.getClass())) {
            // SHOW NO ROUTE TO INTERNET MESSAGE
        } else {
            throw new RuntimeException(throwable);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
