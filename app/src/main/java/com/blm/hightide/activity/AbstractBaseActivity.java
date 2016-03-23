package com.blm.hightide.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

public abstract class AbstractBaseActivity extends AppCompatActivity {

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

    /**
     * Displaying ongoing notification.
     * @param event the file notification event.
     */
    public void notifyFileProgress(FilesNotificationEvent event) {
        progressDialog.setProgress(event.getIncrement());
        progressDialog.setMessage(event.getMessage());
    }

    /**
     * Used for initialization in preparation of notifications.
     * Must be called only once in a single threaded manner.
     * @param initialMessage An initial message to display to the user
     * @param max The total number of notification points the dialog will receive
     */
    public void initProgressDialog(int initialMessage, int max) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(initialMessage));
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(max);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * @see #completeFileProgress(int, Object)
     * @param completeMessage The message to render via snackbar
     */
    public void completeFileProgress(int completeMessage) {
        completeFileProgress(completeMessage, null);
    }

    /**
     * Dismiss the progress notification.
     * @param completeMessage The message to render via a snackbar.
     * @param event A new event to trigger.  May be null.
     */
    public void completeFileProgress(final int completeMessage, final Object event) {
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                snack(getString(completeMessage));
                if (event != null) {
                    EventBus.getDefault().post(event);
                }
            }
        });
        progressDialog.dismiss();
    }

    public void handleThrowable(String tag, SubscriberExceptionEvent event) {

        boolean showing = progressDialog != null && progressDialog.isShowing();
        if (showing) {
            progressDialog.dismiss();
        }

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
