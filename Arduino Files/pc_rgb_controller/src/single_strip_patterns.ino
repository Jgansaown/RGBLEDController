#include <Arduino.h>

// List of patterns to cycle through.  Each is defined as a separate function.
typedef void (*SimplePatternList[])();
SimplePatternList gPatterns = { rainbow_singleStrip, rainbowWithGlitter, confetti, sinelon, juggle, bpm };

uint8_t gCurrentPatternNumber = 0; // Index number of which pattern is current

void demoReel100(){
  // Call the current pattern function once, updating the 'leds' array
  gPatterns[gCurrentPatternNumber]();

  // do some periodic updates
  EVERY_N_SECONDS( 10 ) { nextPattern(); } // change patterns periodically
}


#define ARRAY_SIZE(A) (sizeof(A) / sizeof((A)[0]))
void nextPattern()
{
  // add one to the current pattern number, and wrap around at the end
  gCurrentPatternNumber = (gCurrentPatternNumber + 1) % ARRAY_SIZE( gPatterns);
}

void rainbow_singleStrip()
{
  // FastLED's built-in rainbow generator
  fill_rainbow( leds, NUM_LEDS, gHue, 7);
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void rainbowWithGlitter()
{
  // built-in FastLED rainbow, plus some random sparkly glitter
  rainbow_singleStrip();
  addGlitter(80);
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void addGlitter( fract8 chanceOfGlitter)
{
  if( random8() < chanceOfGlitter) {
    leds[ random16(NUM_LEDS) ] += CRGB::White;
  }
}

void confetti()
{
  // random colored speckles that blink in and fade smoothly
  fadeToBlackBy( leds, NUM_LEDS, 10);
  int pos = random16(NUM_LEDS);
  leds[pos] += CHSV( gHue + random8(64), 200, 255);
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void sinelon()
{
  // a colored dot sweeping back and forth, with fading trails
  fadeToBlackBy( leds, NUM_LEDS, 20);
  int pos = beatsin16(13,0,NUM_LEDS);
  leds[pos] += CHSV( gHue, 255, 192);
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void bpm()
{
  // colored stripes pulsing at a defined Beats-Per-Minute (BPM)
  uint8_t BeatsPerMinute = 62;
  CRGBPalette16 palette = PartyColors_p;
  uint8_t beat = beatsin8( BeatsPerMinute, 64, 255);
  for( int i = 0; i < NUM_LEDS; i++) { //9948
    leds[i] = ColorFromPalette(palette, gHue+(i*2), beat-gHue+(i*10));
  }
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void juggle() {
  // eight colored dots, weaving in and out of sync with each other
  fadeToBlackBy( leds, NUM_LEDS, 20);
  byte dothue = 0;
  for( int i = 0; i < 8; i++) {
    leds[beatsin16(i+7,0,NUM_LEDS)] |= CHSV(dothue, 200, 255);
    dothue += 32;
  }
  // do some periodic updates
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}
