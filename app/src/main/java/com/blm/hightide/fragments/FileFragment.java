package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.FileDataAvailable;
import com.blm.hightide.events.FileLoadStart;
import com.blm.hightide.model.FileData;
import com.blm.hightide.model.FileLine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = FileFragment.class.getSimpleName();

    private static final String SECURITY_SYMBOL = "SECURITY_SYMBOL";

    @Bind(R.id.textview_filename)
    TextView textView;

    @Bind(R.id.recyclerview_file)
    RecyclerView recyclerView;

    public static FileFragment newInstance(String symbol) {
        Bundle args = new Bundle();
        args.putString(SECURITY_SYMBOL, symbol);

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
            lineNum.setText(Integer.valueOf(fileLine.getNum()).toString());
            lineString.setText(fileLine.getLine());
            /*http://stackoverflow.com/questions/22196453/linearlayout-in-horizontalscrollview-is-not-expanding*/
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
            View view = inflator.inflate(R.layout.list_item_file_line, parent, false);
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

        EventBus.getDefault().register(this);
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        ButterKnife.bind(this, view);

        FragmentActivity activity = this.getActivity();

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new Adapter(new ArrayList<>()));

        String symbol = getArguments().getString(SECURITY_SYMBOL);
        EventBus.getDefault().post(new FileLoadStart(symbol));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onFileDataAvailable(FileDataAvailable event) {

        FileData fileData = event.getFileData();

        textView.setText(fileData.getName());
        List<FileLine> lines = fileData.getLines();
        Log.i(TAG, "onFileDataAvailable: line count: " + lines.size());
        recyclerView.setAdapter(new Adapter(lines));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
