package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.events.RelativeTableLoadComplete;
import com.blm.hightide.events.RelativeTableLoadStart;
import com.blm.hightide.fragments.RelativeTableFragment;
import com.blm.hightide.model.RelativeTick;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RelativeTableActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeTableActivity.class.getSimpleName();

    private static final String WATCHLIST_ID = "com.blm.hightide.activity.WATCHLIST_ID";

    private StockService service = new StockService();

    public static Intent newIntent(Context context, int watchlistId) {
        Intent intent = new Intent(context, RelativeTableActivity.class);
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
        return RelativeTableFragment.newInstance(watchlistId);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onRelativeTableLoadStart(RelativeTableLoadStart event) {

        toast(R.string.chart_security);

        int watchlistId = event.getWatchlistId();
        service.findWatchlist(watchlistId)
                .flatMap(wl -> service.setWatchlistPriceData(wl, true))
                .subscribe(wl -> {

                    /* TODO arg, where to put these guys?
                     * must match the relative chart activity.
                     */
                    int lastN = 60;
                    int avgLen = 20;
                    int topN = 6;

                    List<Object> gridList = service.getRelativeTableForAverage(wl, lastN, avgLen, topN);
                    EventBus.getDefault().post(new RelativeTableLoadComplete(wl, gridList, topN));
                }, error -> {
                    Log.e(TAG, "onRelativeTableLoadStart: ", error);
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}