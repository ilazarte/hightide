package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.activity.internal.AbstractBaseActivity;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.events.RelativeChartDataAvailable;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.fragments.RelativeChartFragment;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RelativeChartActivity extends AbstractBaseActivity {

    private static final String TAG = RelativeChartActivity.class.getSimpleName();

    private static final String WATCHLIST_ID = "com.blm.hightide.activity.WATCHLIST_ID";


    public static Intent newIntent(Context context, int watchlistId) {
        Intent intent = new Intent(context, RelativeChartActivity.class);
        intent.putExtra(WATCHLIST_ID, watchlistId);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return RelativeChartFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        int watchlistId = this.getIntent().getExtras().getInt(WATCHLIST_ID);
        onWatchlistLoadFilesStart(new WatchlistLoadFilesStart(watchlistId, new StudyParams()));
    }

    /**
     * @param event The starting watchlist id
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onWatchlistLoadFilesStart(WatchlistLoadFilesStart event) {

        toast(R.string.chart_security);

        StockService service = this.getStockService();
        int watchlistId = event.getWatchlistId();
        StudyParams params = event.getParams();

        service.findWatchlist(watchlistId)
                .flatMap(wl -> service.setWatchlistPriceData(wl, params.getAggType(), true))
                .subscribe(wl -> {
                    LineData data = service.getRelativeForAverage(wl, params);
                    EventBus.getDefault().post(new RelativeChartDataAvailable(wl, data, params));
                }, error -> {
                    Log.e(TAG, "onWatchlistLoadFilesStart: ", error);
                });
    }
}