package com.blm.hightide.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
}
