package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.RelativeTableLoadComplete;
import com.blm.hightide.events.RelativeTableLoadStart;
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

public class RelativeTableFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeTableFragment.class.getSimpleName();

    private static final String WATCHLIST_ID = "WATCHLIST_ID";

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

        public GridAdapter(List<Object> items, int topN) {
            this.items = items;
            this.totalRowCount = topN + 1;
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

    @Bind(R.id.textview_watchlist_name)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    public static RelativeTableFragment newInstance(int watchlistId) {
        Bundle args = new Bundle();
        args.putInt(WATCHLIST_ID, watchlistId);

        RelativeTableFragment fragment = new RelativeTableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_relative_table, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 7);
        gridLayoutManager.setAutoMeasureEnabled(true);

        table.setLayoutManager(gridLayoutManager);
        table.setAdapter(new GridAdapter(new ArrayList<>(), 0));

        int watchlistId = this.getArguments().getInt(WATCHLIST_ID);
        EventBus.getDefault().post(new RelativeTableLoadStart(watchlistId, true));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onRelativeTableLoadComplete(RelativeTableLoadComplete event) {

        List<Object> gridList = event.getGridList();
        Watchlist watchlist = event.getWatchlist();

        textView.setText(watchlist.getName());
        table.setAdapter(new GridAdapter(gridList, event.getTopN()));
    }
}
