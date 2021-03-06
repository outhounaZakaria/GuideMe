package com.example.guideme.guidage;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guideme.R;

public class ListBluetoothDevicesActivity extends AppCompatActivity {
    ListView deviceList;
    TextView searchStatusText;
    Button listDevicesButton;
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> bluetoothDeviceAddresses = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    BluetoothAdapter bluetoothAdapter;

    int counterDevicesWithName = 0;

    /**
     * handles the bluetooth events for our device searches
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // block user interface while searching
                listDevicesButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String address = device.getAddress();

                // add new found device to the list, if already unknown
                if (!bluetoothDeviceAddresses.contains(address)) {

                    String name = device.getName();
                    String rssi_min = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));

                    if (name == null || name.equals("")) {
                        bluetoothDevices.add(counterDevicesWithName, rssi_min + "dBm\n" + address);
                    } else {
                        bluetoothDevices.add(0, rssi_min + "dBm   (" + name + ")" + "\n" + address);
                        counterDevicesWithName++;
                    }
                    bluetoothDeviceAddresses.add(address);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bluetoothDevices);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList.setAdapter(arrayAdapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // stop current running search
                bluetoothAdapter.cancelDiscovery();

                // provide the selected mac address from the list to the next called action
                Intent intent = new Intent(ListBluetoothDevicesActivity.this, SingleDeviceTrackingActivity.class);
                intent.putExtra("device", parent.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });

        // set filter for the bluetooth search events
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * function is triggered by the search button
     *
     * @param view the clicked button as view
     */
    public void searchedClicked(View view) {
        // reset previous searches
        listDevicesButton.setEnabled(false);
        bluetoothDevices.clear();
        bluetoothDeviceAddresses.clear();
        counterDevicesWithName = 0;

        // start new search
        bluetoothAdapter.startDiscovery();
    }
}
