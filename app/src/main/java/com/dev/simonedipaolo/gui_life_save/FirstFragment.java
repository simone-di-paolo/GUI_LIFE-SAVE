package com.dev.simonedipaolo.gui_life_save;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Simone Di Paolo on 05/07/2020.
 */

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FirstFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageView mAutoImageView, mFrontWheelImageView, mBackWheelImageView,
            mFrecciaSolareImageView, mBatteryImageView, mBrakingArrowImageView;

    private CardView mSolarCardView, mContaKmCadeView, mBatteryChargeCardView, mBraking_cardView,
            mWhOggiCardView, mFrenataRigenerativaCardView, consumoEnergia_cardView, nm_cardView;

    private TextView mSolarWTextView, mXxDownTextView, mFrenataRigenerativaTextView, mNmTextView,
            mBrakingTextView, mWOggiTextView, mConsumoEnergiaTextView, mSpeedTextView;

    // prova bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    private int defaultButtonHeight, defaultButtonWidth, buttonClickedWidth, buttonClickedHeight;

    //private SpeedometerGauge mSpeedometer;


    public FirstFragment() {
        // Costruttore vuoto NECESSARIO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "IL DISPOSITIVO NON SUPPORTA IL BLUETOOTH LOW ENERGY!\n" +
                            "UTILIZZARE UN ALTRO DISPOSITIVO.", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // inizializzo i componenti

        // imageView auto
        mAutoImageView = v.findViewById(R.id.auto_imageView);
        mFrontWheelImageView =v.findViewById(R.id.ruota_anteriore);
        mBackWheelImageView =v.findViewById(R.id.ruota_posteriore);
        mBatteryImageView = v.findViewById(R.id.battery_lvl4_imageView);
        mFrecciaSolareImageView = v.findViewById(R.id.solarArrow_imageView);
        mBrakingArrowImageView = v.findViewById(R.id.braking_imageView);

        // card view
        mBatteryChargeCardView = v.findViewById(R.id.xx_down_cardView);
        mBraking_cardView = v.findViewById(R.id.braking_cardView);

        //mSpeedometer = v.findViewById(R.id.speedometer_view);

        mWhOggiCardView = v.findViewById(R.id.wh_oggi_cardView);
        mFrenataRigenerativaCardView = v.findViewById(R.id.frenataRigenerativa_cardView);
        mSolarCardView = v.findViewById(R.id.solar_W_cardView);
        mContaKmCadeView = v.findViewById(R.id.conta_km_cardView);
        consumoEnergia_cardView = v.findViewById(R.id.consumoEnergia_cardView);
        nm_cardView = v.findViewById(R.id.nm_cardView);

        // inizializzo i textView

        mSolarWTextView = v.findViewById(R.id.solar_w_textView);
        mXxDownTextView = v.findViewById(R.id.xx_down_textView);
        mFrenataRigenerativaTextView = v.findViewById(R.id.frenataRigenerativa_textView);
        mNmTextView = v.findViewById(R.id.nm_textView);
        mBrakingTextView = v.findViewById(R.id.braking_textView);
        mWOggiTextView = v.findViewById(R.id.wh_oggi_textView);
        mConsumoEnergiaTextView = v.findViewById(R.id.consumoEnergia_textView);
        mSpeedTextView = v.findViewById(R.id.speed_textView);

        // inizializzo i buttons

        mBottomNavigationView = v.findViewById(R.id.bottom_nave_menu);
        mBottomNavigationView.setSelectedItemId(R.id.bottom_page_one_button);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeInScreenOne();
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBottomNavigationView.setActivated(true);
            }
        }, 1000);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        autoEnterScreenAnimation(this.getView());

//        defaultButtonHeight = mScreenOneButton.getHeight();
//        defaultButtonWidth = mScreenOneButton.getWidth();
    }

    public void autoEnterScreenAnimation(View v){

        TranslateAnimation autoAnimation = new TranslateAnimation(600.0f, 0.0f, 0.0f, 0.0f);
        TranslateAnimation frontWheelAnimation = new TranslateAnimation(600.0f, 0.0f, 0.0f, 0.0f);
        TranslateAnimation backWheelAnimation = new TranslateAnimation(600.0f, 0.0f, 0.0f, 0.0f);

        autoAnimation.setDuration(650);
        frontWheelAnimation.setDuration(650);
        backWheelAnimation.setDuration(650);

        mAutoImageView.startAnimation(autoAnimation);
        mFrontWheelImageView.startAnimation(frontWheelAnimation);
        mBackWheelImageView.startAnimation(backWheelAnimation);
    }

    /**
     * Questo metodo viene richiamato nell'onCreate, avvia la creazione del primo fragment
     */
    public void fadeInScreenOne() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fade_in);

        /*
        fadeIn(mSolarCardView, mContaKmCadeView, mBatteryChargeCardView, mBraking_cardView,
                mFrecciaSolareImageView, mBatteryImageView, mBrakingArrowImageView, mSpeedometer);
        */
        fadeIn(mSolarCardView, mContaKmCadeView, mBatteryChargeCardView, mBraking_cardView,
                mFrecciaSolareImageView, mBatteryImageView, mBrakingArrowImageView);

        // card energia solare
        mSolarCardView.setVisibility(View.VISIBLE);

        // card contakm
        mContaKmCadeView.setVisibility(View.VISIBLE);

        // card carica batteria
        mBatteryChargeCardView.setVisibility(View.VISIBLE);

        // card braking
        mBraking_cardView.setVisibility(View.VISIBLE);

        // imageViewfreccia solare
        mFrecciaSolareImageView.setVisibility(View.VISIBLE);

        // imageView battery
        mBatteryImageView.setVisibility(View.VISIBLE);

        // imageView braking arrow
        mBrakingArrowImageView.setVisibility(View.VISIBLE);

        // speedometer
        //mSpeedometer.setVisibility(View.VISIBLE);
    }

    /*
     * ELENCO CARD VIEW DA RENDERE VISIBILI / INVISIBILI
     *
     * mBatteryChargeCardView
     * mBraking_cardView
     * mWhOggiCardView
     * mFrenataRigenerativaCardView
     * mSolarCardView
     */

    public void changeToScreenOne() {
        makeVisible(mSolarCardView, mBraking_cardView, mBatteryChargeCardView);
        makeInvisible(mWhOggiCardView, mFrenataRigenerativaCardView,
                        consumoEnergia_cardView, nm_cardView);

//        activeButton(mScreenOneButton);
//        deactivateButtons(mScreenTwoButton, mScreenThreeButton, mScreenFourButton);
    }

    public void changeToScreenTwo() {
        makeVisible(mWhOggiCardView, mBatteryChargeCardView);
        makeInvisible(mBraking_cardView, mSolarCardView, mFrenataRigenerativaCardView,
                        consumoEnergia_cardView, nm_cardView);

//        activeButton(mScreenTwoButton);
//        deactivateButtons(mScreenOneButton, mScreenThreeButton, mScreenFourButton);
    }

    private void changeToScreenThree() {
        makeVisible(mSolarCardView, mFrenataRigenerativaCardView, mBatteryChargeCardView);
        makeInvisible(mWhOggiCardView, mBraking_cardView, consumoEnergia_cardView, nm_cardView);

//        activeButton(mScreenThreeButton);
//        deactivateButtons(mScreenOneButton, mScreenTwoButton, mScreenFourButton);
    }

    private void changeToScreenFour() {

        makeVisible(consumoEnergia_cardView, mSolarCardView, nm_cardView);
        makeInvisible(mBatteryChargeCardView, mFrenataRigenerativaCardView,
                    mBraking_cardView, mWhOggiCardView);

//        activeButton(mScreenFourButton);
//        deactivateButtons(mScreenOneButton, mScreenTwoButton, mScreenThreeButton);
    }

    public void fadeOut(View... elements) {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fade_out);

        for (View temp : elements) {
            if (temp.getAlpha() > 0.1f)
                temp.startAnimation(fadeOutAnimation);
        }
    }

    public void fadeIn(View... elements) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.fade_in);

        for (View temp : elements) {
            if (temp.getAlpha() == 0)
                temp.startAnimation(fadeInAnimation);
        }
    }

    public void makeVisible(View... elements) {
        for (View temp : elements) {
            temp.setVisibility(View.VISIBLE);
        }
    }

    public void makeInvisible(View... elements) {
        for (View temp : elements) {
            temp.setVisibility(View.INVISIBLE);
        }
    }

    public void activeButton(Button button) {

        button.setWidth(buttonClickedWidth);
        button.setHeight(buttonClickedHeight);
        button.setBackgroundColor(Color.GREEN);

        Log.d("TEXT", defaultButtonHeight + "");
    }

    public void deactivateButtons(Button... buttons) {
        for (Button temp : buttons) {
            temp.setHeight(defaultButtonHeight);
            temp.setWidth(defaultButtonWidth);
            temp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()) {
            case R.id.bottom_page_one_button:
                changeToScreenOne();
                break;
            case R.id.bottom_page_two_button:
                changeToScreenTwo();
                break;
            case R.id.bottom_page_three_button:
                changeToScreenThree();
                break;
            case R.id.bottom_page_four_button:
                changeToScreenFour();
                break;
        }
        return true;
    }

    // --- GESTISCO LA LETTURA DEI DATI ---

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.d("CONNECTION_STATE_CHANGE", "STATUS: " + status + " -> ");
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                /*
                 * Una volta connesso dobbiamo trovare tutti i servizi sul device prima di poter
                 * leggere e scrivere
                 */
                gatt.discoverServices();
            } else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {

                // mHandler

            } else if (status != BluetoothGatt.GATT_SUCCESS) {

                // mHandler

                gatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

}