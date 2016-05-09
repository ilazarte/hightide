package com.blm.hightide.service;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.blm.corals.PriceData;
import com.blm.corals.ReadError;
import com.blm.corals.Tick;
import com.blm.corals.study.Operators;
import com.blm.corals.study.window.Average;
import com.blm.hightide.db.DatabaseHelper;
import com.blm.hightide.model.AggType;
import com.blm.hightide.model.FileData;
import com.blm.hightide.model.FileLine;
import com.blm.hightide.model.RelativeGridRow;
import com.blm.hightide.model.RelativeTick;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.StudyGridParams;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.TickType;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.util.FrequencyFormatter;
import com.blm.hightide.util.StandardPriceData;
import com.blm.hightide.util.YahooPriceHelper;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

public class StockService {

    @SuppressWarnings("unused")
    private static final String TAG = StockService.class.getSimpleName();

    private Operators<Double> op = Operators.doubles();

    private DatabaseHelper helper;

    private YahooPriceHelper yahooPriceHelper;

    public StockService() {
    }

    /**
     * Required to invoke onCreate
     * @param context Activity context
     */
    public synchronized void resume(Context context) {
        this.yahooPriceHelper = new YahooPriceHelper(context);
        if (helper == null) {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        }
    }

    /**
     * Code to respond to a destroy event.
     * Essentially assumes application is being exited?
     */
    public void destroy() {
        if (helper != null) {
            OpenHelperManager.releaseHelper();
        }
    }

    /**
     * http://stackoverflow.com/questions/28402376/how-to-compose-observables-to-avoid-the-given-nested-and-dependent-callbacks
     * Create an observable which loads the securities onto the watchlist and returns the watchlist.
     * This method sorts the watchlist securities by name.
     *
     * @param watchlist A watchlist containing securities without tick data.
     * @param aggType The tick configuration to use for accessing data.
     * @param readRequest A boolean to request a read only operation if possible.
     * @return a security with ticks populated observer
     */
    public Observable<Watchlist> setWatchlistPriceData(Watchlist watchlist, AggType aggType, boolean readRequest) {

        Observable<List<Security>> listObservable = Observable.from(watchlist.getSecurities())
                .filter(Security::isEnabled)
                .flatMap(security -> this.setStandardPriceData(security, aggType, readRequest)
                        .subscribeOn(Schedulers.io()))
                .toSortedList((s1, s2) -> s1.getSymbol().compareTo(s2.getSymbol()));

        Observable<Watchlist> wlo = Observable.just(watchlist);

        return Observable.zip(wlo, listObservable,
                (wl, securities) -> {
                    wl.setSecurities(securities);
                    /*Log.i(TAG, "setWatchlistPriceData: " + securities.size());
                    for (Security sec : securities) {
                        Log.i(TAG, "setWatchlistPriceData: security: " + sec.getSymbol() + ", " + sec.getStandardPriceData().getTicks().size());
                    }*/
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

                    int wlid = 999;
                    for (Watchlist watchlist : watchlists) {
                        if (watchlist.getId() < wlid) {
                            wl = watchlist;
                            wlid = watchlist.getId();
                        }
                    }

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
     * @param aggType The time option to load
     * @param readRequest A boolean to request a read only operation if possible.
     * @return observable security
     */
    public Observable<Security> setStandardPriceData(Security security, AggType aggType, boolean readRequest) {

        return Observable.just(security)
                .map(sec -> {

                    StandardPriceData priceData = null;
                    File file = null;
                    TickType tickType = aggType.getTickType();
                    boolean notexpired = false;
                    boolean read = false;

                    switch (tickType) {
                        case INTRADAY:
                            file = yahooPriceHelper.toFile(sec.getIntradayFilename());
                            int minutemillis = 60 * 1000;
                            int fiveminutes = 5 * minutemillis;
                            notexpired = (System.currentTimeMillis() - file.lastModified()) < fiveminutes;
                            read = readRequest && file.exists() && notexpired;

                            break;
                        case DAILY:
                            file = yahooPriceHelper.toFile(sec.getDailyFilename());
                            int hourmillis = 60 * 60 * 1000;
                            int fourhours = 4 * hourmillis;
                            notexpired = (System.currentTimeMillis() - file.lastModified()) < fourhours;
                            read = readRequest && file.exists() && notexpired;

                            break;
                    }

                    if (read) {
                        priceData = yahooPriceHelper.readCachePriceData(sec, aggType);
                    } else {
                        priceData = yahooPriceHelper.downloadAndCachePriceData(sec, aggType);
                    }

                    sec.setStandardPriceData(priceData);
                    return sec;
                });
    }

    /**
     * @param security Using a populated instance, retrieve file data.
     * @param aggType the type of tick file to load
     * @return Loaded file data
     */
    public FileData getFileData(Security security, AggType aggType) {

        String filename = yahooPriceHelper.getAggFilename(security, aggType);

        List<FileLine> fileLines = new ArrayList<>();
        PriceData priceData = yahooPriceHelper.readCachePriceData(security, aggType);
        List<String> lines = yahooPriceHelper.read(filename);

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
     * @param params The configuration of this study.
     * @return A relatively raw dataset list.
     */
    public LineData getRelativeForAverage(Watchlist watchlist, StudyParams params) {
        List<Security> securities = watchlist.getSecurities();
        List<ILineDataSet> dataSets = new ArrayList<>();

        int num = 0;
        for (Security security : securities) {
            if (security.isEnabled()) {
                num++;
            }
        }

        int[] colorPalette = ColorBrewer.Accent.getColorPalette(num);

        List<Tick> availableTicks = null; /*the first usable dataset for xaxis*/
        int i = 0;
        for (Security security : securities) {

            if (!security.isEnabled()) {
                continue;
            }

            List<Tick> ticks = security.getStandardPriceData().getTicks();
            if (availableTicks == null) {
                availableTicks = ticks;
            }

            List<Double> study = getCloseByAverage(ticks, params);
            LineDataSet dataset = toLineDataSet(security.getSymbol(), study);
            dataset.setColor(colorPalette[i++]);
            dataset.setDrawCircles(false);
            dataset.setLineWidth(2f);
            dataSets.add(dataset);
        }

        int lastNTicks = params.getLength() - params.getAvgLength();
        List<String> xvals = this.toXAxis(availableTicks, lastNTicks, params.getAggType().getTickType());

        return new LineData(xvals, dataSets);
    }

    /**
     * Given a valid watchlist with ticks for every security,
     * return a line of datasets.
     * @param watchlist Input watchlist containing securities and ticks.
     * @param params The configured parameters for generating the grid.
     * @return A single list where each rowCount is a 'row' sorted from highest to lowest.
     *   Each rowcount of items will represent a single tick.  Each row still start with 1 date instance.
     *   So if rowCount is 6, it will be 7 items per row.
     */
    public List<RelativeGridRow> getRelativeTableForAverage(Watchlist watchlist, StudyGridParams params) {

        int lastN = params.getLength();
        int avgLen = params.getAvgLength();
        int rowCount = params.getTopLength();

        boolean daily = TickType.DAILY.equals(params.getAggType().getTickType());
        String format = daily ? "MM-dd\nyyyy" : "MM-dd\nHH:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        List<Security> securities = watchlist.getSecurities();

        int num = 0;
        for (Security security : securities) {
            if (security.isEnabled()) {
                num++;
            }
        }

        int[] colorPalette = ColorBrewer.Accent.getColorPalette(num);

        Map<String, Integer> colorMap = new HashMap<>();
        Map<String, List<Double>> valueMap = new HashMap<>();
        List<Tick> availableTicks = new ArrayList<>(); /*the first usable dataset for xaxis*/
        int colorIncr = 0;

        for (Security security : securities) {

            if (!security.isEnabled()) {
                continue;
            }

            List<Tick> ticks = security.getStandardPriceData().getTicks();
            if (availableTicks.size() == 0) {
                availableTicks = op.last(ticks, lastN - avgLen);
            }

            List<Double> study = getCloseByAverage(ticks, params);

            colorMap.put(security.getSymbol(), colorPalette[colorIncr++]);
            valueMap.put(security.getSymbol(), study);
        }

        List<String> symbols = new ArrayList<>(valueMap.keySet());
        List<RelativeGridRow> gridList = new ArrayList<>();
        List<RelativeTick> sampleTicks = new ArrayList<>();

        for (int i = 0; i < availableTicks.size(); i++) {
            sampleTicks.clear();

            for (String symbol : symbols) {
                List<Double> values = valueMap.get(symbol);
                Double value = values.get(i);
                Integer color = colorMap.get(symbol);
                sampleTicks.add(new RelativeTick(symbol, value, color));
            }
            Collections.sort(sampleTicks, RelativeTick.COMPARATOR);

            Date date = availableTicks.get(i).getTimestamp();

            List<RelativeTick> rowList = new ArrayList<>();
            for (int j = sampleTicks.size() - 1, min = j - rowCount; j > min; j--) {
                rowList.add(sampleTicks.get(j));
            }

            RelativeGridRow row = new RelativeGridRow(sdf.format(date), rowList);
            gridList.add(row);
        }

        Collections.reverse(gridList);

        return gridList;
    }

    /**
     * Return a close value and the average calcualted for it.
     * @param security The source security with ticks.
     * @param params The params to apply to this price and average.
     * @return the list of line data
     */
    public CombinedData getPriceAndAverage(Security security, StudyParams params) {


        FrequencyFormatter formatter = new FrequencyFormatter();

        boolean daily = TickType.DAILY.equals(params.getAggType().getTickType());
        String column = daily ? "adjclose" : "close";
        int lastN = params.getLength();
        int avgLen = params.getAvgLength();

        int[] colorPalette = ColorBrewer.Accent.getColorPalette(2);
        List<Tick> ticks = security.getStandardPriceData().getTicks();

        List<Double> fullval = op.get(ticks, column);
        List<Double> val = op.last(fullval, lastN);
        List<Double> allavg = op.window(val, avgLen, new Average());

        List<Double> avg = op.range(allavg, avgLen);

        CandleDataSet ohlc = toCandleDataSet(security.getSymbol(), op.range(op.last(ticks, lastN), avgLen));
        ohlc.setShadowWidth(2f);
        ohlc.setHighlightLineWidth(2f);
        ohlc.setShadowColor(Color.DKGRAY);
        ohlc.setShadowWidth(0.7f);
        ohlc.setDecreasingColor(Color.rgb(183, 28, 28));
        ohlc.setDecreasingPaintStyle(Paint.Style.FILL);
        ohlc.setIncreasingColor(Color.BLACK);
        ohlc.setIncreasingPaintStyle(Paint.Style.STROKE);
        ohlc.setNeutralColor(Color.DKGRAY);
        ohlc.setAxisDependency(YAxis.AxisDependency.LEFT);
        ohlc.setValueFormatter(formatter);

        CandleData candleData = new CandleData();
        candleData.addDataSet(ohlc);

        LineDataSet study = toLineDataSet("Average(" + avgLen + ")", avg);
        study.setColor(colorPalette[1]);
        study.setDrawCircles(false);
        study.setLineWidth(2f);
        study.setAxisDependency(YAxis.AxisDependency.LEFT);
        study.setValueFormatter(formatter);

        LineData lineData = new LineData();
        lineData.addDataSet(study);

        int lastNTicks = lastN - avgLen;
        List<String> xvals = this.toXAxis(security.getStandardPriceData().getTicks(), lastNTicks, params.getAggType().getTickType());

        CombinedData data = new CombinedData(xvals);
        data.setData(lineData);
        data.setData(candleData);

        return data;
    }

    /**
     * Perform a moving average calc over them.
     *
     * @param ticks Input
     * @param params The study params.
     * @return The study
     */
    public List<Double> getCloseByAverage(List<Tick> ticks, StudyParams params) {

        boolean daily = TickType.DAILY.equals(params.getAggType().getTickType());
        String data = daily ? "adjclose" : "close";

        List<Double> fullval = op.get(ticks, data);

        List<Double> val = op.last(fullval, params.getLength());
        List<Double> avgs = op.window(val, params.getAvgLength(), new Average());
        List<Double> div = op.divide(val, avgs);

        return op.range(div, params.getAvgLength());
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

    public CandleDataSet toCandleDataSet(String symbol, List<Tick> values) {

        List<CandleEntry> entries = new ArrayList<>();

        for (int i = 0, len = values.size(); i < len; i++) {
            Tick tick = values.get(i);
            float h = (float) tick.get("high").doubleValue();
            float l = (float) tick.get("low").doubleValue();
            float o = (float) tick.get("open").doubleValue();
            float c = (float) tick.get("close").doubleValue();
            CandleEntry entry = new CandleEntry(i, h, l, o, c);
            entries.add(entry);
        }

        return new CandleDataSet(entries, symbol);
    }

    /**
     * Return a list of values for an x column
     * @param ticks A sample containing the timestamp.
     * @param lastN the number of values to get.
     * @param tickType used for changing format strings
     * @return list of values for an x column.
     */
    public List<String> toXAxis(List<Tick> ticks, int lastN, TickType tickType) {

        boolean daily = TickType.DAILY.equals(tickType);
        final String format = daily ?
                "MM-dd-yyyy" :
                "MM-dd HH:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        List<String> axis = new ArrayList<>();
        for (int i = ticks.size() - lastN; i < ticks.size(); i++) {
            Tick tick = ticks.get(i);
            axis.add(sdf.format(tick.getTimestamp()));
        }
        return axis;
    }
}
