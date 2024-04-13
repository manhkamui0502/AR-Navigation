package com.example.arnavigation.ble;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

/**
 * Scanned Bluetooth Device
 */
public class ScannedDevice {
    private static final String UNKNOWN = "Unknown";
    private BluetoothDevice mDevice;
    private int mRssi;
    private String mDisplayName;

    @SuppressLint("MissingPermission")
    public ScannedDevice(BluetoothDevice device, int rssi) {
        if (device == null) {
            throw new IllegalArgumentException("BluetoothDevice is null");
        }
        mDevice = device;
        mDisplayName = device.getName();
        if ((mDisplayName == null) || (mDisplayName.length() == 0)) {
            mDisplayName = UNKNOWN;
        }
        mRssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getRssi() {
        return mRssi;
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }
}
