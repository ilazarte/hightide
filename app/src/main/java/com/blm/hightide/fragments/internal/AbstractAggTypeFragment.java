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
import com.blm.hightide.model.TickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public abstract class AbstractAggTypeFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = AbstractAggTypeFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ActionBar actionBar;

    @Bind(R.id.spinner_agg_type)
    Spinner spinnerAggType;

    ViewStubCompat stub;

    private boolean aggTypeReset = true;

    private AggType aggType = AggType.DAY;

    @OnItemSelected(R.id.spinner_agg_type)
    @SuppressWarnings("unused")
    void selectTickType(int position) {
        if (aggTypeReset) {
            aggTypeReset = false;
            return;
        }
        aggType = AggType.values()[position];
    }

    public void updateAggType(AggType aggType) {

        this.aggType = aggType;
        spinnerAggType.setSelection(AggType.indexOf(aggType));
    }

    public AggType getAggType() {
        return aggType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_aggtype, container, false);
        stub = (ViewStubCompat) view.findViewById(R.id.stub_agg_type);
        this.stub.setLayoutResource(this.stubLayout());
        this.stub.inflate();

        ButterKnife.bind(this, view);

        actionBar = this.getSupportActionBar(toolbar);

        Context themedContext = actionBar.getThemedContext();

        spinnerAggType.setAdapter(this.getSimpleArrayAdapter(themedContext, AggType.labels()));

        return view;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    /**
     * Return the viewstub layout.
     * @return the layout id
     */
    public abstract int stubLayout();
}
