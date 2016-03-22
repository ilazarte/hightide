package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.FilesNotificationEvent;
import com.blm.hightide.events.LineDataAvailableEvent;
import com.blm.hightide.events.LoadFilesCompleteEvent;
import com.blm.hightide.events.LoadFilesStartEvent;
import com.blm.hightide.fragments.RelativePerformanceFragment;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RelativePerformanceActivity extends AbstractBaseActivity {

    private static final String TAG = RelativePerformanceActivity.class.getSimpleName();

    private static final String WATCHLIST_ID = "com.blm.hightide.activity.WATCHLIST_ID";

    private StockService service = new StockService();

    public static Intent newIntent(Context context, int watchlistId) {
        Intent intent = new Intent(context, RelativePerformanceActivity.class);
        intent.putExtra(WATCHLIST_ID, watchlistId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        service.init(this);
    }

    @Override
    public Fragment createFragment() {
        int watchlistId = this.getIntent().getExtras().getInt(WATCHLIST_ID);
        return RelativePerformanceFragment.newInstance(watchlistId);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLoadRequest(LoadFilesStartEvent event) {

        int id = event.getWatchlistId();
        Watchlist watchlist = service.findWatchlist(id);
        service.findSecurities(watchlist);
        service.readDailyTicks(watchlist);

        /**
         * Make this a ui configurable via RPF
         */
        int lastN = 60;
        int avgLen = 20;
        int lastNTicks = lastN - avgLen;

        List<ILineDataSet> relative = service.getRelativeForAverage(watchlist, lastN, avgLen);
        List<String> xvals = service.toXAxis(watchlist.getSecurities().get(0).getTicks(), lastNTicks);
        LineData data = new LineData(xvals, relative);

        EventBus.getDefault().post(new LoadFilesCompleteEvent(new LineDataAvailableEvent(data)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadNotification(FilesNotificationEvent event) {
        this.notifyFileProgress(event, R.string.read_files);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadFilesCompleteEvent(LoadFilesCompleteEvent event) {
        this.completeFileProgress(R.string.read_files_complete);
        EventBus.getDefault().post(event.getLineDataAvailableEvent());
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

