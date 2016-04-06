package com.blm.hightide.util;

import android.content.Context;
import android.util.Log;

import com.blm.corals.PriceData;
import com.blm.corals.provider.YahooPriceURL;
import com.blm.corals.provider.YahooTickReader;
import com.blm.hightide.model.Security;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
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

    public PriceData readDaily(List<String> lines) {
        return reader.daily(lines);
    }

    public List<String> intraday(String symbol) {
        String url = urls.intraday(symbol);
        return download(url);
    }

    public PriceData readIntraday(List<String> lines) {
        return this.reader.intraday(lines);
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
     * @return list of tick data
     */
    public PriceData downloadAndCacheDailyPriceData(Security security) {
        List<String> lines = daily(security.getSymbol());
        write(lines, security.getDailyFilename());
        return readDaily(lines);
    }

    /**
     * Read the daily tick file cache.
     * Test for existence of file prior to reading.
     * @param security The security containing a symbol
     * @return parseresult
     */
    public PriceData readPriceData(Security security) {
        List<String> lines = this.read(security.getDailyFilename());
        return this.readDaily(lines);
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
