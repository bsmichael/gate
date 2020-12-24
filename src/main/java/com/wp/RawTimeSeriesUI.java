package com.wp;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class RawTimeSeriesUI extends ApplicationFrame {

    private final static int NUM_DATA_POINTS = 200;

    private final TimeSeries series = new TimeSeries( "Gate Data" );

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

    public void addValue(final long time, final double value) {
        if (series.getItemCount() > NUM_DATA_POINTS) {
            series.delete(0, 1);
        }
        // TODO: determine millisecond from time provided
        series.addOrUpdate(new Millisecond(), value);
    }

    private JFreeChart createChart(final XYDataset dataset) {
        return ChartFactory.createTimeSeriesChart(
                "Received Data",
                "Seconds",
                "Value",
                dataset,
                false,
                false,
                false);
    }

    public static void main( final String[ ] args ) {
        final RawTimeSeriesUI demo = new RawTimeSeriesUI();
        demo.pack();
        RefineryUtilities.positionFrameRandomly(demo);
        demo.setVisible(true);
    }
}
