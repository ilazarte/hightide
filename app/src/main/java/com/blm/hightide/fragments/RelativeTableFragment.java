package com.blm.hightide.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.blm.hightide.events.RelativeTableLoadComplete;
import com.blm.hightide.events.RelativeTableLoadStart;
import com.blm.hightide.events.WatchlistLoadFilesStart;
import com.blm.hightide.model.MovingAvgGridParams;
import com.blm.hightide.model.RelativeTick;
import com.blm.hightide.model.Watchlist;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class RelativeTableFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeTableFragment.class.getSimpleName();

    abstract class BaseHolder<T> extends RecyclerView.ViewHolder {
        public BaseHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(T o);
    }

    class RelativeTickHolder extends BaseHolder<RelativeTick> {

        private final DecimalFormat df = new DecimalFormat("#.00");

        @Bind(R.id.grid_item_textview)
        TextView value;

        public RelativeTickHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(RelativeTick tick) {
            String text = tick.getSymbol() + "\n" + df.format(tick.getValue());
            value.setBackgroundColor(tick.getColor());
            value.setText(text);
        }
    }

    class StringHolder extends BaseHolder<String> {

        @Bind(R.id.grid_item_textview)
        TextView dateview;

        public StringHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(String str) {
            dateview.setText(str);
        }
    }

    class GridAdapter extends RecyclerView.Adapter<BaseHolder> {

        private List<Object> items;

        private int totalRowCount;

        public GridAdapter(List<Object> items, int rowCount) {
            this.items = items;
            this.totalRowCount = rowCount + 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position % totalRowCount == 0 ? 0 : 1;
        }

        @Override
        public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.grid_item_textview, parent, false);
            if (viewType == 0) {
                return new StringHolder(view);
            } else {
                return new RelativeTickHolder(view);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(BaseHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    @Bind(R.id.toolbar_settings)
    Toolbar toolbar;

    ActionBar supportActionBar;

    @Bind(R.id.spinner_number)
    Spinner spinnerNumber;

    @Bind(R.id.spinner_average_length)
    Spinner spinnerAverageLength;

    @Bind(R.id.textview_watchlist_name)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    private List<Integer> numbers = new ArrayList<>();

    private boolean avgLengthReset = true;

    private boolean numberReset = true;

    private Watchlist watchlist;

    private MovingAvgGridParams params;

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

    public static RelativeTableFragment newInstance() {
        Bundle args = new Bundle();

        RelativeTableFragment fragment = new RelativeTableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onRelativeTableLoadComplete(RelativeTableLoadComplete event) {

        List<Object> gridList = event.getGridList();
        params = event.getParams();
        watchlist = event.getWatchlist();

        int lengthValue = numbers.indexOf(params.getLength());
        int avgLengthValue = numbers.indexOf(params.getAvgLength());

        spinnerNumber.setSelection(lengthValue);
        spinnerAverageLength.setSelection(avgLengthValue);

        textView.setText(watchlist.getName());
        table.setAdapter(new GridAdapter(gridList, event.getParams().getTopLength()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_relative_table, container, false);
        ButterKnife.bind(this, view);

        supportActionBar = this.getSupportActionBar(toolbar);

        for (int i = 10; i < 101; i += 10) {
            numbers.add(i);
        }

        Context themedContext = supportActionBar.getThemedContext();

        spinnerNumber.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));
        spinnerAverageLength.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 7);
        gridLayoutManager.setAutoMeasureEnabled(true);

        table.setLayoutManager(gridLayoutManager);
        table.setAdapter(new GridAdapter(new ArrayList<>(), 0));

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_relative_table, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                RelativeTableLoadStart event = new RelativeTableLoadStart(watchlist.getId(), params, true);
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
