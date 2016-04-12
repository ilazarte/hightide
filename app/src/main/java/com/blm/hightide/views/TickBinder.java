package com.blm.hightide.views;

import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;

public class TickBinder implements Binder<Tick> {

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

    @Override
    public Binder<Tick> create() {
        return new TickBinder();
    }

    @Override
    public void bind(Tick tick, int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-yyyy", Locale.US);
        DecimalFormat df = new DecimalFormat("#.00");

        timestamp.setText(sdf.format(tick.getTimestamp()));
        open.setText(df.format(tick.get("open")));
        high.setText(df.format(tick.get("high")));
        low.setText(df.format(tick.get("low")));
        close.setText(df.format(tick.get("close")));
        volume.setText(String.format("%s", tick.get("volume").intValue()));
        adjclose.setText(df.format(tick.get("adjclose")));
    }
}
