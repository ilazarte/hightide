package com.blm.hightide.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.RelativeChartDataAvailable;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.fragments.internal.AbstractToolbarParamsFragment;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

public class RelativeChartFragment extends AbstractToolbarParamsFragment {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeChartFragment.class.getSimpleName();

    @Bind(R.id.textview_title)
    TextView title;

    @Bind(R.id.textview_datapoint)
    TextView datapoint;

    @Bind(R.id.chart)
    LineChart chart;

    private Watchlist watchlist;

    public static RelativeChartFragment newInstance() {
        Bundle args = new Bundle();
        RelativeChartFragment fragment = new RelativeChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int stubLayout() {
        return R.layout.fragment_relative_chart;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onLineDataAvailable(RelativeChartDataAvailable event) {
        watchlist = event.getWatchlist();

        updateParams(event.getParams());

        title.setText(event.getWatchlist().getName());
        chart.setData(event.getLineData());
        chart.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        chart.setNoDataText(this.getString(R.string.loading));
        chart.getLegend().setWordWrapEnabled(true);
        chart.setDescription(null);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight h) {
                LineData lineData = chart.getLineData();
                XAxis xAxis = chart.getXAxis();

                ILineDataSet dataset = lineData.getDataSetByIndex(dataSetIndex);
                String date = xAxis.getValues().get(h.getXIndex());
                String val = Float.valueOf(entry.getVal()).toString();
                String msg = date + " " + dataset.getLabel() + " " + val;

                datapoint.setText(msg);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        LimitLine ll = new LimitLine(1.0f, "Minimum");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(2f);
        ll.enableDashedLine(20f, 20f, 0f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.addLimitLine(ll);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_relative_chart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                WatchlistLoadFilesStart event = new WatchlistLoadFilesStart(watchlist.getId(), this.getParams());
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
