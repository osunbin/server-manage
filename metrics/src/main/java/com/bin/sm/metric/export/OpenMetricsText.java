package com.bin.sm.metric.export;

import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.ClassicHistogramBuckets;
import io.prometheus.metrics.model.snapshots.CounterSnapshot;
import io.prometheus.metrics.model.snapshots.DataPointSnapshot;
import io.prometheus.metrics.model.snapshots.DistributionDataPointSnapshot;
import io.prometheus.metrics.model.snapshots.Exemplar;
import io.prometheus.metrics.model.snapshots.Exemplars;
import io.prometheus.metrics.model.snapshots.GaugeSnapshot;
import io.prometheus.metrics.model.snapshots.HistogramSnapshot;
import io.prometheus.metrics.model.snapshots.InfoSnapshot;
import io.prometheus.metrics.model.snapshots.Labels;
import io.prometheus.metrics.model.snapshots.MetricMetadata;
import io.prometheus.metrics.model.snapshots.MetricSnapshot;
import io.prometheus.metrics.model.snapshots.MetricSnapshots;
import io.prometheus.metrics.model.snapshots.Quantile;
import io.prometheus.metrics.model.snapshots.StateSetSnapshot;
import io.prometheus.metrics.model.snapshots.SummarySnapshot;
import io.prometheus.metrics.model.snapshots.UnknownSnapshot;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import static com.bin.sm.metric.export.WriterHelper.writeDouble;
import static com.bin.sm.metric.export.WriterHelper.writeEscapedLabelValue;
import static com.bin.sm.metric.export.WriterHelper.writeLabels;
import static com.bin.sm.metric.export.WriterHelper.writeLong;
import static com.bin.sm.metric.export.WriterHelper.writeTimestamp;

public class OpenMetricsText {
    String CONTENT_TYPE = "application/openmetrics-text; version=1.0.0; charset=utf-8";

    public String  collect() throws IOException {
        StringWriter writer = new StringWriter();
        MetricSnapshots metricSnapshots = PrometheusRegistry.defaultRegistry.scrape();
        Iterator<MetricSnapshot> iterator = metricSnapshots.iterator();
        while (iterator.hasNext()) {
            MetricSnapshot snapshot = iterator.next();
            if (!snapshot.getDataPoints().isEmpty()) {
                if (snapshot instanceof CounterSnapshot) {
                    writeCounter(writer, (CounterSnapshot)snapshot);
                } else if (snapshot instanceof GaugeSnapshot) {
                    writeGauge(writer, (GaugeSnapshot)snapshot);
                } else if (snapshot instanceof HistogramSnapshot) {
                    writeHistogram(writer, (HistogramSnapshot)snapshot);
                } else if (snapshot instanceof SummarySnapshot) {
                    writeSummary(writer, (SummarySnapshot)snapshot);
                } else if (snapshot instanceof InfoSnapshot) {
                    writeInfo(writer, (InfoSnapshot)snapshot);
                } else if (snapshot instanceof StateSetSnapshot) {
                    writeStateSet(writer, (StateSetSnapshot)snapshot);
                } else if (snapshot instanceof UnknownSnapshot) {
                    writeUnknown(writer, (UnknownSnapshot)snapshot);
                }
            }
        }

        writer.write("# EOF\n");
        writer.flush();
        return writer.toString();
    }



    private void writeCounter(Writer writer, CounterSnapshot snapshot) throws IOException{
        MetricMetadata metadata = snapshot.getMetadata();
        writeMetadata(writer, "counter", metadata);
        Iterator<CounterSnapshot.CounterDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            CounterSnapshot.CounterDataPointSnapshot data = iterator.next();
            writeNameAndLabels(writer, metadata.getPrometheusName(), "_total", data.getLabels());
            writeDouble(writer, data.getValue());
            writeScrapeTimestampAndExemplar(writer, data, data.getExemplar());
            writeCreated(writer, metadata, data);
        }

    }
    boolean exemplarsOnAllMetricTypesEnabled = false;
    private void writeGauge(Writer writer, GaugeSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        writeMetadata(writer, "gauge", metadata);
        Iterator<GaugeSnapshot.GaugeDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            GaugeSnapshot.GaugeDataPointSnapshot data = iterator.next();
            writeNameAndLabels(writer, metadata.getPrometheusName(), (String)null, data.getLabels());
            writeDouble(writer, data.getValue());
            if (this.exemplarsOnAllMetricTypesEnabled) {
                writeScrapeTimestampAndExemplar(writer, data, data.getExemplar());
            } else {
                writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
            }
        }
    }


    private void writeHistogram(Writer writer, HistogramSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        if (snapshot.isGaugeHistogram()) {
            this.writeMetadata(writer, "gaugehistogram", metadata);
            this.writeClassicHistogramBuckets(writer, metadata, "_gcount", "_gsum", snapshot.getDataPoints());
        } else {
            this.writeMetadata(writer, "histogram", metadata);
            this.writeClassicHistogramBuckets(writer, metadata, "_count", "_sum", snapshot.getDataPoints());
        }

    }

    private void writeClassicHistogramBuckets(Writer writer, MetricMetadata metadata, String countSuffix, String sumSuffix, List<HistogramSnapshot.HistogramDataPointSnapshot> dataList) throws IOException {
        HistogramSnapshot.HistogramDataPointSnapshot data;
        Iterator<HistogramSnapshot.HistogramDataPointSnapshot> iterator = dataList.iterator();
        for (; iterator.hasNext(); writeCreated(writer, metadata, data)) {
            data = iterator.next();
            ClassicHistogramBuckets buckets = getClassicBuckets(data);
            Exemplars exemplars = data.getExemplars();
            long cumulativeCount = 0L;

            for(int i = 0; i < buckets.size(); ++i) {
                cumulativeCount += buckets.getCount(i);
                writeNameAndLabels(writer, metadata.getPrometheusName(), "_bucket", data.getLabels(), "le", buckets.getUpperBound(i));
                writeLong(writer, cumulativeCount);
                Exemplar exemplar;
                if (i == 0) {
                    exemplar = exemplars.get(Double.NEGATIVE_INFINITY, buckets.getUpperBound(i));
                } else {
                    exemplar = exemplars.get(buckets.getUpperBound(i - 1), buckets.getUpperBound(i));
                }

                this.writeScrapeTimestampAndExemplar(writer, data, exemplar);
            }

            if (data.hasCount() && data.hasSum()) {
                this.writeCountAndSum(writer, metadata, data, countSuffix, sumSuffix, exemplars);
            }
        }
    }

    private ClassicHistogramBuckets getClassicBuckets(HistogramSnapshot.HistogramDataPointSnapshot data) {
        return data.getClassicBuckets().isEmpty() ? ClassicHistogramBuckets.of(new double[]{Double.POSITIVE_INFINITY}, new long[]{data.getCount()}) : data.getClassicBuckets();
    }


    private void writeSummary(Writer writer, SummarySnapshot snapshot) throws IOException {
        boolean metadataWritten = false;
        MetricMetadata metadata = snapshot.getMetadata();
        Iterator<SummarySnapshot.SummaryDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (true) {
            SummarySnapshot.SummaryDataPointSnapshot data;
            do {
                if (!iterator.hasNext()) {
                    return;
                }
                data = iterator.next();
            }while(data.getQuantiles().size() == 0 && !data.hasCount() && !data.hasSum());

            if (!metadataWritten) {
                this.writeMetadata(writer, "summary", metadata);
                metadataWritten = true;
            }

            Exemplars exemplars = data.getExemplars();
            int exemplarIndex = 1;

            Iterator<Quantile> iterator1 = data.getQuantiles().iterator();
            while(true) {
                while (iterator1.hasNext()) {
                    Quantile quantile = iterator1.next();
                    this.writeNameAndLabels(writer, metadata.getPrometheusName(), (String)null, data.getLabels(), "quantile", quantile.getQuantile());
                    writeDouble(writer, quantile.getValue());
                    if (exemplars.size() > 0 && this.exemplarsOnAllMetricTypesEnabled) {
                        exemplarIndex = (exemplarIndex + 1) % exemplars.size();
                        this.writeScrapeTimestampAndExemplar(writer, data, exemplars.get(exemplarIndex));
                    } else {
                        this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
                    }
                }

                this.writeCountAndSum(writer, metadata, data, "_count", "_sum", exemplars);
                this.writeCreated(writer, metadata, data);
                break;
            }
        }
    }


    private void writeInfo(Writer writer, InfoSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "info", metadata);
        Iterator<InfoSnapshot.InfoDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            InfoSnapshot.InfoDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_info", data.getLabels());
            writer.write("1");
            this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
        }
    }


    private void writeStateSet(Writer writer, StateSetSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "stateset", metadata);
        Iterator<StateSetSnapshot.StateSetDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            StateSetSnapshot.StateSetDataPointSnapshot data = iterator.next();

            for(int i = 0; i < data.size(); ++i) {
                writer.write(metadata.getPrometheusName());
                writer.write(123);

                for(int j = 0; j < data.getLabels().size(); ++j) {
                    if (j > 0) {
                        writer.write(",");
                    }

                    writer.write(data.getLabels().getPrometheusName(j));
                    writer.write("=\"");
                    writeEscapedLabelValue(writer, data.getLabels().getValue(j));
                    writer.write("\"");
                }

                if (!data.getLabels().isEmpty()) {
                    writer.write(",");
                }

                writer.write(metadata.getPrometheusName());
                writer.write("=\"");
                writeEscapedLabelValue(writer, data.getName(i));
                writer.write("\"} ");
                if (data.isTrue(i)) {
                    writer.write("1");
                } else {
                    writer.write("0");
                }

                this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
            }
        }
    }

    private void writeCountAndSum(Writer writer, MetricMetadata metadata, DistributionDataPointSnapshot data, String countSuffix, String sumSuffix, Exemplars exemplars) throws IOException {
        if (data.hasCount()) {
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), countSuffix, data.getLabels());
            writeLong(writer, data.getCount());
            if (this.exemplarsOnAllMetricTypesEnabled) {
                this.writeScrapeTimestampAndExemplar(writer, data, exemplars.getLatest());
            } else {
                this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
            }
        }

        if (data.hasSum()) {
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), sumSuffix, data.getLabels());
            writeDouble(writer, data.getSum());
            this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
        }

    }


    private void writeUnknown(Writer writer, UnknownSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "unknown", metadata);

        Iterator<UnknownSnapshot.UnknownDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            UnknownSnapshot.UnknownDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), (String)null, data.getLabels());
            writeDouble(writer, data.getValue());
            if (this.exemplarsOnAllMetricTypesEnabled) {
                this.writeScrapeTimestampAndExemplar(writer, data, data.getExemplar());
            } else {
                this.writeScrapeTimestampAndExemplar(writer, data, (Exemplar)null);
            }
        }
    }


    boolean createdTimestampsEnabled = true;
    private void writeCreated(Writer writer, MetricMetadata metadata, DataPointSnapshot data) throws IOException {
        if (this.createdTimestampsEnabled && data.hasCreatedTimestamp()) {
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_created", data.getLabels());
            writeTimestamp(writer, data.getCreatedTimestampMillis());
            if (data.hasScrapeTimestamp()) {
                writer.write(32);
                writeTimestamp(writer, data.getScrapeTimestampMillis());
            }

            writer.write(10);
        }

    }

    private void writeNameAndLabels(Writer writer, String name, String suffix, Labels labels) throws IOException {
        this.writeNameAndLabels(writer, name, suffix, labels, (String)null, 0.0);
    }

    private void writeNameAndLabels(Writer writer, String name, String suffix, Labels labels, String additionalLabelName, double additionalLabelValue) throws IOException {
        writer.write(name);
        if (suffix != null) {
            writer.write(suffix);
        }

        if (!labels.isEmpty() || additionalLabelName != null) {
            writeLabels(writer, labels, additionalLabelName, additionalLabelValue);
        }

        writer.write(32);
    }


    private void writeScrapeTimestampAndExemplar(Writer writer, DataPointSnapshot data, Exemplar exemplar) throws IOException {
        if (data.hasScrapeTimestamp()) {
            writer.write(32);
            writeTimestamp(writer, data.getScrapeTimestampMillis());
        }

        if (exemplar != null) {
            writer.write(" # ");
            writeLabels(writer, exemplar.getLabels(), (String)null, 0.0);
            writer.write(32);
            writeDouble(writer, exemplar.getValue());
            if (exemplar.hasTimestamp()) {
                writer.write(32);
                writeTimestamp(writer, exemplar.getTimestampMillis());
            }
        }

        writer.write(10);
    }

    private void writeMetadata(Writer writer, String typeName, MetricMetadata metadata) throws IOException {
        writer.write("# TYPE ");
        writer.write(metadata.getPrometheusName());
        writer.write(32);
        writer.write(typeName);
        writer.write(10);
        if (metadata.getUnit() != null) {
            writer.write("# UNIT ");
            writer.write(metadata.getPrometheusName());
            writer.write(32);
            writeEscapedLabelValue(writer, metadata.getUnit().toString());
            writer.write(10);
        }

        if (metadata.getHelp() != null && !metadata.getHelp().isEmpty()) {
            writer.write("# HELP ");
            writer.write(metadata.getPrometheusName());
            writer.write(32);
            writeEscapedLabelValue(writer, metadata.getHelp());
            writer.write(10);
        }
    }
}
