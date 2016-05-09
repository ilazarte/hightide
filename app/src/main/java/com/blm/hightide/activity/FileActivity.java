package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.activity.internal.AbstractBaseActivity;
import com.blm.hightide.events.FileDataAvailable;
import com.blm.hightide.events.FileLoadStart;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.fragments.FileFragment;
import com.blm.hightide.model.AggType;
import com.blm.hightide.model.FileData;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FileActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = FileActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(SECURITY_SYMBOL, symbol);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return FileFragment.newInstance();
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        String symbol = this.getIntent().getExtras().getString(SECURITY_SYMBOL);
        onFileLoadStart(new FileLoadStart(symbol, AggType.DAY));
    }

    /**
     * @param event the file load start
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onFileLoadStart(FileLoadStart event) {

        toast(R.string.read_file);

        StockService service = this.getStockService();
        String symbol = event.getSymbol();
        AggType aggType = event.getAggType();

        service.findSecurity(symbol)
                .flatMap(security -> service.setStandardPriceData(security, aggType, true))
                .subscribe(security -> {
                    FileData fileData = service.getFileData(security, aggType);
                    FileDataAvailable available = new FileDataAvailable(symbol, fileData, aggType);
                    EventBus.getDefault().post(available);
                });
    }
}
