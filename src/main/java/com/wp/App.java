package com.wp;

/**
 * Gate Application.
 */
public class App {

    /**
     * Main method.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Capture capture = new Capture();
        capture.registerDataProcessor(new RawDataLogger());
        capture.registerDataProcessor(new RawTimeSeriesUI());
        //capture.registerDataProcessor(new BinaryHypothesis());
        //capture.registerDataProcessor(new SumHypothesis());
        final Thread thread = new Thread(capture);
        thread.start();
    }
}
