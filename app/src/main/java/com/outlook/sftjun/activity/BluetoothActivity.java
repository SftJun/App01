package com.outlook.sftjun.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vpro_004.app01.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;

public class BluetoothActivity extends AppCompatActivity {

    private Button blueBtn1;
    private Button blueBtn2;
    private Button blueBtn3;
    private TextView blueTxt1;
    // requestCode must > 0
    public static final int REQUEST_ENABLE_BT = 1;
    //
    public static final UUID MY_UUID = UUID.randomUUID();
    public static final String NAME = "SFTJUN";
    //
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean isEnable = false;
    private Boolean isStartDiscovery = false;
    //
    ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);
        //
        blueTxt1 = (TextView) findViewById(R.id.blue_txt1);
        blueBtn1 = (Button) findViewById(R.id.blue_btn1);
        blueBtn2 = (Button) findViewById(R.id.blue_btn2);
        blueBtn3 = (Button) findViewById(R.id.blue_btn3);
        blueBtn1.setOnClickListener(MyOnclikListener());
        blueBtn2.setOnClickListener(MyOnclikListener());
        blueBtn3.setOnClickListener(MyOnclikListener());
        //
    }

    private View.OnClickListener MyOnclikListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.blue_btn1:
                        startBluetooth();
                        break;
                    case R.id.blue_btn2:
                        if (isEnable) {
                            showPairedBlueDevices();
                        }
                        break;
                    case R.id.blue_btn3:
                        if (isEnable) {
                            scanBluetoothDevices();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 启动蓝牙装置
     */
    private void startBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) { // 判断设备是否支持蓝牙
            if (!mBluetoothAdapter.isEnabled()) {//判断蓝牙是否启动
                Intent enableBlueToothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBlueToothIntent, REQUEST_ENABLE_BT);
            } else {
                isEnable = true;
                Toast.makeText(getApplication(), "您蓝牙已经启动", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplication(), "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 显示蓝牙设备
     */
    private void showPairedBlueDevices() {
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) { // 已经有配对的设备
            for (BluetoothDevice device : pairedDevice) {
                System.out.println("Name:" + device.getName() + "--Mac:" + device.getAddress());
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                System.out.println(device.getName() + "--" + device.getAddress() + "\n");
            }
        }
    };
    // Register the BroadcastReceiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    private void scanBluetoothDevices() {
        // 注册广播接收
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            System.out.println("蓝牙扫描关闭");
        } else {
            mBluetoothAdapter.startDiscovery();
            System.out.println("蓝牙扫描打开");
        }
    }


    // 连接为服务器
    private class AcceptThread implements Runnable {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // 连接为客户端
    private class ConnectThread implements Runnable {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // 处理连接后的Bluetooth连接
    private class ConnectedThread implements Runnable {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    // TODO
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /**
     * @param socket
     */
    private void manageConnectedSocket(BluetoothSocket socket) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT: // 请求码对应的是蓝牙
                    isEnable = true;
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);//取消广播注册
    }
}
