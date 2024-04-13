package com.example.arnavigation.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arnavigation.R;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DeviceActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private static final String TAG = "BLEDevice";
    public static BluetoothDevice mDevice;
    public static BluetoothGatt mConnGatt;
    private static DeviceActivity instance;
    TextView UUID_view;
    UUID navigationService;
    private BluetoothAdapter mBTAdapter;
    private int mStatus;
    private Button mReadManufacturerNameButton;
    private Button mReadSerialNumberButton;
    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
                Toast.makeText(DeviceActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                Toast.makeText(DeviceActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                } else {
                    navigationService = service.getUuid();
                }/*
                if (!BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service.getUuid().toString())) {
                    mReadManufacturerNameButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING)));
                    mReadSerialNumberButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING)));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadManufacturerNameButton.setEnabled(true);
                            mReadSerialNumberButton.setEnabled(true);
                        }
                    });
                }
                if (!BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service.getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mWriteAlertLevelButton.setEnabled(true);
                        }

                        ;
                    });
                    mWriteAlertLevelButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }*/
            }
/*
            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }
            });*/
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!BleUuid.CHAR_MANUFACTURER_NAME_STRING.equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadManufacturerNameButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } else if (!BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadSerialNumberButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {

            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }

                ;
            });
        }

        ;
    };
    private Button mWriteAlertLevelButton;

    public static synchronized DeviceActivity getInstance() {
        if (instance == null) {
            instance = new DeviceActivity();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public static String getName() {
        return mDevice.getName();
    }

    public static BluetoothGatt getConnGatt() {
        return mConnGatt;
    }

    public boolean writeCharacteristic(String text) {
        //check mBluetoothGatt is available
        if (mConnGatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
        BluetoothGattService Service = mConnGatt.getService(navigationService);
        if (Service == null) {
            Log.e(TAG, "service not found!");
            return false;
        }
        BluetoothGattCharacteristic characteristic = Service.getCharacteristic(navigationService);
        if (characteristic == null) {
            Log.e(TAG, "char not found!");
            return false;
        }
        byte[] data = text.getBytes(StandardCharsets.UTF_8); // Chuyển đổi văn bản thành byte array
        characteristic.setValue(data);
        /*
        byte[] value = new byte[1];
        value[0] = (byte) (21 & 0xFF);
        characteristic.setValue(value);*/
        @SuppressLint("MissingPermission") boolean status = mConnGatt.writeCharacteristic(characteristic);
        return status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);

        // state
        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mReadManufacturerNameButton = (Button) findViewById(R.id.read_manufacturer_name_button);
        mReadManufacturerNameButton.setOnClickListener(this::onClick);
        mReadSerialNumberButton = (Button) findViewById(R.id.read_serial_number_button);
        mReadSerialNumberButton.setOnClickListener(this::onClick);
        mWriteAlertLevelButton = (Button) findViewById(R.id.write_alert_level_button);
        mWriteAlertLevelButton.setOnClickListener(this::onClick);
        UUID_view = findViewById(R.id.uuid);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        init();

        Toast.makeText(this, mConnGatt.getDevice().getName(), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();/*
        if (mConnGatt != null) {
            if ((mStatus != BluetoothProfile.STATE_DISCONNECTING)
                    && (mStatus != BluetoothProfile.STATE_DISCONNECTED)) {
                mConnGatt.disconnect();
            }
            mConnGatt.close();
            mConnGatt = null;
        }*/
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_manufacturer_name_button) {
            if (!writeCharacteristic("99")) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
            //writeCharacteristic("99");
            //writeCharacteristic("1");
            Toast.makeText(this, String.valueOf(navigationService), Toast.LENGTH_SHORT).show();
            if ((v.getTag() != null) && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                Toast.makeText(this, "manufacture", Toast.LENGTH_SHORT).show();
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v.getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
        } else if (v.getId() == R.id.read_serial_number_button) {
            if (!writeCharacteristic("1")) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
            if ((v.getTag() != null)
                    && (v.getTag() instanceof BluetoothGattCharacteristic)) {
                BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
                        .getTag();
                if (mConnGatt.readCharacteristic(ch)) {
                    setProgressBarIndeterminateVisibility(true);
                }
            }
            Toast.makeText(this, "serial", Toast.LENGTH_SHORT).show();

        } else if (v.getId() == R.id.write_alert_level_button) {
            if (!writeCharacteristic("2")) {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // BT check
        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // check BluetoothDevice
        if (mDevice == null) {
            mDevice = getBTDeviceExtra();
            if (mDevice == null) {
                finish();
                return;
            }
        }

        // button disable
        /*
        mReadManufacturerNameButton.setEnabled(false);
        mReadSerialNumberButton.setEnabled(false);
        mWriteAlertLevelButton.setEnabled(false);*/

        // connect to Gatt
        if ((mConnGatt == null) && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            // try to connect
            mConnGatt = mDevice.connectGatt(DeviceActivity.this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                // re-connect and re-discover Services
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                Log.e(TAG, "state error");
                finish();
                return;
            }
        }
        setProgressBarIndeterminateVisibility(true);
    }

    private BluetoothDevice getBTDeviceExtra() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }
        return extras.getParcelable(EXTRA_BLUETOOTH_DEVICE);
    }

}
