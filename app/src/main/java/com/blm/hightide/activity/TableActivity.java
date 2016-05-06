package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.blm.hightide.R;
import com.blm.hightide.activity.internal.AbstractBaseActivity;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.events.TableLoadComplete;
import com.blm.hightide.events.TableLoadStart;
import com.blm.hightide.fragments.TableFragment;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.TickType;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TableActivity extends AbstractBaseActivity {

    @SuppressWarnings("unused")
    private static final String TAG = TableActivity.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "com.blm.hightide.activity.SECURITY_SYMBOL";

    public static Intent newIntent(Context context, String symbol) {
        Intent intent = new Intent(context, TableActivity.class);
        intent.putExtra(SECURITY_SYMBOL, symbol);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return TableFragment.newInstance();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onGlobalLayout(GlobalLayout event) {
        String symbol = this.getIntent().getExtras().getString(SECURITY_SYMBOL);
        onTableLoadStart(new TableLoadStart(symbol, TickType.DAILY));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    @SuppressWarnings("unused")
    public void onTableLoadStart(TableLoadStart event) {

        toast(R.string.read_file);

        StockService service = this.getStockService();
        String symbol = event.getSymbol();
        TickType tickType = event.getTickType();

        service.findSecurity(symbol)
                .flatMap(security -> service.setStandardPriceData(security, tickType, true))
                .subscribe(security -> {
                    EventBus.getDefault().post(new TableLoadComplete(security, tickType));
                });
    }
}
