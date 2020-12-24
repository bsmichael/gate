package com.wp;

import java.io.IOException;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class Capture {

    private static Capture capture;
    private static BinaryHypothesis binaryHypothesis;
    private static RawTimeSeriesUI rawTimeSeriesUI;

    public Capture() {
        SerialPort sp = SerialPort.getCommPort("/dev/cu.usbserial-1430");
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("Port is open :)");
        } else {
            System.out.println("Failed to open port :(");
        }

        final StringBuilder sb = new StringBuilder();
        final byte[] newData = new byte[1];
        while (sp.bytesAvailable() >= 1) {
            sp.readBytes(newData, newData.length);
            final String data = new String(newData);
            if (";".equals(data)) {
                try {
                    final String[] values = sb.toString().split(",");
                    final long value1 = Long.parseLong(values[0]);
                    final double value2 = Double.parseDouble(values[1]);
                    binaryHypothesis.addValue(value1, value2);
                    rawTimeSeriesUI.addValue(value1, value2);
                } catch (NumberFormatException nfe) {
                    // Do nothing
                }
                sb.delete(0, sb.length());
            } else {
                sb.append(data);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        binaryHypothesis = new BinaryHypothesis();
        rawTimeSeriesUI = new RawTimeSeriesUI();
        capture = new Capture();
    }
}
