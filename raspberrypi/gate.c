#include <stdio.h>
#include <stdlib.h>
#include <wiringPi.h>

const int dataPin = 17;

int main(void)
{
    FILE * fp;
    fp = fopen("111000111.csv", "w+"); // Filename derived from DIP position(s).
    wiringPiSetupGpio();
    pinMode(dataPin, INPUT);
    pullUpDnControl(dataPin, PUD_UP);

    while (1) 
    {
        fprintf(fp, "%d,%d", micros(), digitalRead(dataPin));
    }
    fclose(fp);
    return(0);
}
