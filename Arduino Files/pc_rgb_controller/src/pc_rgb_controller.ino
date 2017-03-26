#include <Arduino.h>

#include <EEPROM.h>
#include <SoftwareSerial.h>
#include "FastLED.h"

//neopixel led settings
#define NUM_STRIPS          2
#define NUM_LEDS_PER_STRIP  20
#define NUM_LEDS            NUM_STRIPS * NUM_LEDS_PER_STRIP

#define BRIGHTNESS          100 //0% - 100%
#define FRAMES_PER_SECOND   120

// Define the array of leds
CRGB leds[NUM_STRIPS * NUM_LEDS_PER_STRIP];

CRGB *leds1 = &leds[0];
CRGB *leds2 = &leds[NUM_LEDS_PER_STRIP];

//bluetooth (HC-05) settings
//rx -> bt tx
//tx -> bt rx
SoftwareSerial btSerial(10,11); //(RX,TX)


//function prototypes

void extractInput(char* input);


//basic functions
void SolidColor_rgb();
void SolidPulse_rgb();
void SolidRainbow();
void SolidRainbowPulse();
void SolidColor_hsv(uint8_t h, uint8_t s=255, uint8_t v=255);
void SolidPulse_hsv(uint8_t h, uint8_t s, uint8_t v);

//single strip patterns
void demoReel100();
void rainbow_singleStrip();
void rainbowWithGlitter();
void confetti();
void sinelon();
void bpm();
void juggle();

//two strips patterns
void rainbow_twoStrip();
void rainbow_circle_twoStrip();
void sinelon_twoStrip();
void circle();
void circle_rainbow();
void alternate();
void alternate_opposite();

typedef void (*PatternList[])();
PatternList patterns = {
SolidColor_rgb, SolidPulse_rgb, SolidRainbow, SolidRainbowPulse,
rainbow_singleStrip, rainbowWithGlitter, confetti, sinelon, juggle, bpm,
rainbow_twoStrip, rainbow_circle_twoStrip, sinelon_twoStrip, circle, circle_rainbow, alternate, alternate_opposite,
demoReel100
};


//function settings
int function = 0;
int r = 0;
int g = 0;
int b = 0;
int t_hueCycle = 20;
int t_fadeTime = 20;
int t_patternCycle = 20;
int beat = 20;
int brightness = 100;

uint8_t gHue = 0; // rotating "base color" used by many of the patterns

void setup() {
  delay(3000); // 3 second delay for recovery
  //serial start up
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  Serial.println("Ready");

  btSerial.begin(9600);

  FastLED.addLeds<NEOPIXEL, 12>(leds, 0, NUM_LEDS_PER_STRIP);
  FastLED.addLeds<NEOPIXEL, 13>(leds, NUM_LEDS_PER_STRIP, NUM_LEDS_PER_STRIP);



  FastLED.clear(true);
  FastLED.show();

  pinMode(13, OUTPUT);

  int eeAddress = 0;
  EEPROM.get(eeAddress, function);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, r);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, g);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, b);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, t_hueCycle);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, t_fadeTime);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, t_patternCycle);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, beat);
  eeAddress += sizeof(int);
  EEPROM.get(eeAddress, brightness);

  FastLED.setBrightness(brightness*255/100);
}

void loop() {
  if (btSerial.available() > 0){//if input from device
    digitalWrite(13, HIGH);
    char cmd_buffer[64];
    uint8_t sizes;
    btSerial.read(); //throw away the random byte
    btSerial.write("2OK");
    delay(100);
    byte byteSize = btSerial.read();
    if (byteSize <= (byte)64){
      sizes = (int) byteSize;
      Serial.print(sizes);

      char input[sizes];
      btSerial.readBytes(input, sizes);
      Serial.print(", input string: ");
      Serial.print(input);
      Serial.println("");

      extractInput(input);
      FastLED.clear(true);
    }
    digitalWrite(13, LOW);
  }


  patterns[function]();


  // send the 'leds' array out to the actual LED strip
  FastLED.show();
  // insert a delay to keep the framerate modest
  FastLED.delay(1000/FRAMES_PER_SECOND);
}


String printbt(){
  String input = "";
  while(btSerial.available()){
    char recievedChar = (char)btSerial.read();
    if (recievedChar != '\n'){
      input += recievedChar;
    }
    delay(1);
  }
  return input;
}
