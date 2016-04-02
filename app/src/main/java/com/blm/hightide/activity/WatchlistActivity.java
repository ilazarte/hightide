package com.blm.hightide.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.events.WatchlistFilesRequestComplete;
import com.blm.hightide.events.WatchlistFilesRequestStart;
import com.blm.hightide.fragments.WatchlistFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;
import com.blm.hightide.util.YahooPriceHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class WatchlistActivity extends AbstractBaseActivity {

    private static final String TAG = WatchlistActivity.class.getSimpleName();

    private StockService service = new StockService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service.init(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public Fragment createFragment() {
        return WatchlistFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onWatchlistFilesRequestStart(WatchlistFilesRequestStart event) {

        int watchlistId = event.getWatchlistId();
        List<Watchlist> watchlists = service.findAllWatchlists();
        final Watchlist wl = watchlistId < 0 ?
                watchlists.get(0) :
                service.findWatchlist(watchlistId);

        List<Security> securities = service.findSecurities(wl);
        YahooPriceHelper helper = new YahooPriceHelper(this);
        String fmt = this.getString(R.string.request_files_msg_fmt);

        initProgressDialog(R.string.request_files, securities.size());

        Observable.from(securities)
                .filter(Security::isEnabled)
                .flatMap(security ->
                        Observable.just(security)
                                .map(sec -> {
                                    String msg = String.format(fmt, sec.getSymbol());
                                    this.notifyFileProgress(msg, 1);
                                    sec.setTicks(helper.downloadAndCacheDailyTicks(sec));
                                    return sec;
                                })
                                .subscribeOn(Schedulers.io()))
                .toList()
                .subscribe(loadedSecurities -> {
                    wl.setSecurities(loadedSecurities);
                    this.completeFileProgress(R.string.request_files_complete, new WatchlistFilesRequestComplete(watchlists, wl));

                }, error -> {
                    Log.e(TAG, "onWatchlistFilesRequestStart: ", error);
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
