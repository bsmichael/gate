package com.wp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logs raw data to a file.
 */
public class RawDataLogger implements DataProcessor {

    /**
     * File name prefix.
     */
    private final static String PREFIX = "111000111";

    /**
     * PrintWriter.
     */
    private PrintWriter file = null;

    /**
     * Constructor.
     */
    public RawDataLogger() {
        try {
            file = new PrintWriter(new BufferedWriter(new FileWriter(PREFIX + "_raw.csv")));
            file.println("time (ms),value");
            file.flush();
        } catch (IOException ioe) {
            // Do something
        }
    }

    /**
     * Adds data for processing.
     *
     * @param time data point was captured
     * @param value of the data captured
     */
    @Override
    public void addData(final long time, final double value) {
        if (file != null) {
            file.print(time);
            file.print(",");
            file.println(value);
            file.flush();
        }
    }

}
