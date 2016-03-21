package com.blm.hightide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.blm.hightide.fragments.RelativePerformanceFragment;

public class RelativePerformanceActivity extends AbstractBaseActivity {

    private static final String TAG = RelativePerformanceActivity.class.getSimpleName();

    private static final String WATCHLIST_ID = "com.blm.hightide.activity.WATCHLIST_ID";

    public static Intent newIntent(Context context, int watchlistId) {
        Intent intent = new Intent(context, RelativePerformanceActivity.class);
        intent.putExtra(WATCHLIST_ID, watchlistId);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        int watchlistId = this.getIntent().getExtras().getInt(WATCHLIST_ID);
        return RelativePerformanceFragment.newInstance(watchlistId);
    }
}
