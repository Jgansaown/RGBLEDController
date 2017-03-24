package com.example.jason_desktop.rgbledcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    static final int REQUEST_PICK_COLOR = 1;
    static final int REQUEST_ENABLE_BT = 2;
    //bluetooth device config
    private final String deviceMAC = "98:D3:31:60:22:72";
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //ui
    Button btnConnect;
    TextView tvBTStatus;
    TextView tvDeviceStatus;


    TextView testingread;

    //bluetooth
    BluetoothConnectionService mBluetoothConnection;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBTDevice;

    private ProgressDialog mDisconnectProgressDialog;
    boolean isConnected = false;
    boolean isBTOn = false;
    boolean toConnect = false;

    private String messageToSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lock app to portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //initialize ui components
        btnConnect = (Button) findViewById(R.id.btnConnect);
        tvBTStatus = (TextView) findViewById(R.id.tvBTStatus);
        tvDeviceStatus = (TextView) findViewById(R.id.tvDeviceStatus);
        //initialize bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //initialize broadcast receivers
        //bluetooth state change
        IntentFilter BTStateChangeIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver_btStateStatus, BTStateChangeIntent);
        //bluetooth connection status
        IntentFilter BTConnectionStateChangeIntent = new IntentFilter();
        BTConnectionStateChangeIntent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        BTConnectionStateChangeIntent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        BTConnectionStateChangeIntent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(broadcastReceiver_btConnectionStatus, BTConnectionStateChangeIntent);
        //read bt device message
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));


        //enable bluetooth
        if (mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            // TODO add message dialog
        }else if (!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }else if (mBluetoothAdapter.isEnabled()){
            //bluetooth is already enabled
            isBTOn = true;
            setBTStatus("Enabled", R.color.colorOn);
        }


        testingread = (TextView) findViewById(R.id.textView8);


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
            unregisterReceiver(broadcastReceiver_btStateStatus);
            Log.d(TAG, "onDestroy: broadcastReceiver_btStateStatus unregistered");
        }catch (RuntimeException e){
            Log.e(TAG, "onDestroy: broadcastReceiver_btStateStatus not registered");
        }
        try {
            unregisterReceiver(broadcastReceiver_btConnectionStatus);
            Log.d(TAG, "onDestroy: broadcastReceiver_btConnectionStatus unregistered");
        }catch (RuntimeException e){
            Log.e(TAG, "onDestroy: broadcastReceiver_btConnectionStatus not registered");
        }
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
                isBTOn = false;
            }
        }
    }

    //color picker button
    public void pickColor(View view){
        Intent intent = new Intent(this, ColorSelector.class);
        startActivityForResult(intent, REQUEST_PICK_COLOR);
    }
    //button control for connecting/disconnecting to device
    public void connectDisconnect(View view){
        if (isConnected){
            toConnect = false;
            disconnetFromDevice();
        }else{
            toConnect = true;
            if (!isBTOn) {//if bluetooth is not enabled, turn it on
                //prompt user to enable bluetooth
                //then if it's enabled, connect to device (broadcastReceiver_btStateStatus)
                enableBluetooth();
            }else{
                connectToDevice();
            }
        }
    }

    public void testsend(View view) {
        //send a random byte to tell bt device to get ready
        writeToBTDevice((byte)1);
        //set string to send once bt device is ready
        messageToSend = "Hi my name is jason";
    }


    void sendMessagetoBTDevice(String message) {
        //send a random byte to tell bt device to get ready
        writeToBTDevice((byte)1);
        //set string to send once bt device is ready
        messageToSend = message;
    }

    /**bluetooth controls**/
    //connect to bt device
    void connectToDevice(){
        Log.d(TAG, "connectToDevice: connecting to device");
        Log.d(TAG, "connectToDevice: started");
        mBTDevice = mBluetoothAdapter.getRemoteDevice(deviceMAC);
        mBTDevice.createBond();
        Log.d(TAG, "connectToDevice: deviceName = " + mBTDevice.getName());
        Log.d(TAG, "connectToDevice: deviceAddress = " + mBTDevice.getAddress());

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);

        Log.d(TAG, "connectToDevice: Initializing RFCOM Bluetooth Connection.");
        mBluetoothConnection.startClient(mBTDevice,MY_UUID_INSECURE);
    }
    //disconnect from bt device
    void disconnetFromDevice(){
        if (mBluetoothConnection != null){
            Log.d(TAG, "disconnect: closing bluetooth connection");
            mBluetoothConnection.close();
            Log.d(TAG, "disconnect: bluetooth connection closed");
            mDisconnectProgressDialog = ProgressDialog.show(this,"Disconnecting Bluetooth Device","Please Wait...",true);

        }
    }
    //enables bluetooth
    void enableBluetooth() {
        if (mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            // TODO add message dialog
        }else {
            Log.d(TAG, "connect: enabling bluetooth");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }
    //string to send to bt device
    void setDeviceStatus(String status, int color){
        tvDeviceStatus.setText(status);
        tvDeviceStatus.setTextColor(ContextCompat.getColor(this, color));
    }
    void setBTStatus(String status, int color){
        tvBTStatus.setText(status);
        tvBTStatus.setTextColor(ContextCompat.getColor(this, color));
    }
    //write message to bt device (string)
    void writeToBTDevice(String message){
        try{
            byte[] byteString = {(byte) message.length()};
            mBluetoothConnection.write(byteString);
            byte[] bytes = message.getBytes(Charset.defaultCharset());
            mBluetoothConnection.write(bytes);
        }catch (NullPointerException e){
            Log.e(TAG, "btnSend: " + e.getMessage());
        }
    }
    //write message to bt device (byte)
    void writeToBTDevice(byte message){
        try{
            byte[] bytes = {message};
            mBluetoothConnection.write(bytes);
        }catch (NullPointerException e){
            Log.e(TAG, "btnSend: " + e.getMessage());
        }
    }











    /**BroadcastReceiver**/
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadcastReceiver_btStateStatus = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "Bluetooth State: STATE OFF ");
                        isBTOn = false;
                        isConnected = false;
                        toConnect = false;
                        setBTStatus("Disabled", R.color.colorOff);
                        setDeviceStatus("Not Connected", R.color.colorOff);
                        try{
                            mDisconnectProgressDialog.dismiss();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "Bluetooth State: STATE TURNING OFF");
                        disconnetFromDevice();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "Bluetooth State: STATE ON");
                        setBTStatus("Enabled", R.color.colorOn);
                        isBTOn = true;
                        if (toConnect){
                            connectToDevice();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "Bluetooth State: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    //BroadcastReceiver for bt connection status
    private final BroadcastReceiver broadcastReceiver_btConnectionStatus = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                //device is found
                Log.d(TAG, "BT Connection Status: ACTION_FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                //device is connected
                Log.d(TAG, "BT Connection Status: ACTION_ACL_CONNECTED");
                isConnected = true;
                setDeviceStatus("Connected", R.color.colorOn);
                btnConnect.setText("disconnect");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)){
                //device is about to disconnect
                Log.d(TAG, "BT Connection Status: ACTION_ACL_DISCONNECT_REQUESTED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){
                //device is disconnected
                Log.d(TAG, "BT Connection Status: ACTION_ACL_DISCONNECTED");
                isConnected = false;
                setDeviceStatus("Not Connected", R.color.colorOff);
                try{
                    mDisconnectProgressDialog.dismiss();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                btnConnect.setText("connect");
            }
        }
    };
    //broadcastreceiver for reading message from the bt device
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            if (Objects.equals(text, "OK")){
                Log.d(TAG, "Device sent OK");
                writeToBTDevice(messageToSend);//send message
                messageToSend = "";
            }
            testingread.setText(text);
        }
    };
}

