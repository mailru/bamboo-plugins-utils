package ru.mail.bamboo.plugins.report;

import com.atlassian.bamboo.reports.charts.BambooReportLineChart;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

public class BuildSummaryMultiLabelUsageCountLineChart  extends BambooReportLineChart implements XYToolTipGenerator {
    public BuildSummaryMultiLabelUsageCountLineChart() {
        setyAxisLabel("# Times Label Used");
    }

    @Override
    public String generateToolTip(XYDataset xyDataset, int series, int item) {
        TimeTableXYDataset dataset = (TimeTableXYDataset) xyDataset;

        int labelUsages = new Double(dataset.getYValue(series, item)).intValue();
        String label = (String) dataset.getSeriesKey(series);
        TimePeriod timePeriod = dataset.getTimePeriod(item);

        return "Build plan on average had " + labelUsages + " label \"" + label + "\" usages for " + timePeriod;
    }
}
