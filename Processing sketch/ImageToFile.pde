// Convert image to a C header file suitable for the Adafruit_Thermal library.
// This is NOT an Arduino sketch.  Runs in Processing IDE (www.processing.org)

String filename="1.bmp"; /File to convert
import processing.serial.*;


void setup() {
  String      basename, filenameBin;
  PImage      img;
  PrintWriter output;
  int         i, x, y, b, rowBytes, totalBytes, lastBit, sum, n, r=1;
  byte[] data;
  int byteIndex = 0;

  // Select and load image
  println("Loading image...");
  img        = loadImage(filename);

  // Morph filename into output filename and base name for data
  x = filename.lastIndexOf('.');  
  if (x > 0) filename = filename.substring(0, x);  // Strip current extension
  x = filename.lastIndexOf('/');
  if (x > 0) basename = filename.substring(x + 1); // Strip path
  else      basename = filename;

  filenameBin = filename+".bin";
  println("Writing output to " + filenameBin);

  // Calculate output size
  rowBytes   = (img.width + 7) / 8;
  totalBytes = rowBytes * img.height;

  //Create array for bytes
  data = new byte[totalBytes];

  // Convert image to B&W, make pixels readable
  img.filter(THRESHOLD);
  img.loadPixels();

  // Generate body of array
  for (i=n=y=0; y<img.height; y++) { // Each row...
    //    output.print("\n  ");
    for (x=0; x<rowBytes; x++) { // Each 8-pixel block within row...
      lastBit = (x < rowBytes - 1) ? 1 : (1 << (rowBytes * 8 - img.width));
      sum     = 0; // Clear accumulated 8 bits
      for (b=128; b>=lastBit; b >>= 1) { // Each pixel within block...
        if ((img.pixels[i++] & 1) == 0) sum |= b; // If black pixel, set bit
      }      

      //Write to byte array
      data[byteIndex] = byte(sum);
        if (byteIndex > 0) {
          data[byteIndex] = byte(data[byteIndex-1] << 8 | data[byteIndex]);
//                println("byteIndex "+(byteIndex-1)+": "+data[byteIndex-1]);
        }
      byteIndex++;
    }
  }

  // save byte array as binary file
  saveBytes(filenameBin, data);

  println("Done!");
  exit();
}
