package com.wp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays raw captured data as a time series graph.
 */
public class RawTimeSeriesUI extends ApplicationFrame implements DataProcessor, AdjustmentListener, ActionListener {

    /**
     * Number of data points to display in the time series.
     */
    private final static int NUM_DATA_POINTS = 600;

    /**
     * Time series data.
     */
    private final TimeSeries series = new TimeSeries("Gate Data");

    private final JScrollBar timeWindowScrollBar = new JScrollBar();

    private final JTextField timeWindowTextField = new JTextField(10);

    private final JLabel processingLabel = new JLabel("Processing");

    private final JLabel timeWindowScrollLabel = new JLabel("Time Window");

    private final JScrollBar timeIntervalScrollBar = new JScrollBar();

    private final JTextField timeIntervalTextField = new JTextField(10);

    private final JLabel timeIntervalScrollLabel = new JLabel("Time Interval");

    private final Map<Long, Double> data = new HashMap<>();

    private ChartPanel chartPanel = null;

    /**
     * Display a time series chart of time series data.
     */
    public RawTimeSeriesUI() {
        super("Raw Whispering Pines Gate Data");
        final XYDataset dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1600, 370));
        chartPanel.setMouseZoomable(true, false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(chartPanel);

        processingLabel.setVisible(false);
        mainPanel.add(processingLabel);

        JPanel timeIntervalPanel = new JPanel();
        timeIntervalScrollBar.setOrientation(JScrollBar.HORIZONTAL);
        timeIntervalScrollBar.addAdjustmentListener(this);
        timeIntervalPanel.add(timeIntervalScrollLabel);
        timeIntervalPanel.add(timeIntervalScrollBar);
        timeIntervalTextField.addActionListener(this);
        timeIntervalPanel.add(timeIntervalTextField);
        mainPanel.add(timeIntervalPanel);

        JPanel timeWindowPanel = new JPanel();
        timeWindowScrollBar.setOrientation(JScrollBar.HORIZONTAL);
        timeWindowScrollBar.addAdjustmentListener(this);
        timeWindowPanel.add(timeWindowScrollLabel);
        timeWindowPanel.add(timeWindowScrollBar);
        timeWindowTextField.addActionListener(this);
        timeWindowPanel.add(timeWindowTextField);
        mainPanel.add(timeWindowPanel);

        timeIntervalScrollBar.setValue(timeIntervalScrollBar.getMaximum());
        timeWindowScrollBar.setValue(timeWindowScrollBar.getMaximum());
        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    private void updateScrollBars(int intervalValue, int windowValue) {
        long minTime = data.keySet().stream().findFirst().orElse(0L);
        long maxTime = data.keySet().stream().findFirst().orElse(0L);
        for (Long key : data.keySet()) {
            if (key < minTime) {
                minTime = key;
            }
            if (key > maxTime) {
                maxTime = key;
            }
        }
        final int maxDuration = (int)(maxTime - minTime);
        //timeIntervalScrollBar.removeAdjustmentListener(this);
        timeIntervalScrollBar.setMinimum(0);
        timeIntervalScrollBar.setMaximum(maxDuration);
        timeIntervalScrollBar.setValue(intervalValue);
        //timeIntervalScrollBar.addAdjustmentListener(this);
        timeIntervalScrollLabel.setText("Time Interval: " +
                " (" + timeIntervalScrollBar.getMinimum() +
                " - " + timeIntervalScrollBar.getMaximum() + ") microseconds");
        timeIntervalTextField.setText(String.valueOf(intervalValue));
        //timeWindowScrollBar.removeAdjustmentListener(this);
        timeWindowScrollBar.setMinimum(0);
        timeWindowScrollBar.setMaximum(maxDuration);
        timeWindowScrollBar.setValue(windowValue);
        //timeWindowScrollBar.addAdjustmentListener(this);
        timeWindowScrollLabel.setText("Time Window: " +
                " (" + timeWindowScrollBar.getMinimum() +
                " - " + timeWindowScrollBar.getMaximum() + ") microseconds");
        timeWindowTextField.setText(String.valueOf(windowValue));
    }

    /**
     * Adds data to the time series.
     *
     * @param time data point was captured
     * @param value of the data captured
     */
    @Override
    public void addData(final long time, final double value) {
        data.put(time, value);
    }

    @Override
    public void finished() {
        updateScrollBars(timeIntervalScrollBar.getValue(), timeWindowScrollBar.getValue());
    }

    /**
     * Create the chart.
     *
     * @param dataset XYDataset
     * @return JFreeChart
     */
    private JFreeChart createChart(final XYDataset dataset) {
        return ChartFactory.createTimeSeriesChart(
                "Received Data",
                "Microseconds",
                "Value",
                dataset,
                false,
                false,
                false);
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        final int intervalValue = timeIntervalScrollBar.getValue();
        final int windowValue = timeWindowScrollBar.getValue();
        if (e.getSource() == timeIntervalScrollBar || e.getSource() == timeWindowScrollBar) {
            processAdjustmentChange(intervalValue, windowValue);
        }
        if (e.getSource() == timeIntervalScrollBar) {
            System.out.println("timeIntervalScrollBar moved to " + intervalValue);
        }
        if (e.getSource() == timeWindowScrollBar) {
            System.out.println("timeWindowScrollBar moved to " + windowValue);
        }
        System.out.println("[RawTimeSeriesUI.adjustmentValueChanged()] finished");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timeIntervalTextField || e.getSource() == timeWindowTextField) {
            try {
                int timeIntervalValue = Integer.parseInt(timeIntervalTextField.getText());
                int timeWindowValue = Integer.parseInt(timeWindowTextField.getText());
                if (timeIntervalValue > timeIntervalScrollBar.getMaximum()) {
                    timeIntervalValue = timeIntervalScrollBar.getMaximum();
                }
                if (timeIntervalValue < timeIntervalScrollBar.getMinimum()) {
                    timeIntervalValue = timeIntervalScrollBar.getMinimum();
                }
                if (timeWindowValue > timeWindowScrollBar.getMaximum()) {
                    timeWindowValue = timeWindowScrollBar.getMaximum();
                }
                if (timeWindowValue < timeWindowScrollBar.getMinimum()) {
                    timeWindowValue = timeWindowScrollBar.getMinimum();
                }
                processAdjustmentChange(timeIntervalValue, timeWindowValue);
            } catch (NumberFormatException nfe) {
                System.out.println(nfe.getMessage());
            }
        }
    }

    private void processAdjustmentChange(final int intervalValue, final int windowValue) {
            series.clear();
            processingLabel.setVisible(true);
            chartPanel.setVisible(false);
            final long start = windowValue;
            final long end = Math.min(intervalValue + start, timeWindowScrollBar.getMaximum());
            System.out.println("Setting series data between start: "+start+" and end: " + end);
            int count = 0;
            for (Long key : data.keySet()) {
                if (key > start && key < end) {
                    series.addOrUpdate(new Millisecond(new Date(key)), data.get(key));
                    count++;
                }
            }
            chartPanel.setVisible(true);
            processingLabel.setVisible(false);
            System.out.println("Added " + count + " data points.");
            updateScrollBars(intervalValue, windowValue);
    }
}
