#include "Adafruit_Thermal.h"
#include "SoftwareSerial.h"
#include "Stream.h"

/**
 * Declare which pins to communicate to the printer over
 */
int printer_RX_Pin = 5; // ������� ������
int printer_TX_Pin = 6; // ������ ������

/**
 * �������������� �������
 */
Adafruit_Thermal printer(printer_RX_Pin, printer_TX_Pin);

void setup(){
  printer.begin();
  Serial.begin(9600); //��������� �������� ��������
}

void loop(){
  if (Serial.available() > 0){
    printer.printBitmap(384,75,&Serial); 
    Serial.write(1); //�������� ��� ��� ������ �� ����
  }
}