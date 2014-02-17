#include "Thermal.h"
#include "SD.h"
#include "SPI.h"
#include <avr/pgmspace.h>
#include "SoftwareSerial.h"

int printer_RX_Pin = 5;  // This is the green wire
int printer_TX_Pin = 6;  // This is the yellow wire

File myFile;

Thermal printer(printer_RX_Pin, printer_TX_Pin);

void setup(){
  Serial.begin(9600);

  Serial.print("Initializing SD card...");
  pinMode(7, OUTPUT); digitalWrite(7, LOW);
  pinMode(10, OUTPUT);

  if (!SD.begin(4)) {
    Serial.println("initialization failed!");
    return;
  }
  Serial.println("initialization done.");
  
  printer.begin();
    if (!SD.exists("3.bin"))
  {
    printer.println("File not exists");
  }
  else
  {
  
  myFile = SD.open("3.bin");
  
  if (myFile)
  {
  
  printer.printBitmap(384, 310, dynamic_cast<Stream*>(&myFile));
  }
  else
  {
    printer.println("File not exists");
  }

}
  printer.sleep();      // Tell printer to sleep
  printer.wake();       // MUST call wake() before printing again, even if reset
  printer.setDefault(); // Restore printer to defaults
}

void loop() {
}
