package com.blm.hightide.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

import java.util.List;

public class BaseFragment extends Fragment {

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) this.getActivity();
    }

    public ActionBar getSupportActionBar(Toolbar toolbar) {
        AppCompatActivity activity = (AppCompatActivity) this.getActivity();

        activity.setSupportActionBar(toolbar);

        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(null);
            supportActionBar.setDisplayHomeAsUpEnabled(false);
        }
        return supportActionBar;
    }

    public <T> ArrayAdapter<T> getSimpleArrayAdapter(Context context, List<T> items) {
        int layout = android.R.layout.simple_spinner_item;
        int itemlayout = android.R.layout.simple_spinner_dropdown_item;
        ArrayAdapter<T> spinnerAdapter = new ArrayAdapter<>(context, layout, items);
        spinnerAdapter.setDropDownViewResource(itemlayout);
        return spinnerAdapter;
    }
}
