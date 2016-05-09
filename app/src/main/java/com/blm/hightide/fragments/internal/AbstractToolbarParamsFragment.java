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
import com.blm.hightide.model.AggType;
import com.blm.hightide.model.StudyParams;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Bind(R.id.spinner_agg_type)
    Spinner spinnerAggType;

    ViewStubCompat stub;

    private List<Integer> numbers = new ArrayList<>();

    private StudyParams params = new StudyParams();

    private boolean avgLengthReset = true;

    private boolean numberReset = true;

    private boolean aggTypeReset = true;

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

    @OnItemSelected(R.id.spinner_tick_type)
    @SuppressWarnings("unused")
    void selectTickType(int position) {
        if (aggTypeReset) {
            aggTypeReset = false;
            return;
        }
        AggType aggType = AggType.values()[position];
        params.setAggType(aggType);
    }

    public void updateParams(StudyParams params) {

        this.params = params;
        int lengthValue = numbers.indexOf(params.getLength());
        int avgLengthValue = numbers.indexOf(params.getAvgLength());

        spinnerNumber.setSelection(lengthValue);
        spinnerAverageLength.setSelection(avgLengthValue);
        spinnerAggType.setSelection(AggType.indexOf(params.getAggType()));
    }

    public StudyParams getParams() {
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

        for (int i = 20; i < 121; i += 20) {
            numbers.add(i);
        }

        Context themedContext = supportActionBar.getThemedContext();

        spinnerNumber.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));
        spinnerAverageLength.setAdapter(this.getSimpleArrayAdapter(themedContext, numbers));
        spinnerAggType.setAdapter(this.getSimpleArrayAdapter(themedContext, AggType.labels()));

        return view;
    }

    /**
     * Return the viewstub layout.
     * @return the layout id
     */
    public abstract int stubLayout();
}
