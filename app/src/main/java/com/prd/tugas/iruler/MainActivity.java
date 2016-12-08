package com.prd.tugas.iruler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button MeasureButton = (Button)findViewById(R.id.button);
        MeasureButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
        {
            try {
                measure(); //send command and receive the measurement
            }
            catch (IOException e){}

        }
        });
        Button ConnectButton = (Button)findViewById(R.id.button1);
        ConnectButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try {
                    findBT(); //find the bluetooth module
                    openBT(); //connect to the module
                }
                catch (IOException e){}

            }
        });

    }
    void findBT()
    {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
        }

        if(!myBluetooth.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("HC-06")) //Connect to HC-06 Bluetooth Module
                {
                    mmDevice = device;
                    break;
                }
            }
        }
    }
    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        btSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        btSocket.connect();
        mmOutputStream = btSocket.getOutputStream();
        mmInputStream = btSocket.getInputStream();
        TextView status = (TextView)findViewById(R.id.textView);
        status.setText("Connected");

    }
    public void measure() throws IOException{
            if (btSocket != null) {
                try {
                    byte[] Buffer = new byte[256];
                    mmOutputStream.write("1".toString().getBytes());
                    int bytes = mmInputStream.read(Buffer);
                    String readMessage = new String(Buffer, 0, bytes);
                    TextView lengthText = (TextView)findViewById(R.id.length);
                    lengthText.setText(readMessage + "cm");
                } catch (IOException e) {
                }
            }
        }
    }


