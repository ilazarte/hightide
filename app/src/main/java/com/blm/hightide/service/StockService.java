package com.blm.hightide.service;

import android.content.Context;

import com.blm.corals.PriceData;
import com.blm.corals.ReadError;
import com.blm.corals.Tick;
import com.blm.corals.study.Operators;
import com.blm.corals.study.window.Average;
import com.blm.hightide.db.DatabaseHelper;
import com.blm.hightide.model.FileData;
import com.blm.hightide.model.FileLine;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.util.StandardPriceData;
import com.blm.hightide.util.YahooPriceHelper;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.manolovn.colorbrewer.ColorBrewer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.schedulers.Schedulers;

public class StockService {

    @SuppressWarnings("unused")
    private static final String TAG = StockService.class.getSimpleName();

    private Operators op = new Operators();

    private DatabaseHelper helper;

    private YahooPriceHelper yahooPriceHelper;

    public StockService() {
    }

    /**
     * Required to invoke onCreate
     * @param context Activity context
     */
    public void init(Context context) {
        this.yahooPriceHelper = new YahooPriceHelper(context);
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
    }

    /**
     * http://stackoverflow.com/questions/28402376/how-to-compose-observables-to-avoid-the-given-nested-and-dependent-callbacks
     * Create an observable which loads the securities onto the watchlist and returns the watchlist.
     * This method sorts the watchlist securities by name.
     *
     * @param watchlist A watchlist containing securities without tick data.
     * @param readRequest A boolean to request a read only operation if possible.
     * @return a security with ticks populated observer
     */
    public Observable<Watchlist> setWatchlistPriceData(Watchlist watchlist, boolean readRequest) {

        Observable<List<Security>> listObservable = Observable.from(watchlist.getSecurities())
                .filter(Security::isEnabled)
                .flatMap(security -> this.setStandardPriceData(security, readRequest)
                        .subscribeOn(Schedulers.io()))
                .toSortedList((s1, s2) -> s1.getSymbol().compareTo(s2.getSymbol()));

        Observable<Watchlist> wlo = Observable.just(watchlist);

        return Observable.zip(wlo, listObservable,
                (wl, securities) -> {
                    wl.setSecurities(securities);
                    return wl;
                });
    }

    /**
     * @return A sorted observable of all watchlists.
     */
    public Observable<List<Watchlist>> findWatchlists() {
        return Observable.defer(() -> {
            List<Watchlist> allWatchlists = helper.findAllWatchlists();
            Collections.sort(allWatchlists, (wl1, wl2) -> wl1.getName().compareTo(wl2.getName()));
            return Observable.just(allWatchlists);
        });
    }

    /**
     * @param watchlistId A watchlist id which must exist.
     * @return The observable.
     */
    public Observable<Watchlist> findWatchlist(int watchlistId) {
        return findWatchlist(watchlistId, null, false);
    }

    /**
     * Load the watchlist specified, allowing for the first found watchlist.
     * @param watchlistId An id or < 0 indicating no preference.
     * @param watchlists The list of all watchlists
     * @param any Whether or not the first found watchlist suffices.
     * @return The observable watchlist.
     */
    public Observable<Watchlist> findWatchlist(int watchlistId, List<Watchlist> watchlists, boolean any) {

        if (watchlistId < 0 && !any) {
            return Observable.empty();
        }

        return Observable.just(watchlistId)
            .concatMap(id -> {
                Watchlist wl = null;
                if (id < 0) {
                    wl = watchlists.get(0);
                } else {
                    wl = helper.findWatchlist(id);
                }
                if (wl == null) {
                    return Observable.error(new IllegalStateException("No watchlist found with id " + id));
                }
                List<Security> securities = helper.findSecuritiesByWatchlist(wl);
                wl.setSecurities(securities);
                return Observable.just(wl);
            });
    }

    /**
     * Return a price data empty security from the db.
     * @param symbol the pneumonic
     * @return A light instance of a security.
     */
    public Observable<Security> findSecurity(String symbol) {
        return Observable.defer(() -> Observable.just(helper.findSecurity(symbol)));
    }

    /**
     * Set the price data for one security.
     * @param security A security containing symbol.
     * @param readRequest A boolean to request a read only operation if possible.
     * @return observable security
     */
    public Observable<Security> setStandardPriceData(Security security, boolean readRequest) {

        return Observable.just(security)
                .map(sec -> {

                    File file = yahooPriceHelper.toFile(sec.getDailyFilename());
                    int hourmillis = 60 * 60 * 1000;
                    int fourhours = 4 * hourmillis;
                    boolean recent = (System.currentTimeMillis() - file.lastModified()) < fourhours;
                    boolean read = readRequest && file.exists() && recent;

                    StandardPriceData priceData = null;
                    if (read) {
                        priceData = yahooPriceHelper.readPriceData(sec);
                    } else {
                        priceData = yahooPriceHelper.downloadAndCacheDailyPriceData(sec);
                    }

                    sec.setStandardPriceData(priceData);
                    return sec;
                });
    }

    /**
     * Preferably invoked in onDestroy
     */
    public void release() {
        if (helper != null) {
            OpenHelperManager.releaseHelper();
        }
    }

    /**
     * @param security Using a populated instance, retrieve file data.
     * @return Loaded file data
     */
    public FileData getFileData(Security security) {

        List<FileLine> fileLines = new ArrayList<>();
        PriceData priceData = yahooPriceHelper.readPriceData(security);
        List<String> lines = yahooPriceHelper.read(security.getDailyFilename());

        List<ReadError> errors = priceData.getErrors();
        int lineNum = 1;

        for (String line : lines) {
            FileLine fileLine = new FileLine();
            fileLine.setNum(lineNum);
            fileLine.setLine(line);

            if (errors != null && errors.size() != 0) {
                Iterator<ReadError> it = errors.iterator();
                while (it.hasNext()) {
                    ReadError re = it.next();
                    if (re.getLine() == lineNum) {
                        fileLine.add(re);
                        it.remove();
                    }
                }
            }

            fileLines.add(fileLine);
            lineNum++;
        }

        FileData fileData = new FileData();
        fileData.setName(security.getDailyFilename());
        fileData.setLines(fileLines);
        return fileData;
    }

    /**
     * Given a valid watchlist with ticks for every security,
     * return a line of datasets.
     * @param watchlist Input watchlist containing securities and ticks.
     * @param lastN Last n values from the ticks.
     * @param avgLen Length of the average
     * @return A relatively raw dataset list.
     */
    public LineData getRelativeForAverage(Watchlist watchlist, int lastN, int avgLen) {
        List<Security> securities = watchlist.getSecurities();
        List<ILineDataSet> dataSets = new ArrayList<>();

        int num = 0;
        for (Security security : securities) {
            if (security.isEnabled()) {
                num++;
            }
        }

        int[] colorPalette = ColorBrewer.Accent.getColorPalette(num);

        List<Tick> availableTicks = null;
        int i = 0;
        for (Security security : securities) {

            if (!security.isEnabled()) {
                continue;
            }

            List<Tick> ticks = security.getStandardPriceData().getTicks();
            if (availableTicks == null) {
                availableTicks = ticks;
            }

            List<Double> study = getCloseByAverage(ticks, lastN, avgLen);
            LineDataSet dataset = toLineDataSet(security.getSymbol(), study);
            dataset.setColor(colorPalette[i++]);
            dataset.setDrawCircles(false);
            dataset.setLineWidth(2f);
            dataSets.add(dataset);
        }

        int lastNTicks = lastN - avgLen;
        List<String> xvals = this.toXAxis(availableTicks, lastNTicks);

        return new LineData(xvals, dataSets);
    }

    /**
     * Return a close value and the average calcualted for it.
     * @param security The source security with ticks.
     * @param lastN The number of the values to use from the right.
     * @param avgLen The length of the average.
     * @return the list of line data
     */
    public LineData getPriceAndAverage(Security security, int lastN, int avgLen) {

        List<ILineDataSet> dataSets = new ArrayList<>();

        int[] colorPalette = ColorBrewer.Accent.getColorPalette(2);
        List<Tick> ticks = security.getStandardPriceData().getTicks();

        List<Double> fullval = op.get(ticks, "adjclose");
        List<Double> val = op.last(fullval, lastN);
        List<Double> allavg = op.window(val, avgLen, new Average());

        List<Double> close = op.range(val, avgLen);
        List<Double> avg = op.range(allavg, avgLen);

        LineDataSet dataset = toLineDataSet(security.getSymbol(), close);
        dataset.setColor(colorPalette[0]);
        dataset.setDrawCircles(false);
        dataset.setLineWidth(2f);

        dataSets.add(dataset);

        LineDataSet study = toLineDataSet("Average(" + avgLen + ")", avg);
        study.setColor(colorPalette[1]);
        study.setDrawCircles(false);
        study.setLineWidth(2f);

        dataSets.add(study);

        int lastNTicks = lastN - avgLen;
        List<String> xvals = this.toXAxis(security.getStandardPriceData().getTicks(), lastNTicks);

        return new LineData(xvals, dataSets);
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
        List<Double> fullval = op.get(ticks, "adjclose");

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
