package com.blm.hightide.service;

import android.content.Context;
import android.util.Log;

import com.blm.corals.Tick;
import com.blm.corals.study.Operators;
import com.blm.corals.study.window.Average;
import com.blm.hightide.db.DatabaseHelper;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.util.YahooPriceHelper;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.manolovn.colorbrewer.ColorBrewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StockService {

    private static final String TAG = StockService.class.getSimpleName();

    private Operators op = new Operators();

    private DatabaseHelper helper;

    private Context context;

    public StockService() {
    }

    /**
     * Required to invoke onCreate
     * @param context Activity context
     */
    public void init(Context context) {
        this.context = context;
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
    }

    /**
     * Preferably invoked in onDestroy
     */
    public void release() {
        if (helper != null) {
            OpenHelperManager.releaseHelper();
        }
        this.context = null;
    }

    /**
     * Get a list of all watchlists.
     * @return the entire db list of watchlists
     */
    public List<Watchlist> findAllWatchlists() {
        return helper.findAllWatchlists();
    }

    /**
     * Find a watchlist by id
     * @param id the id to use
     * @return a watchlist
     */
    public Watchlist findWatchlist(Integer id) {
        return helper.findWatchlist(id);
    }

    /**
     * Given a valid watchlist with ticks for every security,
     * return a line of datasets.
     * @param watchlist Input watchlist containing securities and ticks.
     * @param lastN Last n values from the ticks.
     * @param avgLen Length of the average
     * @return A relatively raw dataset list.
     */
    public List<ILineDataSet> getRelativeForAverage(Watchlist watchlist, int lastN, int avgLen) {
        List<Security> securities = watchlist.getSecurities();
        List<ILineDataSet> dataSets = new ArrayList<>();

        int num = 0;
        for (Security security : securities) {
            if (security.isEnabled()) {
                num++;
            }
        }

        int[] colorPalette = ColorBrewer.Spectral.getColorPalette(num);

        int i = 0;
        for (Security security : securities) {

            if (!security.isEnabled()) {
                continue;
            }

            List<Tick> ticks = security.getTicks();
            List<Double> study = getCloseByAverage(ticks, lastN, avgLen);
            LineDataSet dataset = toLineDataSet(security.getSymbol(), study);
            dataset.setColor(colorPalette[i++]);
            dataset.setDrawCircles(false);
            dataSets.add(dataset);
        }
        return dataSets;
    }

    /**
     * Perform a moving average calc over them.
     *
     * @param ticks Input
     * @param lastN Last n values from the ticks.
     * @param avgLen Length of the average
     * @return The study
     */
    public List<Double> getCloseByAverage(List<Tick> ticks, int lastN, int avgLen) {
        List<Double> fullval = op.get(ticks, "close");

        List<Double> val = op.last(fullval, lastN);
        List<Double> avgs = op.window(val, avgLen, new Average());
        List<Double> div = op.divide(val, avgs);

        return op.range(div, avgLen);
    }

    public LineDataSet toLineDataSet(String symbol, List<Double> values) {

        List<Entry> entries = new ArrayList<>();

        for (int i = 0, len = values.size(); i < len; i++) {
            float val = (float) values.get(i).doubleValue();
            Entry entry = new Entry(val, i);
            entries.add(entry);
        }

        return new LineDataSet(entries, symbol);
    }

    /**
     * Load the securities for the watchlist.
     * @param watchlist A fresh watchlist from the db.
     */
    public void findSecurities(Watchlist watchlist) {
        List<Security> securities = helper.findSecuritiesByWatchlist(watchlist);
        watchlist.setSecurities(securities);
    }

    /**
     * @param watchlist the watchlist with preloaded securities, to load ticks for.
     */
    public void requestDailyTicks(Watchlist watchlist) {

        YahooPriceHelper helper = new YahooPriceHelper(context);

        List<Security> securities = watchlist.getSecurities();
        for (Security security : securities) {
            Log.i(TAG, "requesting daily for: " + security.getSymbol());

            List<String> lines = helper.daily(security.getSymbol());
            helper.write(lines, security.getDailyFilename());
            List<Tick> ticks = helper.readDaily(lines);
            security.setTicks(ticks);
        }
    }

    /**
     * Read from the request cache of an already downloaded file.
     * @param watchlist the watchlist to load already requested tick files for
     */
    public void readDailyTicks(Watchlist watchlist) {

        YahooPriceHelper helper = new YahooPriceHelper(context);

        List<Security> securities = watchlist.getSecurities();
        for (Security security : securities) {
            Log.i(TAG, "reading daily for: " + security.getSymbol());
            List<String> lines = helper.read(security.getDailyFilename());
            List<Tick> ticks = helper.readDaily(lines);
            security.setTicks(ticks);
        }
    }

    /**
     * Return a list of values for an x column
     * @param ticks A sample containing the timestamp.
     * @param lastN the number of values to get.
     * @return list of values for an x column.
     */
    public List<String> toXAxis(List<Tick> ticks, int lastN) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        List<String> axis = new ArrayList<>();
        for (int i = ticks.size() - lastN; i < ticks.size(); i++) {
            Tick tick = ticks.get(i);
            axis.add(sdf.format(tick.getTimestamp()));
        }
        return axis;
    }


}
