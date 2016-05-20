package com.blm.hightide.util;

import android.content.Context;
import android.util.Log;

import com.blm.corals.DateHelper;
import com.blm.corals.Interval;
import com.blm.corals.PeriodType;
import com.blm.corals.PriceData;
import com.blm.corals.Tick;
import com.blm.corals.provider.YahooPriceURL;
import com.blm.corals.provider.YahooTickReader;
import com.blm.hightide.model.AggType;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.TickType;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created due to being unable to separate downloading from reading.
 * Solution is to remove downloading from corals.
 * Library should simply provide ability to get url and then parse format with report.
 */
public class YahooPriceHelper {

    private static final String TAG = YahooPriceHelper.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();

    private Context context;
    private YahooPriceURL urls = new YahooPriceURL();
    private YahooTickReader reader = new YahooTickReader();

    public YahooPriceHelper(Context context) {
        this.context = context;
    }

    /**
     * Download daily prices going back x years.
     * @param symbol The security symbol to download
     * @param years The number of years to download (positive value)
     * @return The list of strings in the file
     */
    public List<String> daily(String symbol, int years) {

        Date end = new Date();
        DateHelper dh = new DateHelper();
        Date start = dh.calculate(end, PeriodType.YEAR, -years);
        Interval interval = new Interval(start, end);
        String url = urls.daily(symbol, interval);
        return download(url);
    }

    public List<String> intraday(String symbol) {
        String url = urls.intraday(symbol);
        return download(url);
    }

    public void write(List<String> lines, String filename) {
        File file = toFile(filename);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.e(TAG, "write: ", e);
                }
            }
        }

    }

    /**
     * Download and cache the ticks in a file
     * @param security The security containing a symbol
     * @param aggType The type of file to parse
     * @return list of tick data
     */
    public StandardPriceData downloadAndCachePriceData(Security security, AggType aggType) {

        boolean daily = TickType.DAILY.equals(aggType.getTickType());
        String filename = getAggFilename(security, aggType);

        String symbol = security.getSymbol();

        List<String> lines;

        switch (aggType) {
            case MIN5:
            case MIN15:
            case MIN30:
                lines = intraday(symbol);
                break;
            case DAY:
                lines = daily(symbol, 1);
                break;
            case WEEK:
                lines = daily(symbol, 3);
                break;
            case MONTH:
                lines = daily(symbol, 12);
                break;
            default:
                throw new IllegalArgumentException("No aggregation for aggtype: " + aggType);
        }

        Date date = new Date();
        write(lines, filename);

        PriceData priceData = daily ? reader.daily(lines) : reader.intraday(lines);
        priceData = aggregate(priceData, aggType);

        return new StandardPriceData(priceData, date);
    }

    /**
     * Read the daily tick file cache.
     * Test for existence of file prior to reading.
     * @param security The security containing a symbol
     * @param aggType The type of file to parse.
     * @return parseresult
     */
    public StandardPriceData readCachePriceData(Security security, AggType aggType) {

        boolean daily = TickType.DAILY.equals(aggType.getTickType());
        String filename = getAggFilename(security, aggType);

        File file = this.toFile(filename);
        Date date = new Date(file.lastModified());
        Log.i(TAG, "readCachePriceData: event reading " + security.getSymbol());
        long ts = System.currentTimeMillis();
        List<String> lines = this.read(filename);
        Log.i(TAG, "readCachePriceData: event read.filename " + security.getSymbol() + ": " + (System.currentTimeMillis() - ts) + " millis of lines " + lines.size());
        ts = System.currentTimeMillis();
        PriceData priceData = daily ? reader.daily(lines) : reader.intraday(lines);
        Log.i(TAG, "readCachePriceData: event reader.daily/intraday " + security.getSymbol() + ": " + (System.currentTimeMillis() - ts) + " millis");
        ts = System.currentTimeMillis();
        priceData = aggregate(priceData, aggType);
        Log.i(TAG, "readCachePriceData: event aggregate " + security.getSymbol()  + ": " + (System.currentTimeMillis() - ts) + " millis");

        return new StandardPriceData(priceData, date);
    }

    /**
     * Aggregate the data into the requested time frame.
     * The aggregation is based on simple number of entries.
     * @param priceData The "native" price data (either daily or intraday)
     *                  This is daily or 5 minute.
     * @param aggType The aggregation type desired
     * @return A new price data instance
     */
    private PriceData aggregate(PriceData priceData, AggType aggType) {

        List<Tick> ticks = priceData.getTicks();
        List<Tick> aggTicks;

        switch (aggType) {
            case MIN5:
                aggTicks = priceData.getTicks();
                break;
            case MIN15:
                aggTicks = makeAggTicks(ticks, 3);
                break;
            case MIN30:
                aggTicks = makeAggTicks(ticks, 6);
                break;
            case DAY:
                aggTicks = priceData.getTicks();
                break;
            case WEEK:
                aggTicks = makeAggTicks(ticks, 5, Calendar.WEEK_OF_YEAR);
                break;
            case MONTH:
                aggTicks = makeAggTicks(ticks, 20, Calendar.MONTH);
                break;
            default:
                throw new IllegalArgumentException("No aggregation for aggtype: " + aggType);
        }

        /*System.out.println("new agg ticks: " + aggTicks);*/

        return new PriceData(aggTicks, priceData.getErrors());
    }

    /**
     * Create an aggregated tick list based on an interval only.
     * @param ticks The ticks of the native file
     * @param interval The number of ticks to aggregate into a new agg tick.
     * @return The aggregated ticks
     */
    private List<Tick> makeAggTicks(List<Tick> ticks, int interval) {
        return makeAggTicks(ticks, interval, -1);
    }

    /**
     * Create an aggregated tick list to be used for the new data.
     * @param ticks The list of parsed ticks from the files.
     * @param interval The interval per new tick.
     * @param timeinterval A time interval instead of numerical one. -1 indicates ignore.
     *              Should be Calendar.FIELD value.
     * @return The aggregated ticks
     */
    private List<Tick> makeAggTicks(List<Tick> ticks, int interval, int timeinterval) {

        List<Tick> aggTicks = new ArrayList<>();
        int i, max = ticks.size();
        Tick aggTick = new Tick();
        int timeval = 0;
        int prevtimeval = 0;
        double currVol = 0.0d;

        for (i = 0; i < max; i++) {

            Tick curr = ticks.get(i);
            Date timestamp = curr.getTimestamp();

            if (timeinterval != -1) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(timestamp);
                timeval = cal.get(timeinterval);
            }

            boolean newperiod = timeinterval != -1 ? prevtimeval != timeval : i % interval == 0;

            if (i != 0 && newperiod) {
                aggTick.setTimestamp(curr.getTimestamp());
                aggTick.set("close", curr.get("close"));
                aggTick.set("adjclose", curr.get("adjclose"));
                aggTick.set("volume", currVol);
                aggTicks.add(aggTick);
                aggTick = new Tick();

                currVol = 0.0d;

            } else {

                if (aggTick.get("open") == null) {
                    aggTick.set("open", curr.get("open"));
                }
                Double low = curr.get("low");
                Double aggLow = aggTick.get("low");
                if (aggLow == null) {
                    aggTick.set("low", low);
                } else {
                    if (low < aggLow) {
                        aggTick.set("low", low);
                    }
                }
                Double high = curr.get("high");
                Double aggHigh = aggTick.get("high");
                if (aggLow == null) {
                    aggTick.set("high", high);
                } else {
                    if (high > aggHigh) {
                        aggTick.set("high", high);
                    }
                }
            }

            prevtimeval = timeval;
        }

        return aggTicks;
    }

    /**
     * Read the raw fileto a list of strings.
     * @param filename A specified filename
     * @return A list of strings of each line in the file
     */
    public List<String> read(String filename) {
        BufferedReader bw = null;
        File file = toFile(filename);
        List<String> lines = new ArrayList<>();
        try {
            bw = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bw.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.e(TAG, "read: ", e);
                }
            }
        }
        return lines;
    }

    /**
     * Convert a file name to a testable location.
     * @param filename A alnum only file
     * @return the file ref
     */
    public File toFile(String filename) {
        return new File(context.getCacheDir(), filename);
    }

    /**
     * Download the file.
     * @param url A url prepared to execute
     * @return A string containing the url data or a null value.
     */
    public List<String> download(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response;
        List<String> strings = null;
        try {
            response = client.newCall(request).execute();
            Reader reader = response.body().charStream();
            strings = CharStreams.readLines(reader);
        } catch (IOException e) {
            Log.e(TAG, "download: ", e);
        }
        return strings;
    }

    public String getAggFilename(Security security, AggType aggType) {
        return security.getSymbol() + "-" + aggType.name() + ".csv";
    }
}
