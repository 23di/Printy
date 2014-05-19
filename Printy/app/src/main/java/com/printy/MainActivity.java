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

    //Экземпляры классов наших кнопок
    ToggleButton redButton;
    ToggleButton greenButton;
    ToggleButton blueButton;

    //Сокет, с помощью которого мы будем отправлять данные на Arduino
    BluetoothSocket clientSocket;

    //Эта функция запускается автоматически при запуске приложения
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //"Соединям" вид кнопки в окне приложения с реализацией
        redButton = (ToggleButton) findViewById(R.id.toggleRedLed);
        greenButton = (ToggleButton) findViewById(R.id.toggleGreenLed);
        blueButton = (ToggleButton) findViewById(R.id.toggleBlueLed);

        //Добавлем "слушатель нажатий" к кнопке
        redButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);

        //Включаем bluetooth. Если он уже включен, то ничего не произойдет
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        //Мы хотим использовать тот bluetooth-адаптер, который задается по умолчанию
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        //Пытаемся проделать эти действия
        try{
            //Устройство с данным адресом - удаленное устройство Bluetooth, жестко заданный mac адресс
            BluetoothDevice device = bluetooth.getRemoteDevice("E1:80:20:56:1D:3E\n");

            //Инициируем соединение с устройством
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] {int.class});

            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();

            //В случае появления любых ошибок, выводим в лог сообщение
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

        //Выводим сообщение об успешном подключении
        Toast.makeText(getApplicationContext(), "Подключено", Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Инициализируем меню; на будущее
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //Как раз эта функция и будет вызываться 

    @Override
    public void onClick(View v) {

        //Пытаемся послать данные
        try {
            //Получаем выходной поток для передачи данных
            OutputStream outStream = clientSocket.getOutputStream();
            int value = 0;
            //В зависимости от того, какая кнопка была нажата,
            //изменяем данные для посылки
			if (v == redButton) {File f = new File("/sdcard/1.txt"); //Тут мы октрываем файл с SD карты
                FileInputStream fin = null;
                FileChannel ch = null;
                try {
                    fin = new FileInputStream(f); //Преобразовываем в массив байтов
                    ch = fin.getChannel();
                    int size = (int) ch.size();
                    MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
                    byte[] bytes = new byte[size];
                    buf.get(bytes);
                    outStream.write(bytes); //Пишем байты в буффер

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


            } else if (v == greenButton) { //Начинаем прием вручную

                outStream.write(308);

            }

         else if (v == blueButton) { //Отправляем на печать

            outStream.write(309);

        }
            //Пишем данные в выходной поток

        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
        }
    }
}