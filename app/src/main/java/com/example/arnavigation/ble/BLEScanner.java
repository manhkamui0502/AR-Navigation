package com.example.arnavigation.ble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.arnavigation.R;

import java.util.ArrayList;

public class BLEScanner extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private DeviceAdapter mDeviceAdapter;
    private boolean mIsScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        checkBluetoothPermissions();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
        finish();
    }

    @Override
    public void onLeScan(final BluetoothDevice newDevice, final int newRssi,
                         final byte[] newScanRecord) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceAdapter.update(newDevice, newRssi, newScanRecord);
            }
        });
    }

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
            bluetoothAdapter = manager.getAdapter();
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        startScan();
        // init listview
        ListView deviceListView = (ListView) findViewById(R.id.list);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device, new ArrayList<ScannedDevice>());
        deviceListView.setAdapter(mDeviceAdapter);

        deviceListView.setOnItemClickListener((adapterview, view, position, id) -> {
            ScannedDevice item = mDeviceAdapter.getItem(position);
            if (item != null) {
                /*
                Intent intent = new Intent(view.getContext(), DeviceActivity.class);
                BluetoothDevice selectedDevice = item.getDevice();

                intent.putExtra(DeviceActivity.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
                startActivity(intent);*/


                Intent intent = new Intent(view.getContext(), BLEService.class);
                BluetoothDevice selectedDevice = item.getDevice();
                intent.putExtra(BLEService.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
                startService(intent);
                // stop before change Activity
                stopScan();
            }
        });

        stopScan();
    }

    @SuppressLint("MissingPermission")
    void startScan() {
        if ((bluetoothAdapter != null) && (!mIsScanning)) {
            Toast.makeText(this, "startScan", Toast.LENGTH_SHORT).show();
            bluetoothAdapter.startLeScan(this::onLeScan);
            mIsScanning = true;
            invalidateOptionsMenu();
        }
    }

    @SuppressLint("MissingPermission")
    private void stopScan() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.stopLeScan(this);
        }
        mIsScanning = false;
        invalidateOptionsMenu();
    }

    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }
}
