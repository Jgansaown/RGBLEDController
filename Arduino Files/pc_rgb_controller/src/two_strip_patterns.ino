#include <Arduino.h>

void rainbow_twoStrip(){
  fill_rainbow( leds1, NUM_LEDS_PER_STRIP, gHue, 7);
  fill_rainbow( leds2, NUM_LEDS_PER_STRIP, gHue, 7);
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void rainbow_circle_twoStrip(){
  fill_rainbow( leds1, NUM_LEDS_PER_STRIP, gHue, 7);
  fill_rainbow( leds2, NUM_LEDS_PER_STRIP, 255-gHue, 7);
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void sinelon_twoStrip()
{
  // a colored dot sweeping back and forth, with fading trails
  fadeToBlackBy( leds1, NUM_LEDS_PER_STRIP, t_fadeTime);
  fadeToBlackBy( leds2, NUM_LEDS_PER_STRIP, t_fadeTime);
  int pos = beatsin16(beat,0,NUM_LEDS_PER_STRIP);
  leds1[pos] += CHSV( gHue, 255, 192);
  leds2[pos] += CHSV( gHue, 255, 192);

  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

uint8_t looping(int num, int delta, int min, int max){
  int temp = num + delta;
  if (temp >= max){
    return temp - max;
  }else if (temp < min){
    return max + temp;
  }else{
    return temp;
  }
}

int pos = 0;
bool firstStrip = true;
void circle(){
  fadeToBlackBy( leds, NUM_LEDS, t_fadeTime);
  leds[pos] = CRGB( r, g, b);
  EVERY_N_MILLISECONDS( t_patternCycle ) {
    if (firstStrip){//if at first strip
      pos++;
      if (pos == NUM_LEDS_PER_STRIP){//if pos reach the end
        firstStrip = false;
        pos = NUM_LEDS-1;
      }
    }else{//if at second strip
      pos--;
      if (pos == NUM_LEDS_PER_STRIP-1){//if pos reach the end
        firstStrip = true;
        pos = 0;
      }
    }
  }
}

void circle_rainbow(){
  fadeToBlackBy( leds, NUM_LEDS, t_fadeTime);
  leds[pos] = CHSV( gHue, 255, 192);
  EVERY_N_MILLISECONDS( t_patternCycle ) {
    if (firstStrip){//if at first strip
      pos++;
      if (pos == NUM_LEDS_PER_STRIP){//if pos reach the end
        firstStrip = false;
        pos = NUM_LEDS-1;
      }
    }else{//if at second strip
      pos--;
      if (pos == NUM_LEDS_PER_STRIP-1){//if pos reach the end
        firstStrip = true;
        pos = 0;
      }
    }
  }
  EVERY_N_MILLISECONDS( t_hueCycle ) { gHue++; } // slowly cycle the "base color" through the rainbow
}

void alternate(){
  fadeToBlackBy( leds, NUM_LEDS, t_fadeTime);
  pos = looping(pos, 0, 0, NUM_LEDS);
  leds[pos] = CRGB( r, g, b);
  EVERY_N_MILLISECONDS( t_patternCycle ) { pos++;}
}

void alternate_opposite(){
  fadeToBlackBy( leds, NUM_LEDS, t_fadeTime);
  pos = looping(pos, 0, 0, NUM_LEDS);
  leds[pos] = CRGB( r, g, b);
  EVERY_N_MILLISECONDS( t_patternCycle ) { pos--;}
}
