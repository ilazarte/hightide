package com.blm.hightide.fragments;

import android.app.usage.UsageEvents;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.activity.RelativePerformanceActivity;
import com.blm.hightide.events.RequestFilesCompleteEvent;
import com.blm.hightide.events.RequestFilesInitEvent;
import com.blm.hightide.events.RequestFilesStartEvent;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class WatchlistFragment extends Fragment {

    private static final String TAG = WatchlistFragment.class.getSimpleName();

    private static final String WATCHLIST_ID = "WATCHLIST_ID";

    private StockService service = new StockService();

    private List<Watchlist> watchlists;

    private Watchlist selectedWatchlist;

    private Adapter adapter;

    public static WatchlistFragment newInstance() {
        Bundle args = new Bundle();
        WatchlistFragment fragment = new WatchlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_textview_security_symbol)
        TextView symbol;

        @Bind(R.id.list_item_checkbox_security_enabled)
        CheckBox enabled;

        private Security security;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Security security) {
            symbol.setText(security.getSymbol());
            enabled.setChecked(security.isEnabled());
            this.security = security;
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {
        private List<Security> securities;

        public Adapter(List<Security> securities) {
            this.securities = securities;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.list_item_security, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Security security = securities.get(position);
            holder.bind(security);
        }

        @Override
        public int getItemCount() {
            return securities.size();
        }
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.spinner_watchlist)
    Spinner spinner;

    @Bind(R.id.recyclerview_security)
    RecyclerView recyclerView;

    @OnItemSelected(R.id.spinner_watchlist)
    @SuppressWarnings("unused")
    void selectWatchlist(int position) {
        Watchlist watchlist = watchlists.get(position);
        this.selectedWatchlist = watchlist;
        EventBus.getDefault().post(new RequestFilesInitEvent(this.selectedWatchlist));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView: creating view in ACTIVITY");

        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_stock_compare, container, false);
        ButterKnife.bind(this, view);

        AppCompatActivity activity = (AppCompatActivity) this.getActivity();
        service.init(activity);

        activity.setSupportActionBar(toolbar);

        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(null);
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }

        watchlists = service.findAllWatchlists();
        this.setSelectedWatchlist(savedInstanceState);
        service.findSecurities(this.selectedWatchlist);

        adapter = new Adapter(selectedWatchlist.getSecurities());
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);

        this.setSpinner(supportActionBar);

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestCompleted(RequestFilesCompleteEvent event) {
        adapter = new Adapter(event.getWatchlist().getSecurities());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_stock_compare, menu);
    }

    private void setSelectedWatchlist(Bundle savedInstanceState) {

        int id = savedInstanceState == null ? -1 : savedInstanceState.getInt(WATCHLIST_ID, -1);

        if (id != -1) {
            for (Watchlist watchlist : watchlists) {
                if (watchlist.getId().equals(id)) {
                    this.selectedWatchlist = watchlist;
                    break;
                }
            }
        } else {
            this.selectedWatchlist = watchlists.get(0);
        }
    }

    private void setSpinner(ActionBar supportActionBar) {

        List<String> names = new ArrayList<>();
        for (Watchlist watchlist : watchlists) {
            names.add(watchlist.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(supportActionBar.getThemedContext(),
                android.R.layout.simple_spinner_item,
                names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int idx = 0;
        for (Watchlist watchlist : watchlists) {
            if (watchlist.getId().equals(this.selectedWatchlist.getId())) {
                break;
            }
            idx++;
        }
        spinner.setSelection(idx);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedWatchlist != null) {
            outState.putInt(WATCHLIST_ID, selectedWatchlist.getId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                Intent intent = RelativePerformanceActivity.newIntent(this.getActivity(), this.selectedWatchlist.getId());
                startActivity(intent);
                break;
            case R.id.action_refresh:
                EventBus.getDefault().post(new RequestFilesInitEvent(this.selectedWatchlist));
                break;
            case R.id.action_settings:
                Log.i(TAG, "onOptionsItemSelected: settings");
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        service.release();
    }
}
