package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.fragments.RelativePerformanceFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;
import com.blm.hightide.util.YahooPriceHelper;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

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

        int watchlistId = event.getWatchlistId();
        Watchlist watchlist = service.findWatchlist(watchlistId);
        List<Security> securities = service.findSecurities(watchlist);
        watchlist.setSecurities(securities);

        YahooPriceHelper helper = new YahooPriceHelper(this);
        String fmt = this.getString(R.string.read_files_msg_fmt);

        this.initProgressDialog(R.string.read_files, watchlist.getSecurities().size());

        Observable.from(securities)
                .filter(Security::isEnabled)
                .flatMap(security ->
                        Observable.just(security)
                                .map(sec -> {
                                    String message = String.format(fmt, sec.getSymbol());
                                    this.notifyFileProgress(message, 1);
                                    List<String> lines = helper.read(sec.getDailyFilename());
                                    List<Tick> ticks = helper.readDaily(lines);
                                    sec.setTicks(ticks);
                                    return sec;
                                })
                                .subscribeOn(Schedulers.io()))
                .toList()
                .subscribe(loadedSecurities -> {

                    int lastN = 60;
                    int avgLen = 20;
                    int lastNTicks = lastN - avgLen;

                    watchlist.setSecurities(loadedSecurities);
                    List<ILineDataSet> relative = service.getRelativeForAverage(watchlist, lastN, avgLen);
                    List<String> xvals = service.toXAxis(watchlist.getSecurities().get(0).getTicks(), lastNTicks);
                    LineData data = new LineData(xvals, relative);

                    this.completeFileProgress(R.string.read_files_complete, new LineDataAvailable(data));
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