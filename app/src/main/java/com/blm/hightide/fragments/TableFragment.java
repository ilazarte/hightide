package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.LoginFilter;
import android.util.Log;
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

import java.text.DecimalFormat;
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

        Log.i(TAG, "onCreateView: creating table fragment");
        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ButterKnife.bind(this, view);

        String symbol = getArguments().getString(SECURITY_SYMBOL);
        Log.i(TAG, "onCreateView: posting security start");
        EventBus.getDefault().post(new SecurityLoadStart(symbol));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onSecurityLoad(SecurityLoadComplete event) {

        Log.i(TAG, "onSecurityLoad: received security load");
        Security security = event.getSecurity();

        textView.setText(security.getSymbol());
        setTableData(security.getStandardPriceData());
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void setTableData(StandardPriceData priceData) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        DecimalFormat df = new DecimalFormat("#.00");

        HorizontalScrollView.LayoutParams lp = new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        table.setLayoutParams(lp);

        List<Tick> ticks = priceData.getTicks();
        Log.i(TAG, "setTableData: table data: " + ticks);
        table.addView(tableRow("ROW", "TIMESTAMP", "OPEN", "HIGH", "LOW", "CLOSE", "ADJCLOSE"));
        Integer i = 0;

        for (Tick tick : ticks) {
            TableRow row = tableRow(
                    i.toString(),
                    sdf.format(tick.getTimestamp()),
                    df.format(tick.get("open")),
                    df.format(tick.get("high")),
                    df.format(tick.get("low")),
                    df.format(tick.get("close")),
                    df.format(tick.get("adjclose")));
            table.addView(row);
            i++;
            Log.i(TAG, "setTableData: setting row: " + i);
        }
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
