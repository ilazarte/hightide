package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.fragments.internal.BaseFragment;
import com.blm.hightide.model.Security;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO convert this to include a dropdown to be able to select any stock
 */
public class TableFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = TableFragment.class.getSimpleName();

    class Binder {
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
    }

    class TickHolder extends RecyclerView.ViewHolder {

        private final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        private final DecimalFormat df = new DecimalFormat("#.00");

        Binder binder = new Binder();

        public TickHolder(View view) {
            super(view);
            ButterKnife.bind(binder, view);
        }

        public void bind(Tick tick) {
            binder.timestamp.setText(sdf.format(tick.getTimestamp()));
            binder.open.setText(df.format(tick.get("open")));
            binder.high.setText(df.format(tick.get("high")));
            binder.low.setText(df.format(tick.get("low")));
            binder.close.setText(df.format(tick.get("close")));
            binder.volume.setText(String.format("%s", tick.get("volume").intValue()));
            binder.adjclose.setText(df.format(tick.get("adjclose")));
        }
    }

    class TickAdapter extends RecyclerView.Adapter<TickHolder> {

        private List<Tick> items;

        public TickAdapter(List<Tick> items) {
            this.items = items;
        }

        @Override
        public TickHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.list_item_daily_tick, parent, false);
            return new TickHolder(view);
        }

        @Override
        public void onBindViewHolder(TickHolder holder, int position) {
            Tick item = items.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    Binder header = new Binder();

    @Bind(R.id.textview_security_symbol)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    public static TableFragment newInstance() {
        Bundle args = new Bundle();
        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(header, view);

        header.timestamp.setText(R.string.timestamp_label);
        header.timestamp.setSingleLine();
        header.open.setText(R.string.open_label);
        header.high.setText(R.string.high_label);
        header.low.setText(R.string.low_label);
        header.close.setText(R.string.close_label);
        header.volume.setText(R.string.volume_label);
        header.adjclose.setText(R.string.adjclose_label);
        header.adjclose.setSingleLine();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        table.setLayoutManager(linearLayoutManager);
        table.setAdapter(new TickAdapter(new ArrayList<>()));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onSecurityLoad(SecurityLoadComplete event) {
        Security security = event.getSecurity();
        List<Tick> ticks = security.getStandardPriceData().getTicks();
        Collections.reverse(ticks);

        textView.setText(security.getSymbol());
        table.setAdapter(new TickAdapter(ticks));
    }
}
