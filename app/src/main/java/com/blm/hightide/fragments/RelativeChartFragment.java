package com.blm.hightide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailable;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.model.MovingAvgParams;
import com.blm.hightide.model.Watchlist;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class RelativeChartFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeChartFragment.class.getSimpleName();

    @Bind(R.id.toolbar_settings)
    Toolbar toolbar;

    ActionBar supportActionBar;

    @Bind(R.id.spinner_number)
    Spinner spinnerNumber;

    @Bind(R.id.spinner_average_length)
    Spinner spinnerAverageLength;

    @Bind(R.id.textview_title)
    TextView title;

    @Bind(R.id.textview_datapoint)
    TextView datapoint;

    @Bind(R.id.chart)
    LineChart chart;

    private List<Integer> numbers = new ArrayList<>();

    private Watchlist watchlist;

    private MovingAvgParams params;

    private boolean avgLengthReset = true;

    private boolean numberReset = true;

    @OnItemSelected(R.id.spinner_number)
    @SuppressWarnings("unused")
    void selectNumber(int position) {
        if (numberReset) {
            numberReset = false;
            return;
        }
        params.setLength(numbers.get(position));
    }

    @OnItemSelected(R.id.spinner_average_length)
    @SuppressWarnings("unused")
    void selectAverageLength(int position) {
        if (avgLengthReset) {
            avgLengthReset = false;
            return;
        }
        params.setAvgLength(numbers.get(position));
    }

    public static RelativeChartFragment newInstance() {
        Bundle args = new Bundle();
        RelativeChartFragment fragment = new RelativeChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onLineDataAvailable(LineDataAvailable event) {
        params = event.getParams();
        watchlist = event.getWatchlist();

        int lengthValue = numbers.indexOf(params.getLength());
        int avgLengthValue = numbers.indexOf(params.getAvgLength());

        spinnerNumber.setSelection(lengthValue);
        spinnerAverageLength.setSelection(avgLengthValue);

        title.setText(event.getWatchlist().getName());
        chart.setData(event.getLineData());
        chart.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_relative_chart, container, false);
        ButterKnife.bind(this, view);

        supportActionBar = this.getSupportActionBar(toolbar);

        for (int i = 10; i < 101; i += 10) {
            numbers.add(i);
        }

        Context themedContext = supportActionBar.getThemedContext();

        spinnerNumber.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));
        spinnerAverageLength.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));

        chart.setNoDataText(this.getString(R.string.loading));
        /*chart.setAutoScaleMinMaxEnabled(true);
        chart.setVisibleXRangeMaximum(40);*/
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
                WatchlistLoadFilesStart event = new WatchlistLoadFilesStart(watchlist.getId(), params);
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
