package com.wp;

import com.fazecast.jSerialComm.SerialPort;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Captures data emitted from Arduino.
 */
public class Capture implements Runnable {

    /**
     * Serial port where Arduino may be found.
     */
    private final static String SERIAL_PORT = "/dev/cu.usbserial-1430";

    /**
     * Pattern of data packet captured by Arduino.
     */
    private final static String PATTERN = "a(.*?),(.*?)z";

    /**
     * Number of data values to skip upon start of application.
     * This is to eliminate outlier data.
     */
    private final static int SKIP_COUNT = 5;

    /**
     * Valid data format of captured data.
     */
    private final Pattern capturePattern = Pattern.compile(PATTERN);

    /**
     * List of processors of captured data.
     */
    final List<DataProcessor> processors = new ArrayList<>();

    /**
     * Constructor.
     */
    public Capture() {
        // Do nothing
    }

    /**
     * Registers a data processors so that it may receive captured data.
     *
     * @param dataProcessor DataProcessor
     */
    public void registerDataProcessor(final DataProcessor dataProcessor) {
        processors.add(dataProcessor);
    }

    /**
     * As long as there is data to read, read it!
     */
    @Override
    public void run() {
        SerialPort sp = SerialPort.getCommPort(SERIAL_PORT);
        sp.setComPortParameters(9600, 8, 1, 0);
        sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (sp.openPort()) {
            System.out.println("[Capture] Port is open :)");
        } else {
            System.out.println("[Capture] Failed to open port :(");
        }

        final StringBuilder sb = new StringBuilder();
        final byte[] newData = new byte[1];
        int skipCountIndex = 0;
        while (true) {
            while (sp.bytesAvailable() >= 1) {
                sp.readBytes(newData, newData.length);
                final String data = new String(newData);
                if (";".equals(data)) {
                    if (SKIP_COUNT < skipCountIndex++) {
                        final String bufferData = sb.toString();
                        final Matcher captureMatcher = capturePattern.matcher(bufferData);
                        if (captureMatcher.find()) {
                            try {
                                final long value1 = Long.parseLong(captureMatcher.group(1));
                                final double value2 = Double.parseDouble(captureMatcher.group(2));
                                for (DataProcessor dataProcessor : processors) {
                                    dataProcessor.addData(value1, value2);
                                }
                            } catch (NumberFormatException nfe) {
                                // Do nothing
                            }
                        }
                    }
                    sb.delete(0, sb.length());
                } else {
                    sb.append(data);
                }
            }
        }
    }
}
