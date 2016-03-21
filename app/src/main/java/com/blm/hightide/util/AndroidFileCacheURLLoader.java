package com.blm.hightide.util;

import android.content.Context;
import android.util.Log;

import com.blm.corals.CoralUtils;
import com.blm.corals.loader.URLLoader;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class AndroidFileCacheURLLoader implements URLLoader {

    private static final String TAG = AndroidFileCacheURLLoader.class.getSimpleName();

    private CoralUtils utils = new CoralUtils();

    private Context context;

    private URLLoader loader;

    private boolean overwrite = false;

    private long timeInMillis = 0;

    public AndroidFileCacheURLLoader(Context context, URLLoader loader, long timeInMillis) {
        this.context = context;
        this.loader = loader;
        this.timeInMillis = timeInMillis;
    }

    @Override
    public List<String> load(String url) {

        Log.i(TAG, "loading: " + url);
        Charset utf8 = Charsets.UTF_8;
        String filename = utils.filename(url);
        File file = new File(context.getCacheDir(), filename);

        List<String> strings;

        try {

            if (!file.exists() ||
                overwrite ||
                file.exists() && (System.currentTimeMillis() - file.lastModified()) > timeInMillis) {
                strings = loader.load(url);
                Files.asCharSink(file, utf8).writeLines(strings, "\n");
            } else {
                strings = Files.asCharSource(file, utf8).readLines();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return strings;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
