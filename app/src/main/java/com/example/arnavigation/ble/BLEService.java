package com.example.arnavigation.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BLEService extends Service {
    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    static List<BluetoothGattCharacteristic> characteristics;
    private final IBinder binder = new LocalBinder();
    public static BluetoothDevice mDevice;
    public static BluetoothGatt mConnGatt;
    static UUID navigationService;
    private static int mStatus;
    public static boolean mapMode = false;

    private final BluetoothGattCallback mCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                mapMode = false;
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service != null) || (service.getUuid() != null)) {
                    navigationService = service.getUuid();
                    characteristics = service.getCharacteristics();
                }
            }
            if (characteristics.get(1) != null) {
                gatt.setCharacteristicNotification(characteristics.get(1), true);
                BluetoothGattDescriptor descriptor = characteristics.get(1).getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); // UUID của descriptor cho thông báo (Notification)
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            if (characteristics.get(1).getUuid().equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                String message = new String(data, StandardCharsets.UTF_8);
                if(message.contains("fullmapmode")){
                    mapMode = true;
                } else {
                    mapMode = false;
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getExtras() != null) {
                mDevice = intent.getExtras().getParcelable(EXTRA_BLUETOOTH_DEVICE);
            }
        }
        init();
        if (mConnGatt != null) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnGatt != null) {
            mConnGatt.disconnect();
        }
    }

    public static boolean writeCharacteristic(String text) {
        if (mConnGatt == null) {
            return false;
        }
        BluetoothGattService service = mConnGatt.getService(navigationService);
        if (service == null) {
            return false;
        }
        if (characteristics.get(0) == null) {
            return false;
        }
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        characteristics.get(0).setValue(data);
        @SuppressLint("MissingPermission") boolean status = mConnGatt.writeCharacteristic(characteristics.get(0));
        return status;
    }

    public static boolean getMapMode() {
        return mapMode;
    }

    @SuppressLint("MissingPermission")
    public static void disconnect() {
        mConnGatt.disconnect();
    }

    public static boolean connection_state() {
        if (mConnGatt != null) {
            return true;
        }
        return false;
    }

    public static BluetoothGatt getmConnGatt() {
        return mConnGatt;
    }

    @SuppressLint("MissingPermission")
    public static void sendToBLE(int mode, int maneuver, double cSpeed, double turnDis, double remainDis) {
        String info = mode + "$" + maneuver + "$" + cSpeed + "$" + turnDis + "$" + remainDis;
        writeCharacteristic(info);
    }

    @SuppressLint("MissingPermission")
    public static void sendToBLE_freeDrive(int mode, double cSpeed, int hour, int minute, int second) {
        String info = mode + "$" + cSpeed + "$" + hour + "$" + minute + "$" + second;
        writeCharacteristic(info);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @SuppressLint("MissingPermission")
    private void init() {
        if (mDevice == null) {
            return;
        }
        if ((mConnGatt == null) && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            mConnGatt = mDevice.connectGatt(BLEService.this, false, mCallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                mConnGatt.connect();
                mConnGatt.discoverServices();
            }
        }
    }

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

}
