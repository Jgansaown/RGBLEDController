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
    static final int REQUEST_PICK_COLOR = 1;
    static final int REQUEST_ENABLE_BT = 2;
    //bluetooth device config
    private final String deviceMAC = "98:D3:31:60:22:72";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //ui
    TextView tvBTStatus;
    //bluetooth
    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;


    boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lock app to portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //initialize ui components
        tvBTStatus = (TextView) findViewById(R.id.tvBTStatus);
        //initialize bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //enable bluetooth
        if (mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            // TODO add message dialog
        }else if (!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
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
    }


    public void pickColor(View view){
        Intent intent = new Intent(this, ColorSelector.class);
        startActivityForResult(intent, REQUEST_PICK_COLOR);
    }




    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        TextView test = (TextView) findViewById(R.id.textView8);
        // Check which request we're responding to
        if (requestCode == REQUEST_PICK_COLOR) {// Make sure the request was successful
            if (resultCode == RESULT_OK) {
                test.setBackgroundColor(data.getIntExtra("color", 0));
            }
        }else if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode == RESULT_OK){
                Log.d(TAG, "request bluetooth: ok");
            }else if (resultCode == RESULT_CANCELED){
                Log.d(TAG, "request bluetooth: canceled");
                isConnected = false;
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

    //button control for connecting/disconnecting to device
    public void connectDisconnect(View view){
        if (isConnected){
            disconnet();
            isConnected = false;
        }else{
            connect();
            isConnected = true;
        }
    }
    void connect(){
        //if bluetooth is not enabled, turn it on
        if (!mBluetoothAdapter.isEnabled()){
            //prompt user to enable bluetooth
            //then if it's enabled, connect to device (mBroadcastReceiver1)
            Log.d(TAG, "connect: enabling bluetooth");
            enableBluetooth();
        }else{
            //bt is already enabled
            //connect to device
            Log.d(TAG, "connect: connecting to device");
            connectToDevice();
        }
    }
    void connectToDevice(){
        Log.d(TAG, "connectToDevice: started");
        mBTDevice = mBluetoothAdapter.getRemoteDevice(deviceMAC);
        mBTDevice.createBond();
        Log.d(TAG, "onItemClick: deviceName = " + mBTDevice.getName());
        Log.d(TAG, "onItemClick: deviceAddress = " + mBTDevice.getAddress());

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }
    void disconnet(){
        if (mBluetoothConnection != null){
            Log.d(TAG, "disconnect: closing bluetooth connection");
            mBluetoothConnection.close();
            Log.d(TAG, "disconnect: bluetooth connection closed");
        }
    }

    //enables bluetooth
    void enableBluetooth() {
        if (mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            // TODO add message dialog
        }else {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    void writeToBTDevice(String message){
        try{
            byte[] bytes = message.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytes);
        }catch (NullPointerException e){
            Log.e(TAG, "btnSend: " + e.getMessage());
        }
    }

    //BroadcastReceiver
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF ");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        //bt is enabled
                        //so connect to device
                        connectToDevice();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
}

