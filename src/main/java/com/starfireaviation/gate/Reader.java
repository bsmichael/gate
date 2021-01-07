package com.starfireaviation.gate;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.wiringpi.Gpio;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class Reader implements Runnable {

    private static final Integer EXPIRATION_DURATION = 5;
    private static final Integer CACHE_SIZE = 500000;

    /**
     * Cache of GPIO read data.
     *
     * Note: cache is setup to auto-expire data retrieved, so that the application memory is not overrun.
     */
    private LoadingCache<Long, Integer> cache;

    /**
     * GPIO PIN from which to read data.
     */
    private static final int DATA_PIN = 1;

    private GpioController gpio;

    /**
     * Constructor.
     *
     * @param gpioController GpioController
     */
    public Reader(final GpioController gpioController) {
        gpio = gpioController;

        CacheLoader<Long, Integer> loader = new CacheLoader<Long, Integer>() {
            @Override
            public Integer load(Long aLong) throws Exception {
                return null;
            }
        };

        cache = CacheBuilder.newBuilder()
                .maximumSize(CACHE_SIZE)
                .expireAfterWrite(EXPIRATION_DURATION, TimeUnit.SECONDS)
                .build(loader);
    }

    /**
     * Makes collected data available to outside entities.
     *
     * @return map of Time/Value pairs
     */
    public ConcurrentMap<Long, Integer> getData() {
        return cache.asMap();
    }

    /**
     * Infinite loop that constantly reads data from GPIO PIN.
     */
    @Override
    public void run() {
        while (true) {
            Gpio.pinMode(DATA_PIN, Gpio.INPUT);
            if (Gpio.digitalRead(DATA_PIN) == 0) {
                cache.put(System.nanoTime(), 0);
            } else {
                cache.put(System.nanoTime(), 1);
            }
        }
    }
}
