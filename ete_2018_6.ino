// Pins assigned to Echo and Trig pins of 2 utlrasounds - PWM pins
#define ECHO_L 1
#define TRIG_L 2
#define ECHO_R 3
#define TRIG_R 4

// Pins assigned to vibration modules - Non PWM pins
#define VIB_1 3
#define VIB_2 5
#define VIB_3 6
#define VIB_4 9
#define VIB_5 10
#define VIB_6 11
#define DEFAULT_DELAY 75
#define VIBRATION_MAGNITUDE 127

// Safe distance
#define safeDistanceHigh 20
#define safeDistanceLow 5

// Measurement delay
#define INTERVAL 300

// Device Activation.
bool isActivated = true;

float getDistance(int trigPin, int echoPin, int device);
void vibrate(int pin, int vibrateMagnitude);
void vibrateClock();
void vibrateAnti();
void vibrateClockAnti();
void vibrateAll();

/*
 * Idea: A 300 millisec delay for comparision. With the first dip/increase from a base value, 
 * check for a second dip/increase in the value to judge obstacle approaching, or an opening nearby.
 * 
 * Code individual gestures.
*/

void setup() {
  //Initialize Ultrasound pins
  /*
  pinMode(ECHO_L, INPUT);
  pinMode(TRIG_L, OUTPUT);
  pinMode(ECHO_R, INPUT);
  pinMode(TRIG_R, OUTPUT);*/

  // Iniitialize Vibration modules pins as out pins
  pinMode(VIB_1, OUTPUT);
  pinMode(VIB_2, OUTPUT);
  pinMode(VIB_3, OUTPUT);
  pinMode(VIB_4, OUTPUT);
  pinMode(VIB_5, OUTPUT);
  pinMode(VIB_6, OUTPUT);

  //Serial initialization
  Serial.begin(9600);
}

void loop() {
  // Get distance in float in cm
  float leftDistanceBase = getDistance(TRIG_L, ECHO_L, "Lb");
  float rightDistanceBase = getDistance(TRIG_R, ECHO_R, "Rb");

  if (leftDistanceBase >= safeDistanceHigh || leftDistanceBase <= safeDistanceLow || rightDistanceBase >= safeDistanceHigh || rightDistanceBase <= safeDistanceLow){
    if (leftDistanceBase <= safeDistanceLow){
      vibrateAnti();
    } else if (rightDistanceBase <= safeDistanceLow){
      vibrateClock();
    }
  }
  delay(1000);
}

float getDistance(int trigPin, int echoPin, String device) {
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  // Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  // Reads the echoPin, returns the sound wave travel time in microseconds
  float duration = pulseIn(echoPin, HIGH);
  // Calculating the distance
  float distance= (float) (duration*0.034/2);
  // Prints the distance on the Serial Monitor
  Serial.print("Distance: Device ");
  Serial.print(device);
  Serial.print("   ");
  Serial.println(distance);
  return distance;
}

void vibrate(int pin, int vibrationMagnitude, int input_delay){
  analogWrite(pin, vibrationMagnitude);
  delay(input_delay);
  analogWrite(pin, 0);
}

void vibrateClock(){  
  // Vibrate 1,2,3,4,5,6
  vibrate(VIB_1, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_2, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_3, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_4, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_5, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_6, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
}

void vibrateAnti(){
  //Vibrate 6,5,4,3,2,1
  vibrate(VIB_6, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_5, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_4, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_3, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_2, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
  vibrate(VIB_1, VIBRATION_MAGNITUDE, DEFAULT_DELAY);
}

void vibrateClockAnti(){
  vibrateClock();
  vibrateAnti(); 
}

void vibrateAll(){
  // Vibrate 1,2,3,4,5,6 at once
  vibrate(VIB_1, VIBRATION_MAGNITUDE, 0);
  vibrate(VIB_2, VIBRATION_MAGNITUDE, 0);
  vibrate(VIB_3, VIBRATION_MAGNITUDE, 0);
  vibrate(VIB_4, VIBRATION_MAGNITUDE, 0);
  vibrate(VIB_5, VIBRATION_MAGNITUDE, 0);
  vibrate(VIB_6, VIBRATION_MAGNITUDE, 0); 
}

