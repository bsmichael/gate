#define DATA A1
#define LED 4
#define DATAPOINTS 200

int loopCount = 0;

typedef struct {
  unsigned long t;
  int sample;
} sampleDatapoint;

sampleDatapoint data[DATAPOINTS];

// Setup data and LED pins as well as the serial bus for sending data
void setup() {
  pinMode(DATA, INPUT);
  pinMode(LED, OUTPUT);
  Serial.begin(9600);
  while (! Serial);  // Wait until Serial is ready
  Serial.println("Start");
  for (int i = 0; i < DATAPOINTS; i++) {
    data[i].t = 0;
    data[i].sample = 0;    
  }
}

// Read data from radio receiver's data wire and send it 
// (and the time in which it was read) to the serial bus.
void loop() {
  data[loopCount].t = millis();
  data[loopCount].sample = analogRead(DATA);
  loopCount++;
  if (loopCount > DATAPOINTS) {
    for (int i = 0; i < DATAPOINTS; i++) {
      String msg = "a";
      msg.concat(data[i].t);
      msg.concat(",");
      msg.concat(data[i].sample);
      msg.concat("z");
      msg.concat(i);
      msg.concat(";");
      Serial.print(msg);
      data[i].t = 0;
      data[i].sample = 0;
    }
    loopCount = 0;
  }
  delay(0.5);
}

// Function to be called once bit pattern is deciphered to open the gate
// (simulated by lighting an LED)
void validSignal() {
  digitalWrite(LED, HIGH);   // turn the LED on
  delay(1000);               // wait for a second
  digitalWrite(LED, LOW);    // turn the LED off
}
