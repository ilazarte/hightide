package com.blm.hightide.util;

import android.content.Context;
import android.util.Log;

import com.blm.corals.PriceData;
import com.blm.corals.provider.YahooPriceURL;
import com.blm.corals.provider.YahooTickReader;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.TickType;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
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

    private Charset utf8 = Charsets.UTF_8;

    private Context context;
    private YahooPriceURL urls = new YahooPriceURL();
    private YahooTickReader reader = new YahooTickReader();

    public YahooPriceHelper(Context context) {
        this.context = context;
    }

    public List<String> daily(String symbol) {
        String url = urls.daily(symbol);
        return download(url);
    }

    public List<String> intraday(String symbol) {
        String url = urls.intraday(symbol);
        return download(url);
    }

    public void write(List<String> lines, String filename) {
        File file = toFile(filename);
        try {
              Files.asCharSink(file, utf8).writeLines(lines, "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Download and cache the ticks in a file
     * @param security The security containing a symbol
     * @param tickType The type of file to parse
     * @return list of tick data
     */
    public StandardPriceData downloadAndCachePriceData(Security security, TickType tickType) {

        boolean daily = TickType.DAILY.equals(tickType);
        String filename = daily ?
                security.getDailyFilename() :
                security.getIntradayFilename();

        String symbol = security.getSymbol();

        List<String> lines = daily ?
                daily(symbol) :
                intraday(symbol);

        Date date = new Date();
        write(lines, filename);
        PriceData priceData = daily ?
                reader.daily(lines) :
                reader.intraday(lines);

        return new StandardPriceData(priceData, date);
    }

    /**
     * Read the daily tick file cache.
     * Test for existence of file prior to reading.
     * @param security The security containing a symbol
     * @param tickType The type of file to parse.
     * @return parseresult
     */
    public StandardPriceData readCachePriceData(Security security, TickType tickType) {

        boolean daily = TickType.DAILY.equals(tickType);
        String filename = daily ?
                security.getDailyFilename() :
                security.getIntradayFilename();

        File file = this.toFile(filename);
        Date date = new Date(file.lastModified());
        List<String> lines = this.read(filename);

        PriceData priceData = daily ? reader.daily(lines) : reader.intraday(lines);
        return new StandardPriceData(priceData, date);
    }

    /**
     * Read the raw fileto a list of strings.
     * @param filename
     * @return
     */
    public List<String> read(String filename) {
        File file = toFile(filename);
        List<String> lines;
        try {
            lines = Files.asCharSource(file, utf8).readLines();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
