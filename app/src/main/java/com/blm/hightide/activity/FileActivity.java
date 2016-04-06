package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.events.FileDataAvailable;
import com.blm.hightide.events.FileLoadStart;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.fragments.FileFragment;
import com.blm.hightide.model.FileData;
import com.blm.hightide.service.StockService;
import com.github.mikephil.charting.data.LineData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FileActivity extends AbstractBaseActivity {

    private static final String TAG = FileActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    private StockService service = new StockService();

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(SECURITY_SYMBOL, symbol);
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
        String symbol = this.getIntent().getExtras().getString(SECURITY_SYMBOL);
        return FileFragment.newInstance(symbol);
    }

    /**
     * @param event the file load start
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onFileLoadStart(FileLoadStart event) {

        toast(R.string.read_file);

        String symbol = event.getSymbol();
        service.findSecurity(symbol)
                .flatMap(security -> service.setPriceData(security, true))
                .subscribe(security -> {
                    FileData fileData = service.getFileData(security);
                    FileDataAvailable available = new FileDataAvailable(symbol, fileData);
                    EventBus.getDefault().post(available);
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
