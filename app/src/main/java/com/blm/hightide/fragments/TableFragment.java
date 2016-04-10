package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.model.Security;
import com.blm.hightide.util.StandardPriceData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TableFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = TableFragment.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "SECURITY_SYMBOL";

    private static final TableRow.LayoutParams cellLayout = new TableRow.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    private static final TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);


    @Bind(R.id.textview_security_symbol)
    TextView textView;

    @Bind(R.id.table_security_data)
    TableLayout table;

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
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        ButterKnife.bind(this, view);

        String symbol = getArguments().getString(SECURITY_SYMBOL);
        EventBus.getDefault().post(new SecurityLoadStart(symbol));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onSecurityLoad(SecurityLoadComplete event) {

        Security security = event.getSecurity();

        textView.setText(security.getSymbol());
        table = toTableLayout(security.getStandardPriceData());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private TableLayout toTableLayout(StandardPriceData priceData) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

        TableLayout tl = new TableLayout(this.getActivity());
        HorizontalScrollView.LayoutParams lp = new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tl.setLayoutParams(lp);

        List<Tick> ticks = priceData.getTicks();
        tl.addView(tableRow("TICKNUM", "TIMESTAMP", "OPEN", "HIGH", "LOW", "ADJCLOSE"));
        Integer i = 0;

        for (Tick tick : ticks) {
            TableRow row = tableRow(
                    i.toString(),
                    sdf.format(tick.getTimestamp()),
                    tick.get("open").toString(),
                    tick.get("high").toString(),
                    tick.get("low").toString(),
                    tick.get("adjclose").toString());
            tl.addView(row);
            i++;
        }

        return tl;
    }

    private TableRow tableRow(String... texts) {
        TableRow tableRow = new TableRow(this.getActivity());
        tableRow.setLayoutParams(rowLayout);
        for (String text : texts) {
            tableRow.addView(textView(text));
        }
        return tableRow;
    }

    private TextView textView(String text) {
        TextView tv = new TextView(this.getActivity());
        tv.setLayoutParams(cellLayout);
        tv.setText(text);
        return tv;
    }
}
