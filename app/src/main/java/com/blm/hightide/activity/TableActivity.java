package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.blm.hightide.R;
import com.blm.hightide.events.FileLoadStart;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.fragments.FileFragment;
import com.blm.hightide.fragments.TableFragment;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TableActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = TableActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    private StockService service = new StockService();

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, TableActivity.class);
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
        return TableFragment.newInstance(symbol);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onSecurityLoadStart(SecurityLoadStart event) {

        Log.i(TAG, "onFileLoadStart: received start event");
        toast(R.string.read_file);

        String symbol = event.getSymbol();
        service.findSecurity(symbol)
                .flatMap(security -> service.setStandardPriceData(security, true))
                .subscribe(security -> {
                    SecurityLoadComplete complete = new SecurityLoadComplete(security);
                    Log.i(TAG, "onFileLoadStart: posting security load complete: " + complete.getSecurity());
                    EventBus.getDefault().post(complete);
                });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
