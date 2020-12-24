package com.wp;

/**
 * Interface all data processors need to implement to receive data.
 */
public interface DataProcessor {

    /**
     * Adds data for processing.
     *
     * @param time data point was captured
     * @param value of the data captured
     */
    void addData(final long time, final double value);
}
