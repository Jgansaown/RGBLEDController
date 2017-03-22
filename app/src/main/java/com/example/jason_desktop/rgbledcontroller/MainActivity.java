package com.example.jason_desktop.rgbledcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    static final int PICK_COLOR_REQUEST = 1;
    static final int REQUEST_ENABLE_BT = 2;
    //bluetooth device config
    private final String deviceMAC = "98:D3:31:60:22:72";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //bluetooth
    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;

    //ui
    Button btnSend;
    EditText etSend;
    Button btnDisconnect;
    TextView connectionStatus;

    boolean btOn = false;
    boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lock app to portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        btnSend = (Button) findViewById(R.id.btnSend);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        etSend = (EditText) findViewById(R.id.editText);
        connectionStatus = (TextView) findViewById(R.id.textView2);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        btOn = mBluetoothAdapter.isEnabled();

        Log.d(TAG, "bt is :" + btOn);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try{
                    byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
                    mBluetoothConnection.write(bytes);
                }catch (NullPointerException e){
                    Log.e(TAG, "btnSend: " + e.getMessage());
                }
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "btnDisconnect: clicked");
                if (mBluetoothConnection != null){
                    Log.d(TAG, "btnDisconnect: closing bluetooth connection");
                    mBluetoothConnection.close();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        super.onDestroy();
        if (isFinishing()){
            Log.d(TAG, "onDestroy: isFinishing");
            if (mBluetoothConnection != null){

                mBluetoothConnection.close();
            }
        }
        try {
            unregisterReceiver(mBroadcastReceiver1);
        }catch (RuntimeException e){
            Log.d(TAG, "onDestroy: mBroadcastReceiver1 not registered");
        }
        try {
            unregisterReceiver(mBroadcastReceiver4);
        }catch (RuntimeException e){
            Log.d(TAG, "onDestroy: mBroadcastReceiver4 not registered");
        }
    }


    public void pickColor(View view){
        Intent intent = new Intent(this, ColorSelector.class);
        startActivityForResult(intent, PICK_COLOR_REQUEST);
    }




    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        TextView test = (TextView) findViewById(R.id.textView8);
        // Check which request we're responding to
        if (requestCode == PICK_COLOR_REQUEST) {// Make sure the request was successful
            if (resultCode == RESULT_OK) {
                test.setBackgroundColor(data.getIntExtra("color", 0));
            }
        }

    }


    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(device,uuid);
    }

    public void enableDisableBT(){
        if (mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

public void connectDisconnect(View view){
    if (isConnected){
        disconnet();
        isConnected = false;
    }else{
        connect();
        isConnected = true;
    }
}


    void connectToDevice(){
        Log.d(TAG, "connectToDevice: clicked");
        mBTDevice = mBluetoothAdapter.getRemoteDevice(deviceMAC);
        mBTDevice.createBond();
        Log.d(TAG, "onItemClick: deviceName = " + mBTDevice.getName());
        Log.d(TAG, "onItemClick: deviceAddress = " + mBTDevice.getAddress());

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    void connect(){
        if (!btOn){
            connectBluetooth();
            btOn = true;
            Log.d(TAG, "bt turned on");
        }
        Log.d(TAG, "exit connect");
    }

    void disconnet(){
        if (btOn){
            disconnectBluetooth();
            Log.d(TAG, "bt turned off");
        }
        Log.d(TAG, "exit disconnect");
    }

    void connectBluetooth() {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, BTIntent);
    }
    void disconnectBluetooth(){
        mBluetoothAdapter.disable();

        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, BTIntent);
    }





















    //BroadcastReceiver
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                TextView text = (TextView) findViewById(R.id.textView4);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        text.setText("Bluetooth is off");
                        btOn = false;
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        text.setText("Bluetooth is on");
                        btOn = true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED");

                    mBTDevice = mDevice;
                }
                //case2: creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING");
                }
                //case1: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE");
                }
            }
        }
    };
}

