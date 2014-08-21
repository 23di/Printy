package com.printy2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener {

    //Экземпляры классов наших кнопок
    String bcs;
    String str;
    Button redButton;
    Button greenButton;
    Button blueButton;
    String NameOfImage;

    String[] data = {"Железный человек", "Девушка", "Донателло", "Судоку", "Коээфициент ликвидности"};

    //Сокет, с помощью которого мы будем отправлять данные на Arduino
    BluetoothSocket clientSocket;

    //Эта функция запускается автоматически при запуске приложения
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        //"Соединям" вид кнопки в окне приложения с реализацией
        redButton = (Button) findViewById(R.id.toggleRedLed);
        greenButton = (Button) findViewById(R.id.toggleGreenLed);
        blueButton = (Button) findViewById(R.id.toggleBlueLed);

        //Добавлем "слушатель нажатий" к кнопке
        redButton.setOnClickListener(this);
        greenButton.setOnClickListener(this);
        blueButton.setOnClickListener(this);

        // выделяем элемент
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // показываем позиция нажатого элемента
                if (position == 0) {
                    NameOfImage = "ironman";
                } else if (position == 1) {
                    NameOfImage = "girl";
                } else if (position == 2) {
                    NameOfImage = "tnmt";
                } else if (position == 3) {
                    NameOfImage = "sudoku";
                } else if (position == 4) {
                    NameOfImage = "koeff";
                }
                int duration = Toast.LENGTH_SHORT;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bcs = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), bcs, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }


        });

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();

        ArrayList<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
            s.add(bt.getAddress());

        Spinner spin = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, s);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(categoriesAdapter);

        //Пытаемся проделать эти действия
        try {

            String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
            startActivityForResult(new Intent(enableBT), 0);
            //Мы хотим использовать тот bluetooth-адаптер, который задается по умолчанию

            str = bcs;
            str="00:12:11:20:07:78";
            //BluetoothDevice device = bluetooth.getRemoteDevice("00:12:11:20:07:78");
            //Инициируем соединение с устройством
            Toast.makeText(this, "Подключаюсь", Toast.LENGTH_LONG).show();
            BluetoothDevice device = bluetooth.getRemoteDevice(str);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            clientSocket = (BluetoothSocket) m.invoke(device, 1);



            //В случае появления любых ошибок, выводим в лог сообщение
        } catch (SecurityException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d("BLUETOOTH", e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {

        //Пытаемся послать данные
        try {
            //Получаем выходной поток для передачи данных
            OutputStream outStream = clientSocket.getOutputStream();
            int value = 0;



            if (v == redButton) {
                //Тут мы октрываем файл с SD карты
                //
                try {
                    AssetManager mgr = getBaseContext().getAssets();
                    InputStream is = mgr.open(NameOfImage);

                    byte[] bytes = new byte[is.available()];
                    is.read(bytes);
                    outStream.write(bytes); //Пишем байты в буффер
                    is.close();
                    Toast.makeText(getApplicationContext(), "Переданно", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            } else if (v == greenButton) { //Начинаем прием вручную
                clientSocket.connect();
                outStream.write(308);

            } else if (v == blueButton) { //Начинаем прием вручную
                outStream.write(309);

            }
        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
        }
    }
}