import processing.serial.*;

Serial myPort;  // Create object from Serial class
int val;        // Data received from the serial port



float x = 50;
float y = 50;
float w = 150;
float h = 150;

void setup(){
  
   String portName = Serial.list()[0];
  myPort = new Serial(this, portName, 9600);
  
  
 size(250,250);
 background(255);
 stroke(0);
 noFill();
}

void draw(){
   byte b[] = loadBytes("Image.bin"); 
 background(255);
 rect(x,y,w,h);
 fill(255);
 if(mousePressed){
  if(mouseX>x && mouseX <x+w && mouseY>y && mouseY <y+h){
   
   fill(255,20,20);
     myPort.write('4');              // send an H to indicate mouse is over square
  
    myPort.write(b);              // send an L otherwise
  }
 } 
}
