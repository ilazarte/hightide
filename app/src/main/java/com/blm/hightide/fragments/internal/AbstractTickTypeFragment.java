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
import com.blm.hightide.model.TickType;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public abstract class AbstractTickTypeFragment extends BaseFragment {

    @SuppressWarnings("unused")
    private static final String TAG = AbstractTickTypeFragment.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ActionBar actionBar;

    @Bind(R.id.spinner_tick_type)
    Spinner spinnerTickType;

    ViewStubCompat stub;

    private boolean tickTypeReset = true;

    private TickType tickType = TickType.DAILY;

    @OnItemSelected(R.id.spinner_tick_type)
    @SuppressWarnings("unused")
    void selectTickType(int position) {
        if (tickTypeReset) {
            tickTypeReset = false;
            return;
        }
        tickType = TickType.values()[position];
    }

    public void updateTickType(TickType tickType) {

        this.tickType = tickType;

        List<TickType> tickTypes = Arrays.asList(TickType.values());
        int tickTypeValue = tickTypes.indexOf(tickType);
        spinnerTickType.setSelection(tickTypeValue);
    }

    public TickType getTickType() {
        return tickType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ticktype, container, false);
        stub = (ViewStubCompat) view.findViewById(R.id.stub_ticktype);
        this.stub.setLayoutResource(this.stubLayout());
        this.stub.inflate();

        ButterKnife.bind(this, view);

        actionBar = this.getSupportActionBar(toolbar);

        Context themedContext = actionBar.getThemedContext();

        spinnerTickType.setAdapter(this.getSimpleArrayAdapter(themedContext, Arrays.asList(TickType.values())));

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
