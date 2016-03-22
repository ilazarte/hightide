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
import com.blm.hightide.events.FilesNotificationEvent;

import org.greenrobot.eventbus.SubscriberExceptionEvent;

import java.net.UnknownHostException;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    /**
     * Popup
     * @param event the file notification event.
     * @param initialMessage An initial message to display
     */
    protected void notifyFileProgress(FilesNotificationEvent event, int initialMessage) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(initialMessage));
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(event.getMax());
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        progressDialog.setProgress(event.getIncrement());
        progressDialog.setMessage(event.getMessage());
    }

    /**
     * Dismiss the progress notification and complete with snackbar.
     * @param completeMessage
     */
    protected void completeFileProgress(int completeMessage) {
        progressDialog.dismiss();
        snack(getString(completeMessage));
    }

    protected void handleThrowable(String tag, SubscriberExceptionEvent event) {
        Throwable throwable = event.throwable;
        if (UnknownHostException.class.isAssignableFrom(throwable.getClass())) {
            Log.e(tag, "error: " + throwable.getMessage());
        } else {
            throw new RuntimeException(throwable);
        }
    }

    protected View getRootView() {
        ViewGroup viewById = (ViewGroup) this.findViewById(android.R.id.content);
        final ViewGroup viewGroup = (ViewGroup) viewById.getChildAt(0);
        return viewGroup;
    }

    protected void snack(String message) {
        Snackbar.make(this.getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    public abstract Fragment createFragment();
}
