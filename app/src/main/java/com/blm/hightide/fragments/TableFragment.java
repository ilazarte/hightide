package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.events.TableLoadComplete;
import com.blm.hightide.events.TableLoadStart;
import com.blm.hightide.fragments.internal.AbstractAggTypeFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.TickType;

import org.greenrobot.eventbus.EventBus;
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

public class TableFragment extends AbstractAggTypeFragment {

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

        private final DecimalFormat df = new DecimalFormat("#.00");
        private final SimpleDateFormat sdf;

        Binder binder = new Binder();

        public TickHolder(View view, TickType tickType) {
            super(view);
            String fmtStr = TickType.DAILY.equals(tickType) ? "MM-dd-yyyy" : "MM-dd HH:mm";
            sdf = new SimpleDateFormat(fmtStr, Locale.US);
            ButterKnife.bind(binder, view);
        }

        public void bind(Tick tick) {
            binder.timestamp.setText(sdf.format(tick.getTimestamp()));
            binder.open.setText(df.format(tick.get("open")));
            binder.high.setText(df.format(tick.get("high")));
            binder.low.setText(df.format(tick.get("low")));
            binder.close.setText(df.format(tick.get("close")));
            binder.volume.setText(String.format("%s", tick.get("volume").intValue()));

            Double adjclose = tick.get("adjclose");
            binder.adjclose.setText(adjclose != null ? df.format(adjclose) : "");
        }
    }

    class TickAdapter extends RecyclerView.Adapter<TickHolder> {

        private List<Tick> items;

        private TickType tickType;

        public TickAdapter(List<Tick> items, TickType tickType) {
            this.items = items;
            this.tickType = tickType;
        }

        @Override
        public TickHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.list_item_table, parent, false);
            return new TickHolder(view, tickType);
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

    @Bind(R.id.textview_title)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    private Security security;

    public static TableFragment newInstance() {
        Bundle args = new Bundle();
        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

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
        table.setAdapter(new TickAdapter(new ArrayList<>(), TickType.DAILY));

        return view;
    }

    @Override
    public int stubLayout() {
        return R.layout.stub_table;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onTableLoadComplete(TableLoadComplete event) {
        security = event.getSecurity();
        List<Tick> ticks = security.getStandardPriceData().getTicks();
        Collections.reverse(ticks);

        this.updateAggType(event.getAggType());
        textView.setText(security.getSymbol());
        table.setAdapter(this.getAnimationAdapter(new TickAdapter(ticks, this.getAggType().getTickType())));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_table, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                EventBus.getDefault().post(new TableLoadStart(security.getSymbol(), this.getAggType()));
                break;
            default:
                break;
        }
        return false;
    }
}
