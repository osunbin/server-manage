package com.bin.sm.metric.export;

import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.prometheus.metrics.model.snapshots.ClassicHistogramBuckets;
import io.prometheus.metrics.model.snapshots.CounterSnapshot;
import io.prometheus.metrics.model.snapshots.DataPointSnapshot;
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

import static com.bin.sm.metric.export.WriterHelper.writeDouble;
import static com.bin.sm.metric.export.WriterHelper.writeEscapedLabelValue;
import static com.bin.sm.metric.export.WriterHelper.writeLabels;
import static com.bin.sm.metric.export.WriterHelper.writeLong;
import static com.bin.sm.metric.export.WriterHelper.writeTimestamp;

public class PrometheusText {
    String CONTENT_TYPE = "text/plain; version=0.0.4; charset=utf-8";

    boolean writeCreatedTimestamps = false;

    public String collect() throws IOException {
        StringWriter writer = new StringWriter();
        MetricSnapshots metricSnapshots = PrometheusRegistry.defaultRegistry.scrape();
        Iterator<MetricSnapshot> iterator = metricSnapshots.iterator();
        MetricSnapshot snapshot;
        while (iterator.hasNext()) {
            snapshot = iterator.next();
            if (!snapshot.getDataPoints().isEmpty()) {
                if (snapshot instanceof CounterSnapshot) {
                    writeCounter(writer, (CounterSnapshot) snapshot);
                } else if (snapshot instanceof GaugeSnapshot) {
                    writeGauge(writer, (GaugeSnapshot) snapshot);
                } else if (snapshot instanceof HistogramSnapshot) {
                    writeHistogram(writer, (HistogramSnapshot) snapshot);
                } else if (snapshot instanceof SummarySnapshot) {
                    writeSummary(writer, (SummarySnapshot) snapshot);
                } else if (snapshot instanceof InfoSnapshot) {
                    writeInfo(writer, (InfoSnapshot) snapshot);
                } else if (snapshot instanceof StateSetSnapshot) {
                    writeStateSet(writer, (StateSetSnapshot) snapshot);
                } else if (snapshot instanceof UnknownSnapshot) {
                    writeUnknown(writer, (UnknownSnapshot) snapshot);
                }
            }
        }


        if (this.writeCreatedTimestamps) {
            iterator = metricSnapshots.iterator();

            while (iterator.hasNext()) {
                snapshot = iterator.next();
                if (snapshot.getDataPoints().size() > 0) {
                    if (snapshot instanceof CounterSnapshot) {
                        this.writeCreated(writer, snapshot);
                    } else if (snapshot instanceof HistogramSnapshot) {
                        this.writeCreated(writer, snapshot);
                    } else if (snapshot instanceof SummarySnapshot) {
                        this.writeCreated(writer, snapshot);
                    }
                }
            }
        }

        writer.flush();

       return writer.toString();
    }


    public void writeCreated(Writer writer, MetricSnapshot snapshot) throws IOException {
        boolean metadataWritten = false;
        MetricMetadata metadata = snapshot.getMetadata();
        Iterator<? extends DataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            DataPointSnapshot data = iterator.next();
            if (data.hasCreatedTimestamp()) {
                if (!metadataWritten) {
                    this.writeMetadata(writer, "_created", "gauge", metadata);
                    metadataWritten = true;
                }

                this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_created", data.getLabels());
                writeTimestamp(writer, data.getCreatedTimestampMillis());
                this.writeScrapeTimestampAndNewline(writer, data);
            }
        }
    }


    private void writeCounter(Writer writer, CounterSnapshot snapshot) throws IOException {
        if (snapshot.getDataPoints().isEmpty()) return;
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "_total", "counter", metadata);
        Iterator<CounterSnapshot.CounterDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            CounterSnapshot.CounterDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_total", data.getLabels());
            writeDouble(writer, data.getValue());
            this.writeScrapeTimestampAndNewline(writer, data);
        }
    }

    private void writeGauge(Writer writer, GaugeSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "", "gauge", metadata);
        Iterator<GaugeSnapshot.GaugeDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            GaugeSnapshot.GaugeDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), (String) null, data.getLabels());
            writeDouble(writer, data.getValue());
            this.writeScrapeTimestampAndNewline(writer, data);
        }
    }

    private void writeHistogram(Writer writer, HistogramSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        writeMetadata(writer, "", "histogram", metadata);
        Iterator<HistogramSnapshot.HistogramDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();
        while (iterator.hasNext()) {
            HistogramSnapshot.HistogramDataPointSnapshot data = iterator.next();
            ClassicHistogramBuckets buckets = getClassicBuckets(data);
            long cumulativeCount = 0L;

            for (int i = 0; i < buckets.size(); ++i) {
                cumulativeCount += buckets.getCount(i);
                writeNameAndLabels(writer, metadata.getPrometheusName(), "_bucket", data.getLabels(), "le", buckets.getUpperBound(i));
                writeLong(writer, cumulativeCount);
                writeScrapeTimestampAndNewline(writer, data);
            }

            if (!snapshot.isGaugeHistogram()) {
                if (data.hasCount()) {
                    writeNameAndLabels(writer, metadata.getPrometheusName(), "_count", data.getLabels());
                    writeLong(writer, data.getCount());
                    writeScrapeTimestampAndNewline(writer, data);
                }

                if (data.hasSum()) {
                    writeNameAndLabels(writer, metadata.getPrometheusName(), "_sum", data.getLabels());
                    writeDouble(writer, data.getSum());
                    writeScrapeTimestampAndNewline(writer, data);
                }
            }
        }

        if (snapshot.isGaugeHistogram()) {
            writeGaugeCountSum(writer, snapshot, metadata);
        }

    }


    private ClassicHistogramBuckets getClassicBuckets(HistogramSnapshot.HistogramDataPointSnapshot data) {
        return data.getClassicBuckets().isEmpty() ? ClassicHistogramBuckets.of(new double[]{Double.POSITIVE_INFINITY}, new long[]{data.getCount()}) : data.getClassicBuckets();
    }

    private void writeGaugeCountSum(Writer writer, HistogramSnapshot snapshot, MetricMetadata metadata) throws IOException {
        boolean metadataWritten = false;
        Iterator<HistogramSnapshot.HistogramDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();

        HistogramSnapshot.HistogramDataPointSnapshot data;
        while(iterator.hasNext()) {
            data = iterator.next();
            if (data.hasCount()) {
                if (!metadataWritten) {
                    this.writeMetadata(writer, "_gcount", "gauge", metadata);
                    metadataWritten = true;
                }

                writeNameAndLabels(writer, metadata.getPrometheusName(), "_gcount", data.getLabels());
                writeLong(writer, data.getCount());
                writeScrapeTimestampAndNewline(writer, data);
            }
        }

        metadataWritten = false;
        iterator = snapshot.getDataPoints().iterator();

        while(iterator.hasNext()) {
            data = iterator.next();
            if (data.hasSum()) {
                if (!metadataWritten) {
                    this.writeMetadata(writer, "_gsum", "gauge", metadata);
                    metadataWritten = true;
                }

                this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_gsum", data.getLabels());
                writeDouble(writer, data.getSum());
                this.writeScrapeTimestampAndNewline(writer, data);
            }
        }
    }

    private void writeSummary(Writer writer, SummarySnapshot snapshot) throws IOException {
        boolean metadataWritten = false;
        MetricMetadata metadata = snapshot.getMetadata();
        Iterator<SummarySnapshot.SummaryDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();

        while(true) {
            SummarySnapshot.SummaryDataPointSnapshot data;
            do {
                if (!iterator.hasNext()) {
                    return;
                }

                data = iterator.next();
            } while(data.getQuantiles().size() == 0 && !data.hasCount() && !data.hasSum());

            if (!metadataWritten) {
                this.writeMetadata(writer, "", "summary", metadata);
                metadataWritten = true;
            }

            Iterator<Quantile> iterator1 = data.getQuantiles().iterator();


            while(iterator1.hasNext()) {
                Quantile quantile = iterator1.next();
                this.writeNameAndLabels(writer, metadata.getPrometheusName(), (String)null, data.getLabels(), "quantile", quantile.getQuantile());
                writeDouble(writer, quantile.getValue());
                this.writeScrapeTimestampAndNewline(writer, data);
            }

            if (data.hasCount()) {
                this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_count", data.getLabels());
                writeLong(writer, data.getCount());
                this.writeScrapeTimestampAndNewline(writer, data);
            }

            if (data.hasSum()) {
                this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_sum", data.getLabels());
                writeDouble(writer, data.getSum());
                this.writeScrapeTimestampAndNewline(writer, data);
            }
        }
    }

    private void writeInfo(Writer writer, InfoSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "_info", "gauge", metadata);
        Iterator<InfoSnapshot.InfoDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();

        while(iterator.hasNext()) {
            InfoSnapshot.InfoDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), "_info", data.getLabels());
            writer.write("1");
            this.writeScrapeTimestampAndNewline(writer, data);
        }

    }

    private void writeStateSet(Writer writer, StateSetSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "", "gauge", metadata);
        Iterator<StateSetSnapshot.StateSetDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();

        while(iterator.hasNext()) {
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

                this.writeScrapeTimestampAndNewline(writer, data);
            }
        }

    }

    private void writeUnknown(Writer writer, UnknownSnapshot snapshot) throws IOException {
        MetricMetadata metadata = snapshot.getMetadata();
        this.writeMetadata(writer, "", "untyped", metadata);
        Iterator<UnknownSnapshot.UnknownDataPointSnapshot> iterator = snapshot.getDataPoints().iterator();

        while(iterator.hasNext()) {
            UnknownSnapshot.UnknownDataPointSnapshot data = iterator.next();
            this.writeNameAndLabels(writer, metadata.getPrometheusName(), (String)null, data.getLabels());
            writeDouble(writer, data.getValue());
            this.writeScrapeTimestampAndNewline(writer, data);
        }

    }

    private void writeNameAndLabels(Writer writer, String name, String suffix, Labels labels) throws IOException {
        this.writeNameAndLabels(writer, name, suffix, labels, (String) null, 0.0);
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

    private void writeMetadata(Writer writer, String suffix, String typeString, MetricMetadata metadata) throws IOException {
        if (metadata.getHelp() != null && !metadata.getHelp().isEmpty()) {
            writer.write("# HELP ");
            writer.write(metadata.getPrometheusName());
            if (suffix != null) {
                writer.write(suffix);
            }

            writer.write(32);
            this.writeEscapedHelp(writer, metadata.getHelp());
            writer.write(10);
        }

        writer.write("# TYPE ");
        writer.write(metadata.getPrometheusName());
        if (suffix != null) {
            writer.write(suffix);
        }

        writer.write(32);
        writer.write(typeString);
        writer.write(10);
    }

    private void writeEscapedHelp(Writer writer, String s) throws IOException {
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\n':
                    writer.append("\\n");
                    break;
                case '\\':
                    writer.append("\\\\");
                    break;
                default:
                    writer.append(c);
            }
        }

    }

    private void writeScrapeTimestampAndNewline(Writer writer, DataPointSnapshot data) throws IOException {
        if (data.hasScrapeTimestamp()) {
            writer.write(32);
            writeTimestamp(writer, data.getScrapeTimestampMillis());
        }

        writer.write(10);
    }
}
