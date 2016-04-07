package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.fragments.RelativePerformanceFragment;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        service.init(this);
    }

    @Override
    public Fragment createFragment() {
        int watchlistId = this.getIntent().getExtras().getInt(WATCHLIST_ID);
        return RelativePerformanceFragment.newInstance(watchlistId);
    }

    /**
     * TODO make study parameters configurable via ui
     * @param event The starting watchlist id
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onWatchlistLoadFilesStart(WatchlistLoadFilesStart event) {

        toast(R.string.chart_security);

        int watchlistId = event.getWatchlistId();
        service.findWatchlist(watchlistId)
                .flatMap(wl -> service.setWatchlistPriceData(wl, true))
                .subscribe(wl -> {

                    int lastN = 60;
                    int avgLen = 20;
                    LineData data = service.getRelativeForAverage(wl, lastN, avgLen);

                    EventBus.getDefault().post(new LineDataAvailable(wl, data));
                }, error -> {
                    Log.e(TAG, "onWatchlistLoadFilesStart: ", error);
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}