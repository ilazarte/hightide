package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.blm.hightide.events.FileDataAvailable;
import com.blm.hightide.events.FileLoadStart;
import com.blm.hightide.fragments.internal.AbstractAggTypeFragment;
import com.blm.hightide.model.FileData;
import com.blm.hightide.model.FileLine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileFragment extends AbstractAggTypeFragment {

    @SuppressWarnings("unused")
    private static final String TAG = FileFragment.class.getSimpleName();

    @Bind(R.id.textview_title)
    TextView textView;

    @Bind(R.id.recyclerview_file)
    RecyclerView recyclerView;

    private String symbol;

    public static FileFragment newInstance() {
        Bundle args = new Bundle();

        FileFragment fragment = new FileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.list_item_textview_line_num)
        TextView lineNum;

        @Bind(R.id.list_item_textview_line_string)
        TextView lineString;

        public Holder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(FileLine fileLine) {
            String text = Integer.valueOf(fileLine.getNum()).toString();
            lineNum.setText(text);
            lineString.setText(fileLine.getLine());
            lineString.setSingleLine();
        }
    }

    class Adapter extends RecyclerView.Adapter<Holder> {

        private List<FileLine> fileLines;

        public Adapter(List<FileLine> fileLines) {
            this.fileLines = fileLines;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View view = inflator.inflate(R.layout.list_item_file, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            FileLine fileLine = fileLines.get(position);
            holder.bind(fileLine);
        }

        @Override
        public int getItemCount() {
            return fileLines.size();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        AppCompatActivity activity = this.getAppCompatActivity();

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new Adapter(new ArrayList<>()));

        return view;
    }

    @Override
    public int stubLayout() {
        return R.layout.stub_file;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onFileDataAvailable(FileDataAvailable event) {

        FileData fileData = event.getFileData();
        this.symbol = event.getSymbol();

        updateAggType(event.getAggType());
        textView.setText(symbol);
        List<FileLine> lines = fileData.getLines();
        recyclerView.setAdapter(this.getAnimationAdapter(new Adapter(lines)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_file, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_execute:
                FileLoadStart event = new FileLoadStart(symbol, this.getAggType());
                EventBus.getDefault().post(event);
                break;
            default:
                break;
        }
        return false;
    }
}
