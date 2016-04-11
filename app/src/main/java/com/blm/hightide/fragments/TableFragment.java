package com.blm.hightide.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.SecurityLoadComplete;
import com.blm.hightide.events.SecurityLoadStart;
import com.blm.hightide.model.Security;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TableFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = TableFragment.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "SECURITY_SYMBOL";

    class StringHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.grid_item_textview)
        TextView textview;

        public StringHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(String item, int position) {
            textview.setText(item);
        }
    }

    class StringAdapter extends RecyclerView.Adapter<StringHolder> {

        private List<String> items;

        public StringAdapter(List<String> items) {
            this.items = items;
        }

        @Override
        public StringHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.grid_item_textview, parent, false);
            return new StringHolder(view);
        }

        @Override
        public void onBindViewHolder(StringHolder holder, int position) {
            String item = items.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    @Bind(R.id.textview_security_symbol)
    TextView textView;

    @Bind(R.id.recyclerview_table)
    RecyclerView table;

    public static TableFragment newInstance(String symbol) {
        Bundle args = new Bundle();
        args.putString(SECURITY_SYMBOL, symbol);

        TableFragment fragment = new TableFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 7);
        //gridLayoutManager.setAutoMeasureEnabled(true);

        table.setLayoutManager(gridLayoutManager);
        table.setAdapter(new StringAdapter(new ArrayList<>()));
        table.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 10;
                outRect.right = 10;
                /*
                int position = parent.getChildAdapterPosition(view);
                int column = position % 7;
                switch (column) {
                    case 0:
                        outRect.right = 10;
                        break;
                    default:
                        outRect.left = 10;
                        outRect.right = 10;
                }
                */
            }
        });

        String symbol = getArguments().getString(SECURITY_SYMBOL);
        EventBus.getDefault().post(new SecurityLoadStart(symbol));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onSecurityLoad(SecurityLoadComplete event) {
        Security security = event.getSecurity();
        List<String> columns = event.getColumns();

        textView.setText(security.getSymbol());
        table.setAdapter(new StringAdapter(columns));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
