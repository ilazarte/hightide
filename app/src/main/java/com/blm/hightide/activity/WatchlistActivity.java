package com.blm.hightide.activity;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.activity.internal.AbstractBaseActivity;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.events.WatchlistFilesRequestComplete;
import com.blm.hightide.events.WatchlistFilesRequestStart;
import com.blm.hightide.fragments.WatchlistFragment;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WatchlistActivity extends AbstractBaseActivity {

    private static final String TAG = WatchlistActivity.class.getSimpleName();

    @Override
    public Fragment createFragment() {
        return WatchlistFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        onWatchlistFilesRequestStart(new WatchlistFilesRequestStart(-1, true));
    }
    
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onWatchlistFilesRequestStart(WatchlistFilesRequestStart event) {

        StockService service = this.getStockService();
        toast(R.string.read_files);

        int watchlistId = event.getWatchlistId();
        boolean readRequest = event.isReadRequest();
        service.findWatchlists()
                .concatMap(wls ->
                        service.findWatchlist(watchlistId, wls, true)
                                .concatMap(wl -> service.setWatchlistPriceData(wl, readRequest))
                                .map(wl -> new WatchlistFilesRequestComplete(wls, wl)))
                .subscribe(complete -> {
                    EventBus.getDefault().post(complete);
                }, error -> {
                    Log.e(TAG, "onWatchlistFilesRequestStart: ", error);
                });
    }
}
