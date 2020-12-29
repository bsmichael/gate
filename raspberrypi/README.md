# Receiver to Raspberry Pi setup

Connect red wire (+5v) to GPIO pin 4.
Connect black wire (GND) to GPIO pin 6.
Connect green wire (data) to GPIO pin 11.

GPIO pin layout is as follows:

    2 4 6 8 10 12 ...
    1 3 5 7 9 11 ...
Note: Physical pin #11 is known as pin #17 in software.  See https://learn.sparkfun.com/tutorials/raspberry-gpio/all for pin details.

Compile program by running:
    gcc -wall -o gate gate.c -l wiringPi

Run program:
    sudo ./gate
