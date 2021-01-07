#!/bin/sh
cd /home/pi/gate_raspberrypi
mvn clean package
java -jar /home/pi/gate_raspberrypi/target/gate_raspberrypi-1.0-SNAPSHOT.jar
