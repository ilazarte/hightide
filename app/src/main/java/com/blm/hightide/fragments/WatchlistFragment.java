package com.blm.hightide.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.blm.corals.Tick;
import com.blm.hightide.R;
import com.blm.hightide.activity.FileActivity;
import com.blm.hightide.activity.RelativeChartActivity;
import com.blm.hightide.activity.RelativeTableActivity;
import com.blm.hightide.activity.SecurityActivity;
import com.blm.hightide.activity.TableActivity;
import com.blm.hightide.events.WatchlistFilesRequestComplete;
import com.blm.hightide.events.WatchlistFilesRequestStart;
import com.blm.hightide.fragments.internal.BaseFragment;
import com.blm.hightide.model.Security;
import com.blm.hightide.model.StudyParams;
import com.blm.hightide.model.Watchlist;
import com.blm.hightide.util.StandardPriceData;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * TODO select daily or intraday
 */

public class WatchlistFragment extends BaseFragment {

    private static final String TAG = WatchlistFragment.class.getSimpleName();

    private List<Watchlist> watchlists;

    private Watchlist selectedWatchlist;

    private ActionBar supportActionBar;

    private boolean resettingSpinner = true;

    public static WatchlistFragment newInstance() {
        Bundle args = new Bundle();
        WatchlistFragment fragment = new WatchlistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_textview_security_symbol)
        TextView symbol;

        @Bind(R.id.list_item_textview_last_update)
        TextView lastUpdate;

        @Bind(R.id.list_item_textview_last_date_received)
        TextView lastDateReceived;

        @Bind(R.id.list_item_textview_data_counts)
        TextView dataCounts;

        @Bind(R.id.list_item_checkbox_security_enabled)
        CheckBox enabled;

        @OnClick(R.id.list_item_imagebutton_load_table)
        @SuppressWarnings("unused")
        void clickTable() {
            Intent intent = TableActivity.newIntent(WatchlistFragment.this.getActivity(), security.getSymbol());
            startActivity(intent);
        }

        @OnClick(R.id.list_item_imagebutton_load_file)
        @SuppressWarnings("unused")
        void clickFile() {
            Intent intent = FileActivity.newIntent(WatchlistFragment.this.getActivity(), security.getSymbol());
            startActivity(intent);
        }

        @OnClick(R.id.list_item_imagebutton_chart_security)
        @SuppressWarnings("unused")
        void clickChart() {
            Intent intent = SecurityActivity.newIntent(WatchlistFragment.this.getActivity(), security.getSymbol());
            startActivity(intent);
        }

        @OnClick(R.id.list_item_textview_security_symbol)
        @SuppressWarnings("unused")
        void clickSymbol() {
            Intent intent = SecurityActivity.newIntent(WatchlistFragment.this.getActivity(), security.getSymbol());
            startActivity(intent);
        }

        private Security security;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Security security) {

            StandardPriceData priceData = security.getStandardPriceData();
            List<Tick> ticks = priceData.getTicks();

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.US);
            String datestr = sdf.format(priceData.getDate());
            String lastdatestr = sdf.format(ticks.get(ticks.size() - 1).getTimestamp());
            String msg = String.format("%s, %s",
                    ticks.size(),
                    priceData.getErrors().size());

            symbol.setText(security.getSymbol());
            lastUpdate.setText(datestr);
            lastDateReceived.setText(lastdatestr);
            dataCounts.setText(msg);
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
            View view = inflator.inflate(R.layout.list_item_watchlist, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Security security = securities.get(position);
            holder.bind(security);
            //WatchlistFragment.this.animateAppear(holder.itemView);
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

    private StudyParams params;

    @OnItemSelected(R.id.spinner_watchlist)
    @SuppressWarnings("unused")
    void selectWatchlist(int position) {
        if (resettingSpinner) {
            resettingSpinner = false;
            return;
        }
        Watchlist watchlist = watchlists.get(position);
        this.selectedWatchlist = watchlist;
        EventBus.getDefault().post(new WatchlistFilesRequestStart(watchlist.getId(), this.params, true));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);
        ButterKnife.bind(this, view);

        AppCompatActivity activity = this.getAppCompatActivity();

        Drawable dividerDrawable = ContextCompat.getDrawable(activity, android.R.drawable.divider_horizontal_bright);
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));

        this.supportActionBar = this.getSupportActionBar(toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new Adapter(new ArrayList<>()));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onRequestCompleted(WatchlistFilesRequestComplete event) {

        Watchlist watchlist = event.getWatchlist();
        List<Watchlist> watchlists = event.getWatchlists();
        List<Security> securities = watchlist.getSecurities();

        this.params = event.getParams();
        this.selectedWatchlist = watchlist;
        this.watchlists = watchlists;

        recyclerView.setAdapter(getAnimationAdapter(new Adapter(securities)));
        this.resetSpinner();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_watchlist, menu);
    }

    private void resetSpinner() {

        resettingSpinner = true;
        List<String> names = new ArrayList<>();

        for (Watchlist watchlist : watchlists) {
            names.add(watchlist.getName());
        }

        spinner.setAdapter(this.getSimpleArrayAdapter(supportActionBar.getThemedContext(), names));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_execute:
                intent = RelativeChartActivity.newIntent(this.getActivity(), this.selectedWatchlist.getId());
                startActivity(intent);
                break;
            case R.id.action_table:
                intent = RelativeTableActivity.newIntent(this.getActivity(), this.selectedWatchlist.getId());
                startActivity(intent);
                break;
            case R.id.action_refresh:
                EventBus.getDefault().post(new WatchlistFilesRequestStart(this.selectedWatchlist.getId(), this.params, false));
                break;
            case R.id.action_settings:
                Log.i(TAG, "onOptionsItemSelected: settings");
                break;
            default:
                break;
        }
        return false;
    }
}
