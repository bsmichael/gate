int DATA = A1;
int LED = 4;

// Setup data and LED pins as well as the serial bus for sending data
void setup() {
  pinMode(DATA, INPUT);
  pinMode(LED, OUTPUT);
  Serial.begin(9600);
  while (! Serial);  // Wait until Serial is ready
}

// Read data from radio receiver's data wire and send it 
// (and the time in which it was read) to the serial bus.
void loop() {
  Serial.print("a");
  Serial.print(millis());
  Serial.print(",");
  Serial.print(analogRead(DATA));
  Serial.print("z;");
}

// Function to be called once bit pattern is deciphered to open the gate
// (simulated by lighting an LED)
void validSignal() {
  digitalWrite(LED, HIGH);   // turn the LED on
  delay(1000);               // wait for a second
  digitalWrite(LED, LOW);    // turn the LED off
}
