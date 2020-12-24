package com.wp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import java.util.Date;

/**
 * Displays raw captured data as a time series graph.
 */
public class RawTimeSeriesUI extends ApplicationFrame implements DataProcessor {

    /**
     * Number of data points to display in the time series.
     */
    private final static int NUM_DATA_POINTS = 200;

    /**
     * Time series data.
     */
    private final TimeSeries series = new TimeSeries("Gate Data");

    /**
     * Display a time series chart of time series data.
     */
    public RawTimeSeriesUI() {
        super("Raw Whispering Pines Gate Data");
        final XYDataset dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 370));
        chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
        pack();
        setVisible(true);
    }

    /**
     * Adds data to the time series.
     *
     * @param time data point was captured
     * @param value of the data captured
     */
    @Override
    public void addData(final long time, final double value) {
        if (series.getItemCount() > NUM_DATA_POINTS) {
            series.delete(0, 1);
        }
        series.addOrUpdate(new Millisecond(new Date(time)), value);
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
                "Milliseconds",
                "Value",
                dataset,
                false,
                false,
                false);
    }

}
