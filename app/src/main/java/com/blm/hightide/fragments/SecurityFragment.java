package com.blm.hightide.fragments;

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
import com.blm.hightide.events.SecurityChartDataAvailable;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.fragments.internal.AbstractToolbarParamsFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.util.FrequencyFormatter;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

public class SecurityFragment extends AbstractToolbarParamsFragment {

    @SuppressWarnings("unused")
    private static final String TAG = SecurityFragment.class.getSimpleName();

    @Bind(R.id.textview_title)
    TextView title;

    @Bind(R.id.textview_datapoint)
    TextView datapoint;

    @Bind(R.id.chart)
    CombinedChart chart;

    private Security security;

    public static SecurityFragment newInstance() {
        Bundle args = new Bundle();
        SecurityFragment fragment = new SecurityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onLineDataAvailable(SecurityChartDataAvailable event) {

        security = event.getSecurity();
        this.updateParams(event.getParams());

        title.setText(security.getSymbol());
        chart.setData(event.getCombinedData());
        chart.invalidate();
    }

    @Override
    public int stubLayout() {
        return R.layout.fragment_security;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        chart.setNoDataText(this.getString(R.string.loading));
        chart.setDescription(null);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight h) {
                CandleData data = chart.getCandleData();
                XAxis xAxis = chart.getXAxis();

                ICandleDataSet dataset = data.getDataSetByIndex(dataSetIndex);
                String date = xAxis.getValues().get(h.getXIndex());
                String val = Float.valueOf(entry.getVal()).toString();
                String msg = date + " " + dataset.getLabel() + " " + val;

                datapoint.setText(msg);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_security, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                SecurityLoadStart event = new SecurityLoadStart(security.getSymbol(), this.getParams());
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
