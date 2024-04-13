package com.example.arnavigation.fullmapdisplay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arnavigation.R;
import com.example.arnavigation.ble.BLEService;

public class InternetConnect extends AppCompatActivity {
    private EditText name, password;
    private Button connect, cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_connection);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        connect = findViewById(R.id.connect_button);
        cancel = findViewById(R.id.cancel_button);

        connect.setOnClickListener(view -> {
            String nameStr = name.getText().toString().trim();
            String passwordStr = password.getText().toString();
            if (BLEService.writeCharacteristic("wfcr/" + nameStr + "/" + passwordStr)) {
                Toast.makeText(InternetConnect.this, "Connecting", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(InternetConnect.this, "Error", Toast.LENGTH_SHORT).show();
            }

        });
        cancel.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
