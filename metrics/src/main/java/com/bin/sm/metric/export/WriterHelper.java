package com.bin.sm.metric.export;

import io.prometheus.metrics.model.snapshots.Labels;

import java.io.IOException;
import java.io.Writer;

public class WriterHelper {


    static void writeLong(Writer writer, long value) throws IOException {
        writer.append(Long.toString(value));
    }

    static void writeDouble(Writer writer, double d) throws IOException {
        if (d == Double.POSITIVE_INFINITY) {
            writer.write("+Inf");
        } else if (d == Double.NEGATIVE_INFINITY) {
            writer.write("-Inf");
        } else {
            writer.write(Double.toString(d));
        }

    }

    static void writeTimestamp(Writer writer, long timestampMs) throws IOException {
        writer.write(Long.toString(timestampMs / 1000L));
        writer.write(".");
        long ms = timestampMs % 1000L;
        if (ms < 100L) {
            writer.write("0");
        }

        if (ms < 10L) {
            writer.write("0");
        }

        writer.write(Long.toString(ms));
    }

    static void writeEscapedLabelValue(Writer writer, String s) throws IOException {
        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\n':
                    writer.append("\\n");
                    break;
                case '"':
                    writer.append("\\\"");
                    break;
                case '\\':
                    writer.append("\\\\");
                    break;
                default:
                    writer.append(c);
            }
        }

    }

    static void writeLabels(Writer writer, Labels labels, String additionalLabelName, double additionalLabelValue) throws IOException {
        writer.write(123);

        for(int i = 0; i < labels.size(); ++i) {
            if (i > 0) {
                writer.write(",");
            }

            writer.write(labels.getPrometheusName(i));
            writer.write("=\"");
            writeEscapedLabelValue(writer, labels.getValue(i));
            writer.write("\"");
        }

        if (additionalLabelName != null) {
            if (!labels.isEmpty()) {
                writer.write(",");
            }

            writer.write(additionalLabelName);
            writer.write("=\"");
            writeDouble(writer, additionalLabelValue);
            writer.write("\"");
        }

        writer.write(125);
    }
}