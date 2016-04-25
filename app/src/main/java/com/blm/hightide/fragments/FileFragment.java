package com.blm.hightide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blm.hightide.R;
import com.blm.hightide.events.FileDataAvailable;
import com.blm.hightide.model.FileData;
import com.blm.hightide.model.FileLine;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FileFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = FileFragment.class.getSimpleName();

    @Bind(R.id.textview_filename)
    TextView textView;

    @Bind(R.id.recyclerview_file)
    RecyclerView recyclerView;

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

            /*http://stackoverflow.com/questions/22196453/linearlayout-in-horizontalscrollview-is-not-expanding*/
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

        View view = inflater.inflate(R.layout.fragment_file, container, false);
        ButterKnife.bind(this, view);

        FragmentActivity activity = this.getActivity();

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new Adapter(new ArrayList<>()));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onFileDataAvailable(FileDataAvailable event) {

        FileData fileData = event.getFileData();

        textView.setText(fileData.getName());
        List<FileLine> lines = fileData.getLines();
        recyclerView.setAdapter(new Adapter(lines));
    }
}
