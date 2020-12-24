package com.wp;

/**
 * Gate Application.
 */
public class App {

    /**
     * Capture data from Arduino.
     */
    private static Capture capture;

    /**
     * Test binary hypothesis.
     */
    private static BinaryHypothesis binaryHypothesis;

    /**
     * Displays raw data as a time series.
     */
    private static RawTimeSeriesUI rawTimeSeriesUI;

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        capture = new Capture();
        capture.registerDataProcessor(new BinaryHypothesis());
        capture.registerDataProcessor(new SumHypothesis());
        capture.registerDataProcessor(new RawTimeSeriesUI());
        final Thread thread = new Thread(capture);
        thread.start();
    }
}
