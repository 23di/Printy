package com.printy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
 


import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;
 
public class MainActivity extends Activity implements View.OnClickListener{
 
    //���������� ������� ����� ������
    ToggleButton redButton;
    ToggleButton greenButton;
 
    //�����, � ������� �������� �� ����� ���������� ������ �� Arduino
    BluetoothSocket clientSocket;
 
    //��� ������� ����������� ������������� ��� ������� ����������
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        //"��������" ��� ������ � ���� ���������� � �����������
        redButton = (ToggleButton) findViewById(R.id.toggleRedLed);
        greenButton = (ToggleButton) findViewById(R.id.toggleGreenLed);
 
        //�������� "��������� �������" � ������
        redButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
 
        //�������� bluetooth. ���� �� ��� �������, �� ������ �� ����������
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);
 
        //�� ����� ������������ ��� bluetooth-�������, ������� �������� �� ���������
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
 
        //�������� ��������� ��� ��������
        try{
            //���������� � ������ ������� - ��� Bluetooth Bee
            //����� ����������� ��������� �������: ���������� ����������
            //����� �� � ������� (���: 1234), � ����� ���������� � ����������
            //���������� ����� ������. ������ ����� �� ����� �����������.
            BluetoothDevice device = bluetooth.getRemoteDevice("00:12:05:10:90:72"); 
 
            //���������� ���������� � �����������
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] {int.class});
 
            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();
 
            //� ������ ��������� ����� ������, ������� � ��� ���������
        } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (SecurityException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalAccessException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }
 
        //������� ��������� �� �������� �����������
        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();
 
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
 
    //��� ��� ��� ������� � ����� ���������� 
 
    @Override
    public void onClick(View v) {
 
        //�������� ������� ������
        try {
            //�������� �������� ����� ��� �������� ������
            OutputStream outStream = clientSocket.getOutputStream();
 
            int value = 0;
 
            //� ����������� �� ����, ����� ������ ���� ������, 
            //�������� ������ ��� �������
            if (v == redButton) {
                
                
                
                File f = new File("/sdcard/1.txt");
                FileInputStream fin = null;
                FileChannel ch = null;
                try {
                    fin = new FileInputStream(f);
                    ch = fin.getChannel();
                    int size = (int) ch.size();
                    MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
                    byte[] bytes = new byte[size];
                    buf.get(bytes);
                    outStream.write(bytes);
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        if (fin != null) {
                            fin.close();
                        }
                        if (ch != null) {
                            ch.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                               
                
            } else if (v == greenButton) {
            	   
            	outStream.write(308);
              
            }
            
            
          
          
 
            //����� ������ � �������� �����
           
        } catch (IOException e) { 
            //���� ���� ������, ������� �� � ���
            Log.d("BLUETOOTH", e.getMessage());
        }
    }
}