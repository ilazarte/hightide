package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.activity.internal.AbstractBaseActivity;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.events.RelativeTableLoadComplete;
import com.blm.hightide.events.RelativeTableLoadStart;
import com.blm.hightide.fragments.RelativeTableFragment;
import com.blm.hightide.model.RelativeGridRow;
import com.blm.hightide.model.StudyGridParams;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RelativeTableActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeTableActivity.class.getSimpleName();

    private static final String WATCHLIST_ID = "com.blm.hightide.activity.WATCHLIST_ID";

    public static Intent newIntent(Context context, int watchlistId) {
        Intent intent = new Intent(context, RelativeTableActivity.class);
        intent.putExtra(WATCHLIST_ID, watchlistId);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return RelativeTableFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        int watchlistId = this.getIntent().getExtras().getInt(WATCHLIST_ID);
        onRelativeTableLoadStart(new RelativeTableLoadStart(watchlistId, new StudyGridParams(), true));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onRelativeTableLoadStart(RelativeTableLoadStart event) {

        toast(R.string.chart_security);

        StockService service = this.getStockService();
        int watchlistId = event.getWatchlistId();
        StudyGridParams params = event.getParams();

        service.findWatchlist(watchlistId)
                .flatMap(wl -> service.setWatchlistPriceData(wl, params.getAggType(), true))
                .subscribe(wl -> {
                    List<RelativeGridRow> rows = service.getRelativeTableForAverage(wl, params);
                    EventBus.getDefault().post(new RelativeTableLoadComplete(wl, rows, params));
                }, error -> {
                    Log.e(TAG, "onRelativeTableLoadStart: ", error);
                });
    }
}