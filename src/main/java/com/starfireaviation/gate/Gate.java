package com.starfireaviation.gate;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;

public class Gate {

    /**
     * Write messages to standard out?
     */
    private final static Boolean DEBUG = Boolean.FALSE;

    private int[] READ_CODE = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int[] VALID_CODE = { 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1 };

    // Tolerances in nanoseconds
    private static final int SHORT_LOWER_BOUND = 386000;
    private static final int SHORT_UPPER_BOUND = 550000;
    private static final int LONG_LOWER_BOUND = 3170000;
    private static final int LONG_UPPER_BOUND = 3360000;
    private static final int MIN_MID_CODE_DIVIDE_DURATION = 25000000;
    private static final int MAX_MID_CODE_DIVIDE_DURATION = 26000000;
    private static final int TRANSITION_COUNT = 36;

    /**
     * Reader of data from radio receiver.
     */
    private Reader reader;

    /**
     * Ordered data retrieved from reader.
     */
    private TreeMap<Long, Integer> data;

    /**
     * GPIO PIN Digital Output.
     */
    private GpioPinDigitalOutput pin;

    /**
     * Constructor.
     */
    public Gate() {
        // Initialize GPIO, reader, and data map
        final GpioController gpio = GpioFactory.getInstance();
        reader = new Reader(gpio);
        data = new TreeMap<>();
        Thread readerThread = new Thread(reader);
        readerThread.start();

        pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "Gate", PinState.LOW);

        // set shutdown state for this pin
        pin.setShutdownOptions(true, PinState.LOW);

        // Loop forever...
        while (true) {
            populateData();
            final List<Long> interCodeBreaksKeys = findInterCodeBreaks();
            log("Found " + interCodeBreaksKeys.size() + " potential codes.");
            for (Long key : interCodeBreaksKeys) {
                checkCode(getDataGroup(key, TRANSITION_COUNT));
            }
        }
    }

    /**
     * Checks acquired DataSet for bit pattern(s), then compares the bits to the valid code.
     *
     * @param dataSetList list of DataSets
     */
    private void checkCode(final List<DataSet> dataSetList) {
        final Instant start = Instant.now();
        boolean shortOn = false;
        boolean longOn = false;
        boolean shortOff = false;
        boolean longOff = false;
        if (dataSetList.size() != TRANSITION_COUNT) {
            return;
        }
        int index = 0;
        for (DataSet dataSet : dataSetList) {
            if (dataSet.getValue() == 1 &&
                dataSet.getDiff() > LONG_LOWER_BOUND &&
                dataSet.getDiff() < LONG_UPPER_BOUND) {
                longOn = true;
            }
            if (dataSet.getValue() == 1 &&
                dataSet.getDiff() > SHORT_LOWER_BOUND &&
                dataSet.getDiff() < SHORT_UPPER_BOUND) {
                shortOn = true;
            }
            if (dataSet.getValue() == 0 &&
                dataSet.getDiff() > LONG_LOWER_BOUND &&
                dataSet.getDiff() < LONG_UPPER_BOUND) {
                longOff = true;
            }
            if (dataSet.getValue() == 0 &&
                dataSet.getDiff() > SHORT_LOWER_BOUND &&
                dataSet.getDiff() < SHORT_UPPER_BOUND) {
                shortOff = true;
            }
            if (shortOn && longOff) {
                READ_CODE[index] = 1;
                log("\t\t" + readCodeToString());
                shortOn = false; longOn = false; shortOff = false; longOff = false;
                index++;
            } else if (longOn && shortOff) {
                READ_CODE[index] = 0;
                log("\t\t" + readCodeToString());
                shortOn = false; longOn = false; shortOff = false; longOff = false;
                index++;
            } else if (shortOn && longOn) {
                shortOn = false; longOn = false; shortOff = false; longOff = false;
                resetReadCode();
            } else if (shortOff && longOff) {
                shortOn = false; longOn = false; shortOff = false; longOff = false;
                resetReadCode();
            }
        }
        for (int i = 0; i < VALID_CODE.length - 1; i++) {
            if (READ_CODE[i] != VALID_CODE[i]) {
                i = VALID_CODE.length + 1;
            }
            if (i == VALID_CODE.length - 2) {
                openGate();
            }
        }
        resetReadCode();
        final Instant end = Instant.now();
        log("checkCode() took " + Duration.between(start, end));
    }

    /**
     * Resets READ_CODE array for next processing cycle.
     */
    private void resetReadCode() {
        for (int i = 0; i < READ_CODE.length; i++) {
            READ_CODE[i] = 0;
        }
    }

    /**
     * Converts READ_CODE array to a printable string.
     *
     * @return string representation of READ_CODE array
     */
    private String readCodeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i = 0; i < READ_CODE.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(READ_CODE[i]);
        }
        sb.append(" }");
        return sb.toString();
    }

    /**
     * Gets list of DataGroups corresponding to indentified inter-code time key.
     *
     * @param key inter-code time key
     * @param count number of DataSets to retrieve
     * @return list of DataSets
     */
    private List<DataSet> getDataGroup(final Long key, final Integer count) {
        final Instant start = Instant.now();
        final List<DataSet> list = new ArrayList<>();
        int cnt = 0;
        Long lastKey = 0L;
        boolean canPrint = false;
        for (Long k : data.keySet()) {
            if (k == key) {
                canPrint = true;
            }
            if (cnt > count) {
                canPrint = false;
                break;
            }
            if (canPrint) {
                cnt++;
                list.add(new DataSet(k, data.get(k), k - lastKey));
            }
            lastKey = k;
        }
        final Instant end = Instant.now();
        log("getDataGroup() took " + Duration.between(start, end));
        return diffShift(list);
    }

    /**
     * Shifts diff values within list of DataSets.
     *
     * @param unshifted list of DataSets
     * @return list of DataSets with diffs shifted
     */
    private List<DataSet> diffShift(final List<DataSet> unshifted) {
        final Instant start = Instant.now();
        final List<DataSet> shifted = new ArrayList<>();
        long lastKey = 0L;
        int lastValue = -1;
        for (DataSet dataSet : unshifted) {
            if (lastKey > 0 && lastValue >= 0) {
                shifted.add(new DataSet(lastKey, lastValue, dataSet.getDiff()));
            }
            lastKey = dataSet.getKey() == null ? 0L : dataSet.getKey();
            lastValue = dataSet.getValue() == null ? -1 : dataSet.getValue();
        }
        final Instant end = Instant.now();
        log("diffShift() took " + Duration.between(start, end));
        return shifted;
    }

    /**
     * Searches data for potential inter-code breaks.
     *
     * @return list of times indicating a potential code location
     */
    private List<Long> findInterCodeBreaks() {
        final Instant start = Instant.now();
        final List<Long> keys = new ArrayList<>();
        Long lastKey = 0L;
        for (Long key : data.keySet()) {
            final Long diff = key - lastKey;
            if (diff > MIN_MID_CODE_DIVIDE_DURATION && diff < MAX_MID_CODE_DIVIDE_DURATION) {
                keys.add(key);
                log("Adding key: " + key + "; duration: " + diff);
            }
            lastKey = key;
        }
        final Instant end = Instant.now();
        log("findInterCodeBreaks() took " + Duration.between(start, end));
        return keys;
    }

    /**
     * Logs out information message, if DEBUG is enabled.
     *
     * @param msg to be logged
     */
    private void log(String msg) {
        if (DEBUG) {
            System.out.println(msg);
        }
    }

    /**
     * Retrieves data from reader, orders the data in chronological order, then prunes out any extraneous data.
     */
    private void populateData() {
        final Instant start = Instant.now();
        data.clear();
        final ConcurrentMap<Long, Integer> map = reader.getData();
        log("Retrieved " + map.size() + " records from reader.");

        // Convert ConcurrentMap to a TreeMap to ensure times are in ascending order
        TreeMap<Long, Integer> localMap = new TreeMap<>();
        for (Long time : map.keySet()) {
            localMap.put(time, map.get(time));
        }

        // Insert only bit flip values into data map
        Integer lastValue = -1;
        for (Long key : localMap.keySet()) {
            final Integer value = localMap.get(key);
            if (lastValue != value) {
                lastValue = value;
                data.put(key, value);
            }
        }

        log(data.size() + " records inserted into data TreeMap.");
        final Instant end = Instant.now();
        log("populateData() took " + Duration.between(start, end));
    }

    /**
     * Sends signal to open gate.
     */
    private void openGate() {
        System.out.println("--> GPIO state should be: ON");

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Caught InterruptedException: " + e.getMessage());
        }

        // turn off gpio pin #02
        pin.low();
        System.out.println("--> GPIO state should be: OFF");
    }

    /**
     * Main method for application.
     *
     * @param args command line arguments
     */
    public static void main(String args[]) {
        new Gate();
    }
}
