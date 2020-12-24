package com.wp;

/**
 * Converts groups of data values into a number.
 * Assumes each data point is a bit to be summed.
 */
public class SumHypothesis implements DataProcessor {

    /**
     * Binary "word" size.
     */
    private final static int CODE_SIZE = 4;

    /**
     * Point at which the data value should be considered a 0 or 1.
     */
    private final static int DIVIDE = 600;

    /**
     * Array index pointer.
     */
    private int codeCount = 0;

    /**
     * Binary data (a.k.a. word)
     */
    private double code[] = new double[CODE_SIZE];

    /**
     * Constructor.
     */
    public SumHypothesis() {
        // Do something
    }

    /**
     * Adds data for processing.
     *
     * @param time data point was captured
     * @param value of the data captured
     */
    @Override
    public void addData(final long time, final double value) {
        if (codeCount >= CODE_SIZE) {
            codeCount = 0;
            printCode();
        }
        if (value < DIVIDE) {
            code[codeCount++] = 0;
        } else {
            code[codeCount++] = 1;
        }
    }

    /**
     * Prints out the decimal equivalent to the binary value captured.
     */
    private void printCode() {
        if (code[0] == 0 && code[1] == 0 && code[2] == 0 && code[3] == 0) {
            log("0");
        } else if (code[0] == 0 && code[1] == 0 && code[2] == 0 && code[3] == 1) {
            log("1");
        } else if (code[0] == 0 && code[1] == 0 && code[2] == 1 && code[3] == 0) {
            log("2");
        } else if (code[0] == 0 && code[1] == 0 && code[2] == 1 && code[3] == 1) {
            log("3");
        } else if (code[0] == 0 && code[1] == 1 && code[2] == 0 && code[3] == 0) {
            log("4");
        } else if (code[0] == 0 && code[1] == 1 && code[2] == 0 && code[3] == 1) {
            log("5");
        } else if (code[0] == 0 && code[1] == 1 && code[2] == 1 && code[3] == 0) {
            log("6");
        } else if (code[0] == 0 && code[1] == 1 && code[2] == 1 && code[3] == 1) {
            log("7");
        } else if (code[0] == 1 && code[1] == 0 && code[2] == 0 && code[3] == 0) {
            log("8");
        } else if (code[0] == 1 && code[1] == 0 && code[2] == 0 && code[3] == 1) {
            log("9");
        }
    }

    /**
     * Writes out computed value.
     *
     * @param line value to be printed
     */
    private void log(final String line) {
        // TODO: write this out to a file?
        System.out.println("[SumHypothesis] " + line);
    }
}
