package com.blm.hightide.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class BaseFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = BaseFragment.class.getSimpleName();

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity) this.getActivity();
    }

    @Override
    public void onResume() {
        /*Log.i(TAG, "onResume: " + this.getClass().getSimpleName());*/
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        /*Log.i(TAG, "onPause: " + this.getClass().getSimpleName());*/
        EventBus.getDefault().unregister(this);
        super.onPause();
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
