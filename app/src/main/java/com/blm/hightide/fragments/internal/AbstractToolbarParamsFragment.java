package com.blm.hightide.fragments.internal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.blm.hightide.R;
import com.blm.hightide.model.MovingAvgParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public abstract class AbstractToolbarParamsFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = AbstractToolbarParamsFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ActionBar supportActionBar;

    @Bind(R.id.spinner_number)
    Spinner spinnerNumber;

    @Bind(R.id.spinner_average_length)
    Spinner spinnerAverageLength;

    ViewStubCompat stub;

    private List<Integer> numbers = new ArrayList<>();

    private MovingAvgParams params;

    private boolean avgLengthReset = true;

    private boolean numberReset = true;

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

    public void updateParams(MovingAvgParams params) {

        this.params = params;
        int lengthValue = numbers.indexOf(params.getLength());
        int avgLengthValue = numbers.indexOf(params.getAvgLength());

        spinnerNumber.setSelection(lengthValue);
        spinnerAverageLength.setSelection(avgLengthValue);
    }

    public MovingAvgParams getParams() {
        return params;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_params, container, false);
        stub = (ViewStubCompat) view.findViewById(R.id.stub_params);
        this.stub.setLayoutResource(this.stubLayout());
        this.stub.inflate();

        ButterKnife.bind(this, view);

        supportActionBar = this.getSupportActionBar(toolbar);

        for (int i = 10; i < 101; i += 10) {
            numbers.add(i);
        }

        Context themedContext = supportActionBar.getThemedContext();

        spinnerNumber.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));
        spinnerAverageLength.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));

        return view;
    }

    /**
     * Return the viewstub layout.
     * @return the layout id
     */
    public abstract int stubLayout();
}
