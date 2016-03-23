package com.blm.hightide.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.FilesNotificationEvent;
import com.blm.hightide.events.RequestFilesCompleteEvent;
import com.blm.hightide.events.RequestFilesInitEvent;
import com.blm.hightide.events.RequestFilesStartEvent;
import com.blm.hightide.fragments.WatchlistFragment;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;

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

    @Subscribe
    public void onRequestInit(RequestFilesInitEvent event) {
        Watchlist watchlist = event.getWatchlist();
        service.findSecurities(watchlist);
        this.initProgressDialog(R.string.request_files, watchlist.getSecurities().size());

        EventBus.getDefault().post(new RequestFilesStartEvent(watchlist));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onRequestData(RequestFilesStartEvent event) {
        Watchlist watchlist = event.getWatchlist();
        service.requestDailyTicks(watchlist);

        EventBus.getDefault().post(new RequestFilesCompleteEvent(watchlist));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestNotification(FilesNotificationEvent event) {
        notifyFileProgress(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestCompleted(RequestFilesCompleteEvent event) {
        completeFileProgress(R.string.request_files_complete);
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
