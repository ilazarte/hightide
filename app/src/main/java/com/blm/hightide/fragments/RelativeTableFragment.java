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

import com.blm.hightide.R;
import com.blm.hightide.events.RelativeTableLoadComplete;
import com.blm.hightide.events.RelativeTableLoadStart;
import com.blm.hightide.fragments.internal.AbstractToolbarParamsFragment;
import com.blm.hightide.model.RelativeGridRow;
import com.blm.hightide.model.RelativeTick;
import com.blm.hightide.model.StudyGridParams;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.views.AdapterFactory;
import com.blm.hightide.views.Binder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class RelativeTableFragment extends AbstractToolbarParamsFragment {

    @SuppressWarnings("unused")
    private static final String TAG = RelativeTableFragment.class.getSimpleName();

    class RowBinder implements Binder<RelativeGridRow> {

        private final DecimalFormat df = new DecimalFormat("#.00");

        @Bind(R.id.list_item_timestamp)
        TextView timestamp;

        @Bind(R.id.list_item_1)
        TextView rank1;

        @Bind(R.id.list_item_2)
        TextView rank2;

        @Bind(R.id.list_item_3)
        TextView rank3;

        @Bind(R.id.list_item_4)
        TextView rank4;

        @Bind(R.id.list_item_5)
        TextView rank5;

        @Bind(R.id.list_item_6)
        TextView rank6;

        @Override
        public Binder<RelativeGridRow> create() {
            return new RowBinder();
        }

        @Override
        public void bind(RelativeGridRow row, int position) {
            timestamp.setText(row.getTimestamp());
            bind(rank1, row.getTicks().get(0));
            bind(rank2, row.getTicks().get(1));
            bind(rank3, row.getTicks().get(2));
            bind(rank4, row.getTicks().get(3));
            bind(rank5, row.getTicks().get(4));
            bind(rank6, row.getTicks().get(5));
        }

        private void bind(TextView view, RelativeTick tick) {
            String text = tick.getSymbol() + "\n" + df.format(tick.getValue());
            view.setText(text);
            view.setBackgroundColor(tick.getColor());
        }
    }

    private AdapterFactory<RelativeGridRow> factory;

    @Bind(R.id.textview_watchlist_name)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    private Watchlist watchlist;

    public static RelativeTableFragment newInstance() {
        Bundle args = new Bundle();

        RelativeTableFragment fragment = new RelativeTableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int stubLayout() {
        return R.layout.stub_relative_table;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onRelativeTableLoadComplete(RelativeTableLoadComplete event) {

        List<RelativeGridRow> gridList = event.getGridList();
        watchlist = event.getWatchlist();

        updateParams(event.getParams());
        textView.setText(watchlist.getName());
        table.setAdapter(this.getAnimationAdapter(factory.adapter(gridList)));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        factory = new AdapterFactory<>(this.getAppCompatActivity(), new RowBinder(), R.layout.list_item_relative_chart);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getAppCompatActivity());
        linearLayoutManager.setAutoMeasureEnabled(true);

        table.setLayoutManager(linearLayoutManager);
        table.setAdapter(factory.adapter(new ArrayList<>()));

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
                RelativeTableLoadStart event = new RelativeTableLoadStart(watchlist.getId(), (StudyGridParams) this.getParams(), true);
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
