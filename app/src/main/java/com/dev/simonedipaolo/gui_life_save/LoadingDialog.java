package com.dev.simonedipaolo.gui_life_save;

import android.app.Activity;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Created by Simone Di Paolo on 10/07/2020.
 */

public class LoadingDialog {

    private Activity activity;
    private AlertDialog builder;

    private TextView mTimerTextView;

    public LoadingDialog (Activity activity) {
        this.activity = activity;
    }

    /**
     * Questo metodo crea un AlertDialog con un'animazione di "caricamento".
     * Il dialog è cancellabile, basta toccar su un qualsiasi punto dello schermo.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void startCancelableLoadingAnimation() {
        LayoutInflater inflater = activity.getLayoutInflater();

        View v = inflater.inflate(R.layout.custom_dialog_loading, null);
        mTimerTextView = v.findViewById(R.id.timer_textView);

        builder = new MaterialAlertDialogBuilder(activity)
                .setView(v)
                .setCancelable(false)
                .show();

        new CountDownTimer(6000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 > 1)
                    mTimerTextView.setText("Wait for " + millisUntilFinished/1000+  " seconds");
                else if (millisUntilFinished/1000 == 1)
                    mTimerTextView.setText("Wait for " + millisUntilFinished/1000 + " second");
            }

            @Override
            public void onFinish() {
                // nothing
            }
        }.start();
    }

    /**
     * Questo metodo crea un AlertDialog con un'animazione di "caricamento"
     * Il dialog non è cancellabile toccando sullo schermo ma solo invocando il metodo
     * dismissDialog().
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void startNonCancelableLoadingAnimation() {
        LayoutInflater inflater = activity.getLayoutInflater();

        View v = inflater.inflate(R.layout.custom_dialog_loading, null);
        mTimerTextView = v.findViewById(R.id.timer_textView);

        builder = new MaterialAlertDialogBuilder(activity)
                .setView(v)
                .setCancelable(false)
                .show();

        new CountDownTimer(6000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished/1000 > 1)
                    mTimerTextView.setText("Wait for " + millisUntilFinished/1000 + " seconds");
                else if (millisUntilFinished/1000 == 1)
                    mTimerTextView.setText("Wait for " + millisUntilFinished/1000 + " second");
            }

            @Override
            public void onFinish() {
                // nothing
            }
        }.start();
    }


    /**
     * Questo metodo interrompe l'alertView SE non è cancellabile. Altrimenti basta cliccare
     * da qualsiasi altra parte.
     */
    void dismissDialog() {
        builder.dismiss();
    }

}
