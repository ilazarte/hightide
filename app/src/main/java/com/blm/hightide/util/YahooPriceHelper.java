package com.blm.hightide.util;

import android.content.Context;

import com.blm.corals.DateHelper;
import com.blm.corals.Interval;
import com.blm.corals.PeriodType;
import com.blm.corals.Tick;
import com.blm.corals.YahooTickReader;
import com.blm.corals.loader.HTTPURLLoader;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

public class YahooPriceHelper {

    private Charset utf8 = Charsets.UTF_8;

    private Context context;
    private HTTPURLLoader loader = new HTTPURLLoader();
    private DateHelper dateHelper = new DateHelper();
    private YahooTickReader reader = new YahooTickReader();

    public YahooPriceHelper(Context context) {
        this.context = context;
    }

    public List<String> daily(String symbol) {
        String url = this.makeDownloadHistorialUrl(symbol);
        return this.loader.load(url);
    }

    public List<Tick> readDaily(List<String> lines) {
        return this.reader.historical(lines);
    }

    public List<String> intraday(String symbol) {
        String url = this.makeDownloadIntradayUrl(symbol);
        return this.loader.load(url);
    }

    public List<Tick> readIntraday(List<String> lines) {
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

    public List<String> read(String filename) {
        File file = toFile(filename);
        List<String> lines = null;
        try {
            lines = Files.asCharSource(file, utf8).readLines();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }

    private File toFile(String filename) {
        return new File(context.getCacheDir(), filename);
    }

    private String makeDownloadHistorialUrl(String symbol) {
        Interval iv = this.dateHelper.makeDateTimes(PeriodType.YEAR, 1);
        return this.makeDownloadHistoricalUrl(symbol, iv);
    }

    private String makeDownloadHistoricalUrl(String symbol, Interval iv) {
        Date start = iv.getBegin();
        Date end = iv.getEnd();
        String url = String.format("http://ichart.finance.yahoo.com/table.csv?s=%s&a=%s&b=%s&c=%s&d=%s&e=%s&f=%s&g=d&ignore=.csv", new Object[]{symbol, Integer.valueOf(this.dateHelper.month(start)), Integer.valueOf(this.dateHelper.day(start)), Integer.valueOf(this.dateHelper.year(start)), Integer.valueOf(this.dateHelper.month(end)), Integer.valueOf(this.dateHelper.day(end)), Integer.valueOf(this.dateHelper.year(end))});
        return url;
    }

    private String makeDownloadIntradayUrl(String symbol) {
        return String.format("http://chartapi.finance.yahoo.com/instrument/1.0/%s/chartdata;type=quote;range=5d/csv", new Object[]{symbol});
    }
}
