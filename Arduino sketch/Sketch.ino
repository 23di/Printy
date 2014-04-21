#include "Thermal.h"
#include "SD.h"
#include "SPI.h"
#include <avr/pgmspace.h>
#include "SoftwareSerial.h"

int led = 9;
int led2 = 8;
Thermal printer(5, 6); //5 This is the green wire, 6 This is the yellow wire

unsigned long lastCharArrived = 0;
long charCount = 0;

File myFile;

void blinkLED()
{
    digitalWrite(led2, HIGH);
    delay(500);
    digitalWrite(led2, LOW);
    delay(500);
}

void setup() {
  Serial.begin(9600);
  printer.begin();

  if (!SD.begin(4))
  {
    Serial.println("Initialization failed!");
    return;
  }

  myFile = SD.open("42.bin");
  if (myFile.size() > 0)
  {
    Serial.println("Printing...");
    printer.wake();       // MUST call wake() before printing again, even if reset
    printer.printBitmap(384, 385, dynamic_cast<Stream*>(&myFile));
    SD.remove("42.bin");
    printer.sleep();      // Tell printer to sleep
    printer.wake();       // MUST call wake() before printing again, even if reset
    printer.setDefault(); // Restore printer to defaults
  }
  else
  {
    Serial.println("Nothing to print");
  }

  pinMode(led, OUTPUT);
  pinMode(led2, OUTPUT);

}

void loop() {

  int c = Serial.read();
  if (c == '4' || c == '308') {
    digitalWrite(led, HIGH);
    myFile = SD.open("42.bin", FILE_WRITE);

    while (millis() - lastCharArrived < 5000 ||  charCount == 0)
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

 blinkLED();

}
