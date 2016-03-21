package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blm.hightide.R;
import com.blm.hightide.events.LineDataAvailableEvent;
import com.blm.hightide.events.LoadFilesStartEvent;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RelativePerformanceFragment extends Fragment {

    private static final String TAG = RelativePerformanceFragment.class.getSimpleName();

    private static final String WATCHLIST_ID = "WATCHLIST_ID";

    private StockService service = new StockService();

    @Bind(R.id.chart)
    LineChart chart;

    public static RelativePerformanceFragment newInstance(int watchlistId) {
        Bundle args = new Bundle();
        args.putInt(WATCHLIST_ID, watchlistId);

        RelativePerformanceFragment fragment = new RelativePerformanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        service.init(this.getActivity());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLoadRequest(LoadFilesStartEvent event) {

        int id = event.getWatchlistId();
        Watchlist watchlist = service.findWatchlist(id);
        service.findSecurities(watchlist);
        service.readDailyTicks(watchlist);

        int lastN = 60;
        int avgLen = 20;
        int lastNTicks = lastN - avgLen;

        List<ILineDataSet> relative = service.getRelativeForAverage(watchlist, lastN, avgLen);
        List<String> xvals = service.toXAxis(watchlist.getSecurities().get(0).getTicks(), lastNTicks);
        LineData data = new LineData(xvals, relative);

        EventBus.getDefault().post(new LineDataAvailableEvent(data));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLineDataAvailable(LineDataAvailableEvent event) {
        chart.setData(event.getLineData());
        chart.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_relative_performance, container, false);
        ButterKnife.bind(this, view);

        chart.setNoDataText(this.getString(R.string.loading));
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                LineData lineData = chart.getLineData();
                XAxis xAxis = chart.getXAxis();

                ILineDataSet dataset = lineData.getDataSetByIndex(dataSetIndex);
                String date = xAxis.getValues().get(h.getXIndex());

                String msg = date + ": " + dataset.getLabel() + " => " + e.getVal();
                Toast.makeText(
                        RelativePerformanceFragment.this.getActivity(),
                        msg,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        int watchlistId = this.getArguments().getInt(WATCHLIST_ID);
        EventBus.getDefault().post(new LoadFilesStartEvent(watchlistId));

        return view;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
