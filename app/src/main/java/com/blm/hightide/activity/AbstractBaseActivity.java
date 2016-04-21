package com.blm.hightide.activity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.blm.hightide.R;
import com.blm.hightide.events.GlobalLayout;
import com.blm.hightide.service.StockService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.SubscriberExceptionEvent;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    private static final String TAG = AbstractBaseActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private StockService stockService = new StockService();

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockService.init(this);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_fragment_container);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = this.createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        /*layout is complete and the dimensions of myView and any child views are known.*/
        View container = findViewById(R.id.main_container);
        if (container == null) {
            return;
        }

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {

                    container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                EventBus.getDefault().post(GlobalLayout.INSTANCE);
            }
        });
    }

    public StockService getStockService() {
        return stockService;
    }

    @SuppressWarnings("unused")
    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void error(SubscriberExceptionEvent event) {
        handleThrowable(TAG, event);
    }

    /**
     * Used for initialization in preparation of notifications.
     * Must be called only once in a single threaded manner.
     *
     * @param initialMessage An initial message to display to the user
     * @param max            The total number of notification points the dialog will receive
     */
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public void notifyFileProgress(String message, int incr) {
        this.runOnUiThread(() -> {
            progressDialog.setMessage(message);
            progressDialog.setProgress(incr);
        });
    }

    /**
     * Dismiss the progress notification.
     *
     * @param completeMessage The message to render via a snackbar.
     * @param event           A new event to trigger.  May be null.
     */
    @SuppressWarnings("unused")
    public void completeFileProgress(final int completeMessage, final Object event) {
        this.runOnUiThread(() -> {
            progressDialog.setOnDismissListener(dialog -> {
                snackbar(getString(completeMessage));
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
            snackbar("error: " + throwable.getMessage());
        } else if (FileNotFoundException.class.isAssignableFrom(throwable.getClass())) {
            snackbar("error: " + throwable.getMessage());
        } else {
            throw new RuntimeException(throwable);
        }
    }

    public View getRootView() {
        ViewGroup viewById = (ViewGroup) this.findViewById(android.R.id.content);
        if (viewById == null) {
            throw new RuntimeException("No view attached!");
        }
        return viewById.getChildAt(0);
    }

    /**
     * Simple toast value
     *
     * @param id a resource id.
     */
    public void toast(int id) {
        this.runOnUiThread(() -> {
            String message = this.getString(id);
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
        });
    }

    /**
     * Snackbar
     *
     * @param id a resourceid
     */
    @SuppressWarnings("unused")
    public void snackbar(int id) {
        snackbar(this.getString(id));
    }

    public void snackbar(String message) {
        Snackbar.make(this.getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    public abstract Fragment createFragment();


    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
        stockService.release();
    }
}
