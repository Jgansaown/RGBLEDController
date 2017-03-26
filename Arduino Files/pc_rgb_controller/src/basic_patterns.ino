#include <Arduino.h>

/*
 * Basic use of FastLed library
 * setting RGB: leds[i] = CRGB(r,g,b);
 * setting HSV: leds[i] = CHSV(h,s,v);
 */
void SolidColor_rgb(){
  for (int i = 0; i < NUM_LEDS; i++){
    leds[i] = CRGB(r,g,b);
  }
}
int pulse = 0;
void SolidPulse_rgb(){
  SolidColor_rgb();
  EVERY_N_MILLISECONDS(t_fadeTime){
    FastLED.setBrightness(cubicwave8(pulse));
    pulse++;
  }
}
void SolidRainbow(){
  // FastLED's built-in rainbow generator
  fill_rainbow( leds, NUM_LEDS, gHue, 0);
    EVERY_N_MILLISECONDS(t_hueCycle){gHue++; }
}
void SolidRainbowPulse(){
  SolidColor_hsv(sin8(gHue), 255,255);
  EVERY_N_MILLISECONDS(t_fadeTime){
    FastLED.setBrightness(cubicwave8(pulse));
    pulse++;
  }
  EVERY_N_MILLISECONDS(t_hueCycle){gHue++;}
}

//hsv
void SolidColor_hsv(uint8_t h, uint8_t s=255, uint8_t v=255){
  for (int i = 0; i < NUM_LEDS; i++){
    leds[i] = CHSV(h,s,v);
  }
}
void SolidPulse_hsv(uint8_t h, uint8_t s, uint8_t v){
  SolidColor_hsv(h, s, v);
  EVERY_N_MILLISECONDS(t_fadeTime){
    FastLED.setBrightness(cubicwave8(pulse));
    pulse++;
  }
}
