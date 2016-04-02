package com.blm.hightide.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blm.hightide.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    private static final String TAG = AbstractBaseActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = this.createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Subscribe
    public void error(SubscriberExceptionEvent event) {
        handleThrowable(TAG, event);
    }

    /**
     * Used for initialization in preparation of notifications.
     * Must be called only once in a single threaded manner.
     * @param initialMessage An initial message to display to the user
     * @param max The total number of notification points the dialog will receive
     */
    public void initProgressDialog(int initialMessage, int max) {
        this.runOnUiThread(() -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(initialMessage));
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(max);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        });
    }

    public void notifyFileProgress(String message, int incr) {
        this.runOnUiThread(() -> {
            progressDialog.setMessage(message);
            progressDialog.setProgress(incr);
        });
    }

    /**
     * Dismiss the progress notification.
     * @param completeMessage The message to render via a snackbar.
     * @param event A new event to trigger.  May be null.
     */
    public void completeFileProgress(final int completeMessage, final Object event) {
        this.runOnUiThread(() -> {
            progressDialog.setOnDismissListener(dialog -> {
                snack(getString(completeMessage));
                if (event != null) {
                    EventBus.getDefault().post(event);
                }
            });
            progressDialog.dismiss();
        });
    }

    public void handleThrowable(String tag, SubscriberExceptionEvent event) {

        this.runOnUiThread(() -> {
            boolean showing = progressDialog != null && progressDialog.isShowing();
            if (showing) {
                progressDialog.dismiss();
            }
        });

        Throwable throwable = event.throwable;
        Log.e(tag, "handleThrowable: error in event handling:", throwable);

        if (UnknownHostException.class.isAssignableFrom(throwable.getClass())) {
            snack("error: " + throwable.getMessage());
        } else if (FileNotFoundException.class.isAssignableFrom(throwable.getClass())) {
            snack("error: " + throwable.getMessage());
        } else {
            throw new RuntimeException(throwable);
        }
    }

    public View getRootView() {
        ViewGroup viewById = (ViewGroup) this.findViewById(android.R.id.content);
        return viewById.getChildAt(0);
    }

    public void snack(String message) {
        Snackbar.make(this.getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    public abstract Fragment createFragment();
}
