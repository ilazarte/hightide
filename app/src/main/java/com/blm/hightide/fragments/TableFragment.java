package com.blm.hightide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.model.Security;
import com.blm.hightide.util.RecyclerViewAdapterFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TableFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = TableFragment.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "SECURITY_SYMBOL";

    class TickAdapterFactory extends RecyclerViewAdapterFactory<Tick> {

        @Bind(R.id.list_item_table_num)
        TextView num;

        @Bind(R.id.list_item_table_timestamp)
        TextView timestamp;

        @Bind(R.id.list_item_table_open)
        TextView open;

        @Bind(R.id.list_item_table_high)
        TextView high;

        @Bind(R.id.list_item_table_low)
        TextView low;

        @Bind(R.id.list_item_table_close)
        TextView close;

        @Bind(R.id.list_item_table_volume)
        TextView volume;

        @Bind(R.id.list_item_table_adjclose)
        TextView adjclose;

        public TickAdapterFactory(Context context, int layoutResource) {
            super(context, layoutResource);
        }

        @Override
        public void create(View view) {
            ButterKnife.bind(this, view);
        }

        @Override
        public void bind(Tick tick, int i) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            DecimalFormat df = new DecimalFormat("#.00");

            num.setText(String.format("%s", i));
            timestamp.setText(sdf.format(tick.getTimestamp()));
            open.setText(df.format(tick.get("open")));
            high.setText(df.format(tick.get("high")));
            low.setText(df.format(tick.get("low")));
            close.setText(df.format(tick.get("close")));
            volume.setText(String.format("%s", tick.get("volume")));
            adjclose.setText(df.format(tick.get("adjclose")));
        }
    }

    TickAdapterFactory factory;

    @Bind(R.id.textview_security_symbol)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    public static TableFragment newInstance(String symbol) {
        Bundle args = new Bundle();
        args.putString(SECURITY_SYMBOL, symbol);

        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ButterKnife.bind(this, view);

        factory = new TickAdapterFactory(this.getActivity(), R.layout.list_item_daily_tick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setAutoMeasureEnabled(true);
        table.setLayoutManager(layoutManager);
        table.setAdapter(factory.adapter(new ArrayList<>()));

        String symbol = getArguments().getString(SECURITY_SYMBOL);
        EventBus.getDefault().post(new SecurityLoadStart(symbol));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onSecurityLoad(SecurityLoadComplete event) {
        Security security = event.getSecurity();

        textView.setText(security.getSymbol());
        table.setAdapter(factory.adapter(security.getStandardPriceData().getTicks()));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
