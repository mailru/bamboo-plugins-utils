package ru.mail.bamboo.plugins.report;

import com.atlassian.bamboo.charts.collater.TimePeriodCollater;
import com.atlassian.bamboo.charts.collater.TimePeriodLabelUsageCountCollater;
import com.atlassian.bamboo.reports.collector.ReportCollector;
import com.atlassian.bamboo.resultsummary.ResultsSummary;
import com.atlassian.bamboo.utils.Comparators;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.*;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import ru.mail.jira.plugins.commons.CommonUtils;

import java.util.*;

public class MultiLabelUsageCountCollector implements ReportCollector {
    private static final String LABEL_TARGET_PARAMETER = "labelTarget";
    private List<? extends ResultsSummary> resultsList;
    private Map<String, String[]> params;

    @Override
    @NotNull
    public Dataset getDataset() {
        TimeTableXYDataset dataset = new TimeTableXYDataset();
        String labelsTarget = null;

        if (params.containsKey(LABEL_TARGET_PARAMETER) && params.get(LABEL_TARGET_PARAMETER).length == 1)
            labelsTarget = params.get(LABEL_TARGET_PARAMETER)[0];
        if (StringUtils.isNotEmpty(labelsTarget))
            for (String labelTarget : CommonUtils.split(labelsTarget)) {
                Collection<TimePeriodCollater> finalPeriods = getFinalPeriods(dataset, labelTarget);
                for (final TimePeriodCollater timePeriodCollater : finalPeriods)
                    writeCollaterToDataSet(dataset, timePeriodCollater, labelTarget);
            }
        else {
            Collection<TimePeriodCollater> finalPeriods = getFinalPeriods(dataset, null);
            for (final TimePeriodCollater timePeriodCollater : finalPeriods)
                writeCollaterToDataSet(dataset, timePeriodCollater, null);
        }

        return dataset;
    }

    protected Collection<TimePeriodCollater> getFinalPeriods(TimeTableXYDataset dataset, String label) {
        Map<String, TimePeriodCollater> seriesToPeriodMap = Maps.newHashMap();

        for (final ResultsSummary summary : resultsList) {
            String key = getSeriesKey(summary);
            Date buildDate = summary.getStatDate();
            TimePeriodCollater collaterForSeries = seriesToPeriodMap.get(key);

            if (collaterForSeries == null) {
                collaterForSeries = createCollater(getPeriod(buildDate, getPeriodRange()), key, label);
                collaterForSeries.addResult(summary);
                seriesToPeriodMap.put(key, collaterForSeries);
            } else if (isInPeriod(collaterForSeries.getPeriod(), buildDate)) {
                collaterForSeries.addResult(summary);
            } else {
                writeCollaterToDataSet(dataset, collaterForSeries, label);

                RegularTimePeriod nextPeriod = collaterForSeries.getPeriod().next();

                while (!isInPeriod(nextPeriod, buildDate)) {
                    collaterForSeries = createCollater(nextPeriod, key, label);
                    writeCollaterToDataSet(dataset, collaterForSeries, label);
                    nextPeriod = nextPeriod.next();
                }

                collaterForSeries = createCollater(nextPeriod, key, label);
                collaterForSeries.addResult(summary);
                seriesToPeriodMap.put(key, collaterForSeries);
            }
        }
        return seriesToPeriodMap.values();
    }

    protected String getSeriesKey(@NotNull final ResultsSummary summary) {
        return summary.getPlanKey().getKey();
    }

    @Override
    public String getPeriodRange() {
        String periodRange = "AUTO";

        if (params.containsKey("groupByPeriod")) {
            String[] groupBy = params.get("groupByPeriod");

            if (groupBy != null && groupBy.length > 0)
                periodRange = groupBy[0];
        }

        if ("AUTO".equals(periodRange)) {
            ResultsSummary first = resultsList.get(0);
            ResultsSummary last = resultsList.get(resultsList.size() - 1);

            periodRange = getAutoDate(first.getStatDate(), last.getStatDate());
        }

        return periodRange;
    }

    protected String getAutoDate(Date startDate, Date lastDate) {
        DateTime start = new DateTime(startDate);
        Interval threeMonths = new Interval(start, Period.months(3));
        Interval threeWeeks = new Interval(start, Period.weeks(3));

        if (!threeMonths.contains(lastDate.getTime()))
            return "MONTH";
        else if (!threeWeeks.contains(lastDate.getTime()))
            return "WEEK";
        else
            return "DAY";
    }

    protected RegularTimePeriod getPeriod(Date date, String periodRange) {
        if ("YEAR".equals(periodRange))
            return new Year(date);
        else if ("WEEK".equals(periodRange))
            return new Week(date);
        else if ("DAY".equals(periodRange))
            return new Day(date);
        else if ("MONTH".equals(periodRange))
            return new Month(date);
        else
            return new Month(date);
    }

    protected boolean isInPeriod(@Nullable final RegularTimePeriod time, Date date) {
        return time != null && date.getTime() >= time.getFirstMillisecond() && date.getTime() <= time.getLastMillisecond();
    }

    protected void writeCollaterToDataSet(TimeTableXYDataset dataset, TimePeriodCollater collater, String label) {
        Double value = collater.getValue();

        if (value != 0)
            dataset.add(collater.getPeriod(), value, label);
        else if (StringUtils.isNotEmpty(label))
            dataset.add(collater.getPeriod(), null, label, true);
        else
            dataset.add(collater.getPeriod(), null, collater.getSeriesName(), true);
    }

    protected TimePeriodCollater createCollater(RegularTimePeriod nextPeriod, String key, String label) {
        TimePeriodCollater collaterForSeries;

        collaterForSeries = getCollater(label);
        collaterForSeries.setPeriod(nextPeriod);
        collaterForSeries.setSeriesName(key);
        return collaterForSeries;
    }

    protected TimePeriodCollater getCollater(String label) {
        return new TimePeriodLabelUsageCountCollater(label);
    }

    @Override
    public void setResultsList(@NotNull List<? extends ResultsSummary> resultsList) {
        this.resultsList = Comparators.getBuildDateOrdering().sortedCopy(resultsList);
    }

    @Override
    public void setParams(@NotNull Map<String, String[]> params) {
        this.params = params;
    }
}
