#include <Arduino.h>

void extractInput(char* input){
  Serial.println(input);
  int i = 0;
  int numToRead = 0;

  //read function name
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    function = 0;
  }else{
    function = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(function);
  }

  //read red
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    r = 0;
  }else{
    r = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(r);
  }

  //read green
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    g = 0;
  }else{
    g = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(g);
  }

  //read blue
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    b = 0;
  }else{
    b = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(b);
  }

  //read t_hueCycle
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    t_hueCycle = 20;
  }else{
    t_hueCycle = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(t_hueCycle);
  }

  //read t_fadeTime
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    t_fadeTime = 20;
  }else{
    t_fadeTime = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(t_fadeTime);
  }

  //read t_patternCycle
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    t_patternCycle = 20;
  }else{
    t_patternCycle = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(t_patternCycle);
  }

  //read beat
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    beat = 20;
  }else{
    beat = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(beat);
  }

  //read brightness
  numToRead = input[i]-48;
  //Serial.println(numToRead);
  i++;
  if (numToRead == 0){
    brightness = 100;
  }else{
    brightness = charArrayToString(input, i, numToRead).toInt();
    i += numToRead;
    //Serial.println(brightness);
  }
  choosePattern();
}

String charArrayToString(char* input, int startPos, int numToRead){
  String temp = "";
  for (int i = startPos; i < startPos+numToRead; i++){
      temp += input[i];
  }
  return temp;
}

void choosePattern(){
  int eeAddress = 0;
  EEPROM.put(eeAddress, function);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, r);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, g);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, b);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, t_hueCycle);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, t_fadeTime);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, t_patternCycle);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, beat);
  eeAddress += sizeof(int);
  EEPROM.put(eeAddress, brightness);



  Serial.print("Function Name: ");
  Serial.println(function);

  Serial.print("r: ");
  Serial.print(r);
  Serial.print(", g: ");
  Serial.print(g);
  Serial.print(", b: ");
  Serial.println(b);

  Serial.print("Hue Cycle: ");
  Serial.print(t_hueCycle);
  Serial.print(", Fade Time: ");
  Serial.print(t_fadeTime);
  Serial.print(", Pattern Cycle: ");
  Serial.print(t_patternCycle);
  Serial.print(", Beat: ");
  Serial.println(beat);


  Serial.print("Brightness: ");
  Serial.println(brightness);

  FastLED.setBrightness(brightness*255/100);
  //patterns[function]();
}
