package com.dev.simonedipaolo.gui_life_save;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Simone Di Paolo on 05/07/2020.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int ENABLE_BT_REQUEST = 1;

    private BluetoothAdapter mBtAdapter;
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    private AlertDialog builder;

    private SwitchPreferenceCompat bluetoothOnOffSwitch;

    private LoadingDialog loadingDialog;

    public SettingsFragment() {
        // Costruttore vuoto NECESSARIO NON TOCCARE
    }

    @Override
    public void onStart() {
        super.onStart();

        // Componente per il LoadingDialog (AlertView custom)
        loadingDialog = new LoadingDialog(getActivity());

        // PAIRING
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver4, filter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // "unregistro" i broadcast receiver
    @Override
    public void onPause() {
        Log.d("onPause", " called");
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unregisterReceiver(mBroadcastReceiver1);
        getActivity().unregisterReceiver(mBroadcastReceiver2);
        getActivity().unregisterReceiver(mBroadcastReceiver3);
        getActivity().unregisterReceiver(mBroadcastReceiver4);
    }

    /**
     * Gestione pulsanti/switch/Preference
     *
     * @param savedInstanceState
     * @param rootKey
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null)
            Log.d("BLUETOOTH_NOT_SUPPORTED","Bluetooth non supportato dal device");

        // QUI GESTISCO LO SWITCH PER FARE IN MODO CHE LO SCHERMO RESTI ATTIVO
        SwitchPreferenceCompat keepScreenOnSwitch = findPreference("keepScreenOn");
        keepScreenOnSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean isOn = (boolean) newValue;

                if (isOn)
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                else
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                return true;
            }
        });

         // Switch ON/OFF Bluetooth
        bluetoothOnOffSwitch = findPreference("bluetooth_on_off_switch");
        if (mBtAdapter.isEnabled())
            bluetoothOnOffSwitch.setChecked(true);
        else
            bluetoothOnOffSwitch.setChecked(false);

        bluetoothOnOffSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean isOn = (boolean) newValue;

                if (mBtAdapter == null)
                    Log.d("BT_DEVICE_ERROR", "The device doesn't support Bluetooth.");
                else {
                    if (isOn) {
                        if (!mBtAdapter.isEnabled()) {
                            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ENABLE_BT_REQUEST);
                            getActivity().registerReceiver(mBroadcastReceiver1, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                            Log.d("ENABLING_BLUETOOTH", "Enabling...");
                        }
                    } else {
                        if (mBtAdapter.isEnabled()) {
                            mBtAdapter.disable();
                            getActivity().registerReceiver(mBroadcastReceiver1, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
                            Toast.makeText(getActivity().getApplicationContext(), "Shutting down Bluetooth", Toast.LENGTH_LONG)
                                    .show();
                            Log.d("DISABLING_BLUETOOTH", "Disabling...");
                        }
                    }
                }

                return true;
            }
        });

        /*
         * CERCO NUOVI DISPOSITIVI
         */
        Preference discoverNewDevicesPref = findPreference("bluetooth_search_new_devices");
        discoverNewDevicesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mBTDevices.clear();

                if (mBtAdapter.isDiscovering()) {
                    Log.d("SEARCH_LOG","DISCOVERING. CANCEL DISCOVERING");
                    mBtAdapter.cancelDiscovery();

                    checkPermission();

                    mBtAdapter.startDiscovery();

                    IntentFilter discoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getActivity().registerReceiver(mBroadcastReceiver3, discoverFilter);
                } else if (!mBtAdapter.isDiscovering()) {
                    Log.d("SEARCH_LOG","NOT DISCOVERING. START DISCOVERING");

                    checkPermission();

                    mBtAdapter.startDiscovery();

                    IntentFilter discoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getActivity().registerReceiver(mBroadcastReceiver3, discoverFilter);
                }
                startLoadingDialogAndCheckForNewDevices();
                return true;
            }
        });

        /*
         * Qui mostro i dispositivi già collegati precedentemente per riprovare una connessione veloce
         */
        Preference showPairedDevicesPref = findPreference("bluetooth_paired_devices_list");
        showPairedDevicesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if(!mBtAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, 1001);
                }

                showPairedDevices();
                return true;
            }
        });


        /*
         * Pulsante checkbox discoverability
         */
        final CheckBoxPreference discoverabilityCheckbox = findPreference("discoverability_checkbox");
        discoverabilityCheckbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.d("discoverabilityCheckbox", "Making the device discoverable for 60 seconds...");

                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                        .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60));

                getActivity().registerReceiver(mBroadcastReceiver2, new IntentFilter(mBtAdapter.ACTION_SCAN_MODE_CHANGED));
                discoverabilityCheckbox.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        discoverabilityCheckbox.setChecked(false);
                        discoverabilityCheckbox.setEnabled(true);
                    }
                }, 63000); // 63 secondi dal click, 3 secondi in più così stiamo sicuro nei tempi
                return true;
            }
        });

        // DARK MODE -> da completare

        SwitchPreferenceCompat darkModeSwitch = findPreference("darkModeSwitch");

        darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean isOn = (boolean) newValue;
/*
                if (isOn)

                else
*/
                return true;
            }
        });
    }

    // ------- BROADCAST RECEIVER -------

    // Crea un BroadcastReceiver for ACTION_STATE_CHANGED (accensione/spegnimento bluetooth)
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBtAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBtAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("mBroadcastReceiver1", "STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("mBroadcastReceiver1", "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("mBroadcastReceiver1", "STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("mBroadcastReceiver1", "STATE TURNING ON");
                        break;
                }
            }
        }
    };

    // Crea un BroadcastReceiver per ACTION_SCAN_MODE_CHANGED (discoverability)
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBtAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d("mBroadcastReceiver2", "Discoverability enabled.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d("mBroadcastReceiver2", "Discoverability disablee. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d("mBroadcastReceiver2", "Discoverability disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d("mBroadcastReceiver2", "Connecting...");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d("mBroadcastReceiver2", "Connected.");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("BROADCAST_3", "BROADCAST_RECEIVER_3");
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d("DISPOSITIVO_TROVATO", "Ho trovato il dispositivo: " + device.getName());
            }
        }
    };

    /*
     * BroadcastReceiver per il pairing con i vari dispositivi
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 3 casi

                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d("BroadcastReceiver4", "BOND_BONDED.");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d("BroadcastReceiver4", "BOND_BONDING.");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d("BroadcastReceiver4", "BOND_NONE.");
                }
            }
        }
    };

        // ------ FINE BROADCAST RECEIVER ------
    /**
     * Questo metodo, mostra un elenco di dispositivi con cui il device è stato già connesso
     * precedentemente.
     *
     * Mostra a video un AlertDialog cliccabile con la lista dei dispositivi.
     */
    public void showPairedDevices() {
            final Set<BluetoothDevice> pairedDevicesSet = mBtAdapter.getBondedDevices();
            final ArrayList<BluetoothDevice> pairedDevicesArrayList = new ArrayList<>();

            if (pairedDevicesSet.size() > 0) {
                int i=0;

                String[] devicesArrayName = new String[pairedDevicesSet.size()];
                for (BluetoothDevice temp : pairedDevicesSet) {
                    devicesArrayName[i] = temp.getName();
                    pairedDevicesArrayList.add(temp);
                    i++;
                }
                new MaterialAlertDialogBuilder(getActivity())
                        .setTitle("Found " + pairedDevicesSet.size() + " already paired devices:")
                        .setItems(devicesArrayName, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // GESTISCO LA PRESSIONE SU UN DISPOSITIVO GIA' CONNESSO
                                // PRECEDENTEMENTE

                                // 1) cancello "discovery"
                                mBtAdapter.cancelDiscovery();

                                // LOG del dispositivo a cui mi sto connettendo...
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    Log.d("PAIRING","Trying to pair with..." +
                                            pairedDevicesArrayList.get(which).getName());
                                    pairedDevicesArrayList.get(which).createBond();
                                }
                            }
                        })
                        .show();
            } else
                Toast.makeText(getActivity().getApplicationContext(),
                        "NO PAIRED DEVICES!",
                        Toast.LENGTH_LONG)
                .show();
        }

    /**
     * Check dei permessi in base all'SDK
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
        private void checkPermission() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck =  getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if (permissionCheck != 0) {
                    getActivity().requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                }
            }
        }

    /**
     * Questo metodo avvia un AlertDialog animato per la ricerca. Lancia un nuovo Thread
     * che dopo 5 secondi controlla se la ricerca di nuovi dispositivi bluetooth ha trovato qualcosa.
     *
     * Se sì, mostra i dispositivi trovati in un nuovo AlertView altrimenti, sempre con un AlertView
     * avvisa l'utente che non ne ha trovati.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void startLoadingDialogAndCheckForNewDevices() {
            loadingDialog.startNonCancelableLoadingAnimation();
            int j=0;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mBTDevices.size() > 0) {
                        loadingDialog.dismissDialog();
                        showNewDevicesAlertView();
                    }
                    else {
                        loadingDialog.dismissDialog();
                        showNoDevicesFoundAlertView();
                    }
                }
            }, 5000);

        }

    /**
     * Questo metodo mostra i dispositivi trovati tramite una nuova ricerca Bluetooth.
     * Se non ne trova, non viene chiamato.
     */
    private void showNewDevicesAlertView() {
        int j=0;

        final String[] arrayNewDevices = new String[mBTDevices.size()];
        for (BluetoothDevice tempDev : mBTDevices) {
            arrayNewDevices[j] = tempDev.getName();
            j++;
        }

        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Found " + arrayNewDevices.length + " new devices:")
                .setItems(arrayNewDevices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), "Clicked on " + arrayNewDevices[which], Toast.LENGTH_SHORT)
                                .show();
                        if (mBtAdapter.isDiscovering())
                            mBtAdapter.cancelDiscovery();

                        // creo la connessione
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            Log.d("createBond", "Tryin to bond with: " + mBTDevices.get(which).getName());
                            mBTDevices.get(which).createBond();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mBtAdapter.isDiscovering()) {
                            mBtAdapter.cancelDiscovery();
                            Log.d("IS_ADAPTER_DISCOVERING", mBtAdapter.isDiscovering() + "");
                        }
                    }
                })
                .show();
    }   // method end

    /**
     * Questo metodo mostra un AlertView in cui avvisa che non ha trovato
     * nuovi dispositivi Bluetooth con la ricerca effettuata
     */
    private void showNoDevicesFoundAlertView() {
        new MaterialAlertDialogBuilder(getActivity())
                .setTitle("No devices found.")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), "CANCEL PRESSED", Toast.LENGTH_SHORT).show();
                        Log.d("NEGATIVE_BUTTON_LOG", "Is discovering? " + mBtAdapter.isDiscovering());
                        if (mBtAdapter.isDiscovering()) {
                            mBtAdapter.cancelDiscovery();
                            Log.d("NEGATIVE_BUTTON_LOG", "Discovering cancelled.");
                        }
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENABLE_BT_REQUEST) {
            if(resultCode == RESULT_CANCELED) {
                bluetoothOnOffSwitch.setChecked(false);
                Log.d("ENABLE_BT_REQUEST", "REFUSED");
            }
        }
    }
}   //class end