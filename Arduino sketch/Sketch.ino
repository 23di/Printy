#include "Thermal.h"
#include "SD.h"
#include "SPI.h"
#include <avr/pgmspace.h>
#include "SoftwareSerial.h"

int led = 8;
int led2 = 9;
Thermal printer(5, 6); //5 зеленый провод, 6 желтый

File myFile;

void blinkLED(int fr) //Мограем светодиодом
{
  digitalWrite(led2, HIGH);
  delay(fr);
  digitalWrite(led2, LOW);
  delay(fr);
}

void setup() {
  Serial.begin(9600);
  printer.begin();

  if (!SD.begin(4)) //Инициализируем SD
  {
    Serial.println("Initialization failed!");
    return;
  }
  pinMode(led, OUTPUT);
  pinMode(led2, OUTPUT);
}

void loop() {
  
  unsigned long lastCharArrived = 0;
  long charCount = 0;
  
  int c = Serial.read();

  if (c == '4' || c == '308') {   //Если 4 то прием
    digitalWrite(led, HIGH);
    Serial.println("Receiving...");
    myFile = SD.open("42.bin", FILE_WRITE);

    while (millis() - lastCharArrived < 8000 || charCount == 0)
    {
      while (Serial.available() > 0)
      {
        digitalWrite(led, HIGH);
        myFile.write(Serial.read());
        lastCharArrived = millis();
        charCount++;
      }
    }
    myFile.close();
    Serial.println("Received");
    digitalWrite(led, LOW);
  }
  else  if (c == '5' || c == '309') { //Если 5 то печать
    myFile = SD.open("42.bin");
    if (myFile.size() > 0)
    {
      blinkLED(50);
      Serial.println("Printing...");
      printer.wake();       // MUST call wake() before printing again, even if reset
      printer.printBitmap(384, 385, dynamic_cast<Stream*>(&myFile));
      SD.remove("42.bin");
      printer.sleep();      // Tell printer to sleep
      printer.wake();       // MUST call wake() before printing again, even if reset
      printer.setDefault(); // Restore printer to defaults
      blinkLED(50);
    }
    else
    {
      Serial.println("Nothing to print");
    }
  }
  blinkLED(500);
}

